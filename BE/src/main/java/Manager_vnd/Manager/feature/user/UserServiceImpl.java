package Manager_vnd.Manager.feature.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.dto.PaginationMeta;
import Manager_vnd.Manager.exception.ConflictException;
import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.auth.RefreshTokenRepository;
import Manager_vnd.Manager.feature.company.Company;
import Manager_vnd.Manager.feature.company.CompanyRepository;
import Manager_vnd.Manager.feature.role.Role;
import Manager_vnd.Manager.feature.role.RoleRepository;
import Manager_vnd.Manager.feature.user.dto.CreateUserRequest;
import Manager_vnd.Manager.feature.user.dto.UpdateUserRequest;
import Manager_vnd.Manager.feature.user.dto.UserResponse;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            RoleRepository roleRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<UserResponse> getAllUsers(int page, int size, String sort, Boolean active) {
        Pageable pageable = toPageable(page, size, sort);
        Page<User> userPage = active == null
                ? userRepository.findAll(pageable)
                : userRepository.findByActive(active, pageable);
        List<UserResponse> users = userPage.getContent().stream()
                .map(UserResponse::fromEntity)
                .toList();
        PaginationMeta meta = new PaginationMeta(
                userPage.getNumber() + 1,
                userPage.getSize(),
                userPage.getTotalPages(),
                userPage.getTotalElements());
        return new PaginatedResult<>(meta, users);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(long id) {
        User user = userRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email đã tồn tại");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setAge(request.age());
        user.setGender(request.gender());
        user.setAddress(request.address());
        user.setActive(true);
        user.setCompany(resolveCompany(request.companyId()));
        user.setRoles(resolveRoles(request.roleIds()));

        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UpdateUserRequest request) {
        User user = userRepository.findWithDetailsById(request.id())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.id()));

        if (request.name() != null) {
            user.setName(request.name());
        }
        if (request.age() != null) {
            user.setAge(request.age());
        }
        if (request.gender() != null) {
            user.setGender(request.gender());
        }
        if (request.address() != null) {
            user.setAddress(request.address());
        }
        if (request.avatar() != null) {
            user.setAvatar(request.avatar());
        }
        if (request.companyId() != null) {
            user.setCompany(resolveCompany(request.companyId()));
        }
        if (request.roleIds() != null) {
            user.setRoles(resolveRoles(request.roleIds()));
        }

        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public UserResponse disableUser(long id) {
        User user = userRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        if (!user.isActive()) {
            throw new ConflictException("User đã bị vô hiệu hóa");
        }
        user.setActive(false);
        refreshTokenRepository.revokeAllByUserId(id);
        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse enableUser(long id) {
        User user = userRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        if (user.isActive()) {
            throw new ConflictException("User đang hoạt động");
        }
        user.setActive(true);
        return UserResponse.fromEntity(userRepository.save(user));
    }

    private Company resolveCompany(Long companyId) {
        if (companyId == null) {
            return null;
        }
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
    }

    private List<Role> resolveRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Role> roles = roleRepository.findAllById(roleIds);
        if (roles.size() != roleIds.size()) {
            throw new ResourceNotFoundException("Role", "id", roleIds);
        }
        return roles;
    }

    private Pageable toPageable(int page, int size, String sort) {
        int zeroBasedPage = Math.max(page - 1, 0);
        return PageRequest.of(zeroBasedPage, size, parseSort(sort));
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by("id").ascending();
        }
        String[] parts = sort.split(",");
        if (parts.length == 2) {
            Sort.Direction direction = Sort.Direction.fromString(parts[1].trim());
            return Sort.by(direction, parts[0].trim());
        }
        return Sort.by("id").ascending();
    }
}
