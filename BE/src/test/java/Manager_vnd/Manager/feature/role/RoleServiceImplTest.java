package Manager_vnd.Manager.feature.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import Manager_vnd.Manager.feature.permission.Permission;
import Manager_vnd.Manager.feature.permission.PermissionRepository;
import Manager_vnd.Manager.feature.role.dto.CreateRoleRequest;
import Manager_vnd.Manager.feature.role.dto.UpdateRoleRequest;
import Manager_vnd.Manager.feature.user.User;
import Manager_vnd.Manager.feature.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    @DisplayName("Tạo role thành công kèm permission")
    void create_success() {
        Permission permission = permission(1L);
        when(roleRepository.existsByNameIgnoreCase("HR")).thenReturn(false);
        when(permissionRepository.findAllById(List.of(1L))).thenReturn(List.of(permission));
        when(roleRepository.save(any(Role.class))).thenAnswer(inv -> {
            Role role = inv.getArgument(0);
            role.setId(3L);
            return role;
        });

        var result = roleService.create(new CreateRoleRequest("HR", "Human resources", List.of(1L)));

        assertEquals(3L, result.id());
        assertEquals("HR", result.name());
        assertEquals(1, result.permissions().size());
    }

    @Test
    @DisplayName("Tạo role trùng tên bị conflict")
    void create_duplicateName_throwsConflict() {
        when(roleRepository.existsByNameIgnoreCase("HR")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> roleService.create(new CreateRoleRequest("HR", null, List.of())));
        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Tạo role với permission không tồn tại")
    void create_permissionNotFound() {
        when(roleRepository.existsByNameIgnoreCase("HR")).thenReturn(false);
        when(permissionRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(permission(1L)));

        assertThrows(ResourceNotFoundException.class,
                () -> roleService.create(new CreateRoleRequest("HR", null, List.of(1L, 2L))));
    }

    @Test
    @DisplayName("Get by id thành công")
    void getById_success() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role(1L, "ADMIN")));

        var result = roleService.getById(1L);

        assertEquals("ADMIN", result.name());
    }

    @Test
    @DisplayName("Get by id không tìm thấy")
    void getById_notFound() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> roleService.getById(99L));
    }

    @Test
    @DisplayName("List trả về danh sách")
    void getAll_returnsList() {
        when(roleRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(role(1L, "ADMIN"))));

        var result = roleService.getAll(1, 10, "id,asc");

        assertEquals(1, result.result().size());
    }

    @Test
    @DisplayName("List rỗng")
    void getAll_empty() {
        when(roleRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        var result = roleService.getAll(1, 10, "id,asc");

        assertEquals(0, result.result().size());
    }

    @Test
    @DisplayName("Update thành công, permissionIds thay toàn bộ list")
    void update_success() {
        Role existing = role(3L, "HR");
        existing.getPermissions().add(permission(1L));
        when(roleRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(permissionRepository.findAllById(List.of(2L))).thenReturn(List.of(permission(2L)));
        when(roleRepository.save(existing)).thenReturn(existing);

        var result = roleService.update(new UpdateRoleRequest(3L, "HR Lead", "Updated", List.of(2L)));

        assertEquals("HR Lead", result.name());
        assertEquals(1, result.permissions().size());
        assertEquals(2L, result.permissions().get(0).id());
    }

    @Test
    @DisplayName("Update không tìm thấy")
    void update_notFound() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> roleService.update(new UpdateRoleRequest(99L, "X", null, null)));
    }

    @Test
    @DisplayName("Update trùng tên bị conflict")
    void update_duplicateName_throwsConflict() {
        when(roleRepository.findById(3L)).thenReturn(Optional.of(role(3L, "HR")));
        when(roleRepository.existsByNameIgnoreCaseAndIdNot("ADMIN", 3L)).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> roleService.update(new UpdateRoleRequest(3L, "ADMIN", null, null)));
        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Không cho đổi tên role hệ thống")
    void update_systemRoleRename_throwsConflict() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role(1L, "ADMIN")));

        assertThrows(ConflictException.class,
                () -> roleService.update(new UpdateRoleRequest(1L, "SUPERADMIN", null, null)));
    }

    @Test
    @DisplayName("Xóa role gỡ khỏi user rồi xóa")
    void delete_success() {
        Role role = role(3L, "HR");
        User user = new User();
        user.setId(9L);
        user.setRoles(new ArrayList<>(List.of(role)));
        when(roleRepository.findById(3L)).thenReturn(Optional.of(role));
        when(userRepository.findByRoles_Id(3L)).thenReturn(List.of(user));

        roleService.delete(3L);

        assertTrue(user.getRoles().isEmpty());
        verify(roleRepository).delete(role);
    }

    @Test
    @DisplayName("Không xóa được role hệ thống")
    void delete_systemRole_throwsConflict() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role(1L, "ADMIN")));

        assertThrows(ConflictException.class, () -> roleService.delete(1L));
        verify(roleRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Xóa role không tìm thấy")
    void delete_notFound() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> roleService.delete(99L));
    }

    private Role role(long id, String name) {
        Role role = new Role();
        role.setId(id);
        role.setName(name);
        return role;
    }

    private Permission permission(long id) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setName("P" + id);
        permission.setApiPath("/api/v1/x");
        permission.setMethod("GET");
        permission.setModule("USER");
        return permission;
    }
}
