package Manager_vnd.Manager.feature.permission;

import java.util.List;

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
import Manager_vnd.Manager.feature.permission.dto.CreatePermissionRequest;
import Manager_vnd.Manager.feature.permission.dto.PermissionResponse;
import Manager_vnd.Manager.feature.permission.dto.UpdatePermissionRequest;
import Manager_vnd.Manager.feature.role.Role;
import Manager_vnd.Manager.feature.role.RoleRepository;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public PermissionServiceImpl(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<PermissionResponse> getAll(int page, int size, String sort) {
        Page<Permission> result = permissionRepository.findAll(toPageable(page, size, sort));
        List<PermissionResponse> items = result.getContent().stream()
                .map(PermissionResponse::fromEntity)
                .toList();
        return new PaginatedResult<>(toMeta(result), items);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getById(long id) {
        return PermissionResponse.fromEntity(findPermission(id));
    }

    @Override
    @Transactional
    public PermissionResponse create(CreatePermissionRequest request) {
        String apiPath = request.apiPath().trim();
        String method = request.method().trim().toUpperCase();
        assertUniquePathMethod(apiPath, method, null);
        Permission permission = new Permission();
        permission.setName(request.name().trim());
        permission.setApiPath(apiPath);
        permission.setMethod(method);
        permission.setModule(request.module().trim());
        return PermissionResponse.fromEntity(permissionRepository.save(permission));
    }

    @Override
    @Transactional
    public PermissionResponse update(UpdatePermissionRequest request) {
        Permission permission = findPermission(request.id());
        applyName(permission, request.name());
        applyModule(permission, request.module());
        applyPathAndMethod(permission, request.apiPath(), request.method());
        return PermissionResponse.fromEntity(permissionRepository.save(permission));
    }

    @Override
    @Transactional
    public void delete(long id) {
        Permission permission = findPermission(id);
        List<Role> roles = roleRepository.findByPermissions_Id(id);
        for (Role role : roles) {
            role.getPermissions().removeIf(item -> item.getId() == id);
        }
        permissionRepository.delete(permission);
    }

    private void applyName(Permission permission, String name) {
        if (name == null) {
            return;
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidRequestException("Tên permission không được để trống");
        }
        permission.setName(trimmed);
    }

    private void applyModule(Permission permission, String module) {
        if (module == null) {
            return;
        }
        String trimmed = module.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidRequestException("Module không được để trống");
        }
        permission.setModule(trimmed);
    }

    private void applyPathAndMethod(Permission permission, String apiPath, String method) {
        String path = apiPath == null ? permission.getApiPath() : apiPath.trim();
        String httpMethod = method == null ? permission.getMethod() : method.trim().toUpperCase();
        if (path.isEmpty()) {
            throw new InvalidRequestException("apiPath không được để trống");
        }
        assertUniquePathMethod(path, httpMethod, permission.getId());
        permission.setApiPath(path);
        permission.setMethod(httpMethod);
    }

    private void assertUniquePathMethod(String apiPath, String method, Long excludeId) {
        boolean duplicate = excludeId == null
                ? permissionRepository.existsByApiPathAndMethod(apiPath, method)
                : permissionRepository.existsByApiPathAndMethodAndIdNot(apiPath, method, excludeId);
        if (duplicate) {
            throw new ConflictException("apiPath + method đã tồn tại");
        }
    }

    private Permission findPermission(long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", id));
    }

    private PaginationMeta toMeta(Page<Permission> result) {
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
