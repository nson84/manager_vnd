package Manager_vnd.Manager.feature.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.dto.PaginationMeta;
import Manager_vnd.Manager.exception.ConflictException;
import Manager_vnd.Manager.exception.InvalidRequestException;
import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.permission.Permission;
import Manager_vnd.Manager.feature.permission.PermissionRepository;
import Manager_vnd.Manager.feature.role.dto.CreateRoleRequest;
import Manager_vnd.Manager.feature.role.dto.RoleResponse;
import Manager_vnd.Manager.feature.role.dto.UpdateRoleRequest;
import Manager_vnd.Manager.feature.user.User;
import Manager_vnd.Manager.feature.user.UserRepository;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Set<String> SYSTEM_ROLE_NAMES = Set.of("ADMIN", "USER");

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    public RoleServiceImpl(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<RoleResponse> getAll(int page, int size, String sort) {
        Page<Role> result = roleRepository.findAll(toPageable(page, size, sort));
        List<RoleResponse> items = result.getContent().stream().map(RoleResponse::fromEntity).toList();
        return new PaginatedResult<>(toMeta(result), items);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getById(long id) {
        return RoleResponse.fromEntity(findRole(id));
    }

    @Override
    @Transactional
    public RoleResponse create(CreateRoleRequest request) {
        String name = request.name().trim();
        if (roleRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("Tên role đã tồn tại");
        }
        Role role = new Role();
        role.setName(name);
        role.setDescription(request.description());
        role.setPermissions(resolvePermissions(request.permissionIds()));
        return RoleResponse.fromEntity(roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleResponse update(UpdateRoleRequest request) {
        Role role = findRole(request.id());
        if (request.name() != null) {
            applyName(role, request.name());
        }
        if (request.description() != null) {
            role.setDescription(request.description());
        }
        if (request.permissionIds() != null) {
            role.setPermissions(resolvePermissions(request.permissionIds()));
        }
        return RoleResponse.fromEntity(roleRepository.save(role));
    }

    @Override
    @Transactional
    public void delete(long id) {
        Role role = findRole(id);
        if (isSystemRole(role.getName())) {
            throw new ConflictException("Không thể xóa role hệ thống " + role.getName());
        }
        unlinkFromUsers(id);
        role.getPermissions().clear();
        roleRepository.delete(role);
    }

    private void applyName(Role role, String rawName) {
        String name = rawName.trim();
        if (name.isEmpty()) {
            throw new InvalidRequestException("Tên role không được để trống");
        }
        if (isSystemRole(role.getName()) && !role.getName().equalsIgnoreCase(name)) {
            throw new ConflictException("Không thể đổi tên role hệ thống");
        }
        if (roleRepository.existsByNameIgnoreCaseAndIdNot(name, role.getId())) {
            throw new ConflictException("Tên role đã tồn tại");
        }
        role.setName(name);
    }

    private List<Permission> resolvePermissions(List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> distinctIds = permissionIds.stream().distinct().toList();
        List<Permission> permissions = permissionRepository.findAllById(distinctIds);
        if (permissions.size() != distinctIds.size()) {
            throw new ResourceNotFoundException("Permission", "id", distinctIds);
        }
        return new ArrayList<>(permissions);
    }

    private void unlinkFromUsers(long roleId) {
        List<User> users = userRepository.findByRoles_Id(roleId);
        for (User user : users) {
            user.getRoles().removeIf(item -> item.getId() == roleId);
        }
    }

    private boolean isSystemRole(String name) {
        return name != null && SYSTEM_ROLE_NAMES.contains(name.toUpperCase());
    }

    private Role findRole(long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
    }

    private PaginationMeta toMeta(Page<Role> result) {
        return new PaginationMeta(
                result.getNumber() + 1,
                result.getSize(),
                result.getTotalPages(),
                result.getTotalElements());
    }

    private Pageable toPageable(int page, int size, String sort) {
        int zeroBased = Math.max(page - 1, 0);
        return PageRequest.of(zeroBased, size, parseSort(sort));
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by("id").ascending();
        }
        String[] parts = sort.split(",");
        if (parts.length == 2) {
            return Sort.by(Sort.Direction.fromString(parts[1].trim()), parts[0].trim());
        }
        return Sort.by("id").ascending();
    }
}
