package Manager_vnd.Manager.feature.permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import Manager_vnd.Manager.exception.ConflictException;
import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.permission.dto.CreatePermissionRequest;
import Manager_vnd.Manager.feature.permission.dto.UpdatePermissionRequest;
import Manager_vnd.Manager.feature.role.Role;
import Manager_vnd.Manager.feature.role.RoleRepository;

@ExtendWith(MockitoExtension.class)
class PermissionServiceImplTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    @Test
    @DisplayName("Tạo permission thành công, method uppercase")
    void create_success() {
        CreatePermissionRequest request = new CreatePermissionRequest(
                "CREATE_USER", "/api/v1/users", "post", "USER");
        when(permissionRepository.existsByApiPathAndMethod("/api/v1/users", "POST")).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenAnswer(inv -> {
            Permission permission = inv.getArgument(0);
            permission.setId(1L);
            return permission;
        });

        var result = permissionService.create(request);

        assertEquals(1L, result.id());
        assertEquals("POST", result.method());
        assertEquals("/api/v1/users", result.apiPath());
    }

    @Test
    @DisplayName("Tạo permission trùng apiPath + method bị conflict")
    void create_duplicatePathMethod_throwsConflict() {
        when(permissionRepository.existsByApiPathAndMethod("/api/v1/users", "POST")).thenReturn(true);

        assertThrows(ConflictException.class, () -> permissionService.create(
                new CreatePermissionRequest("CREATE_USER", "/api/v1/users", "POST", "USER")));
        verify(permissionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get by id thành công")
    void getById_success() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission(1L)));

        var result = permissionService.getById(1L);

        assertEquals("CREATE_USER", result.name());
    }

    @Test
    @DisplayName("Get by id không tìm thấy")
    void getById_notFound() {
        when(permissionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> permissionService.getById(99L));
    }

    @Test
    @DisplayName("List trả về danh sách")
    void getAll_returnsList() {
        when(permissionRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(permission(1L))));

        var result = permissionService.getAll(1, 10, "id,asc");

        assertEquals(1, result.result().size());
        assertEquals(1, result.meta().total());
    }

    @Test
    @DisplayName("List rỗng")
    void getAll_empty() {
        when(permissionRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        var result = permissionService.getAll(1, 10, "id,asc");

        assertEquals(0, result.result().size());
    }

    @Test
    @DisplayName("Update thành công")
    void update_success() {
        Permission existing = permission(1L);
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(permissionRepository.existsByApiPathAndMethodAndIdNot("/api/v1/users", "PUT", 1L))
                .thenReturn(false);
        when(permissionRepository.save(existing)).thenReturn(existing);

        var result = permissionService.update(
                new UpdatePermissionRequest(1L, "UPDATE_USER", "/api/v1/users", "PUT", "USER"));

        assertEquals("UPDATE_USER", result.name());
        assertEquals("PUT", result.method());
    }

    @Test
    @DisplayName("Update không tìm thấy")
    void update_notFound() {
        when(permissionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> permissionService.update(
                new UpdatePermissionRequest(99L, "X", null, null, null)));
    }

    @Test
    @DisplayName("Update trùng apiPath + method bị conflict")
    void update_duplicatePathMethod_throwsConflict() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission(1L)));
        when(permissionRepository.existsByApiPathAndMethodAndIdNot("/api/v1/roles", "GET", 1L))
                .thenReturn(true);

        assertThrows(ConflictException.class, () -> permissionService.update(
                new UpdatePermissionRequest(1L, null, "/api/v1/roles", "GET", null)));
        verify(permissionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Xóa permission gỡ khỏi role rồi xóa")
    void delete_success() {
        Permission permission = permission(4L);
        Role role = new Role();
        role.setId(2L);
        role.getPermissions().add(permission);
        when(permissionRepository.findById(4L)).thenReturn(Optional.of(permission));
        when(roleRepository.findByPermissions_Id(4L)).thenReturn(List.of(role));

        permissionService.delete(4L);

        assertEquals(0, role.getPermissions().size());
        verify(permissionRepository).delete(permission);
    }

    @Test
    @DisplayName("Xóa permission không tìm thấy")
    void delete_notFound() {
        when(permissionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> permissionService.delete(99L));
        verify(permissionRepository, never()).delete(any());
    }

    private Permission permission(long id) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setName("CREATE_USER");
        permission.setApiPath("/api/v1/users");
        permission.setMethod("POST");
        permission.setModule("USER");
        return permission;
    }
}
