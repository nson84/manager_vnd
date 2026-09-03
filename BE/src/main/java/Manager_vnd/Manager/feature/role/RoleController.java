package Manager_vnd.Manager.feature.role;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Manager_vnd.Manager.dto.ApiResponse;
import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.feature.role.dto.CreateRoleRequest;
import Manager_vnd.Manager.feature.role.dto.RoleResponse;
import Manager_vnd.Manager.feature.role.dto.UpdateRoleRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResult<RoleResponse>>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy danh sách role thành công",
                roleService.getAll(page, size, sort)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> getById(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy thông tin role thành công",
                roleService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> create(@RequestBody @Valid CreateRoleRequest request) {
        RoleResponse created = roleService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/roles/" + created.id()))
                .body(ApiResponse.created("Tạo role thành công", created));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<RoleResponse>> update(@RequestBody @Valid UpdateRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cập nhật role thành công",
                roleService.update(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable long id) {
        roleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa role thành công", null));
    }
}
