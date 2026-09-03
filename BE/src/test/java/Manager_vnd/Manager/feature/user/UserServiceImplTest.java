package Manager_vnd.Manager.feature.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import Manager_vnd.Manager.dto.PaginatedResult;
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

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Should return paginated users")
    void getAllUsers_returnsPaginatedResult() {
        User user = buildUser(1L, "Test User", "test@test.com");
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        PaginatedResult<UserResponse> result = userService.getAllUsers(1, 10, "id,asc", null);

        assertEquals(1, result.result().size());
        assertEquals("test@test.com", result.result().get(0).email());
        assertEquals(1, result.meta().page());
    }

    @Test
    @DisplayName("Should return empty paginated result when no users exist")
    void getAllUsers_empty_returnsEmptyResult() {
        Page<User> page = new PageImpl<>(List.of());
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        PaginatedResult<UserResponse> result = userService.getAllUsers(1, 10, "id,asc", null);

        assertEquals(0, result.result().size());
        assertEquals(0, result.meta().total());
    }

    @Test
    @DisplayName("Should return user when found by id")
    void getUserById_found_returnsUserResponse() {
        User user = buildUser(1L, "Test User", "test@test.com");
        when(userRepository.findWithDetailsById(1L)).thenReturn(Optional.of(user));

        UserResponse result = userService.getUserById(1L);

        assertEquals(1L, result.id());
        assertEquals("test@test.com", result.email());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found")
    void getUserById_notFound_throwsException() {
        when(userRepository.findWithDetailsById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    @DisplayName("Should create user when email is unique")
    void createUser_success_returnsUserResponse() {
        CreateUserRequest request = new CreateUserRequest(
                "New User", "new@test.com", "password123", 25, Gender.MALE,
                "Address", 1L, List.of(1L));

        Company company = new Company();
        company.setId(1L);
        company.setName("Test Co");

        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");

        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(roleRepository.findAllById(List.of(1L))).thenReturn(List.of(role));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });

        UserResponse result = userService.createUser(request);

        assertEquals(2L, result.id());
        assertEquals("new@test.com", result.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when email already exists")
    void createUser_duplicateEmail_throwsConflictException() {
        CreateUserRequest request = new CreateUserRequest(
                "User", "exists@test.com", "password123", null, null, null, null, null);
        when(userRepository.existsByEmail("exists@test.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when company not found on create")
    void createUser_companyNotFound_throwsException() {
        CreateUserRequest request = new CreateUserRequest(
                "User", "user@test.com", "password123", null, null, null, 99L, null);
        when(userRepository.existsByEmail("user@test.com")).thenReturn(false);
        when(companyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.createUser(request));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when role not found on create")
    void createUser_roleNotFound_throwsException() {
        CreateUserRequest request = new CreateUserRequest(
                "User", "user@test.com", "password123", null, null, null, null, List.of(99L));
        when(userRepository.existsByEmail("user@test.com")).thenReturn(false);
        when(roleRepository.findAllById(List.of(99L))).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> userService.createUser(request));
    }

    @Test
    @DisplayName("Should update user when found")
    void updateUser_success_returnsUpdatedUser() {
        User user = buildUser(1L, "Old Name", "test@test.com");
        UpdateUserRequest request = new UpdateUserRequest(
                1L, "New Name", 30, Gender.FEMALE, "New Address", null, null, null);
        when(userRepository.findWithDetailsById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponse result = userService.updateUser(request);

        assertEquals("New Name", result.name());
        assertEquals(30, result.age());
        assertEquals(Gender.FEMALE, result.gender());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent user")
    void updateUser_notFound_throwsException() {
        UpdateUserRequest request = new UpdateUserRequest(
                1L, "Name", null, null, null, null, null, null);
        when(userRepository.findWithDetailsById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(request));
    }

    @Test
    @DisplayName("Disable user sets active=false and revokes tokens")
    void disableUser_success_revokesTokensWithoutDelete() {
        User user = buildUser(1L, "Test", "test@test.com");
        user.setActive(true);
        when(userRepository.findWithDetailsById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        var result = userService.disableUser(1L);

        assertFalse(result.active());
        verify(refreshTokenRepository).revokeAllByUserId(1L);
        verify(refreshTokenRepository, never()).deleteByUserId(anyLong());
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when disabling non-existent user")
    void disableUser_notFound_throwsException() {
        when(userRepository.findWithDetailsById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.disableUser(1L));
        verify(refreshTokenRepository, never()).revokeAllByUserId(anyLong());
    }

    @Test
    @DisplayName("Enable user sets active=true")
    void enableUser_success() {
        User user = buildUser(1L, "Test", "test@test.com");
        user.setActive(false);
        when(userRepository.findWithDetailsById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        var result = userService.enableUser(1L);

        assertTrue(result.active());
    }

    private User buildUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setActive(true);
        return user;
    }
}
