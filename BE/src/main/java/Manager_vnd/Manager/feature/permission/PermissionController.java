package Manager_vnd.Manager.feature.permission;

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
import Manager_vnd.Manager.feature.permission.dto.CreatePermissionRequest;
import Manager_vnd.Manager.feature.permission.dto.PermissionResponse;
import Manager_vnd.Manager.feature.permission.dto.UpdatePermissionRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/permissions")
@PreAuthorize("hasRole('ADMIN')")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResult<PermissionResponse>>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy danh sách permission thành công",
                permissionService.getAll(page, size, sort)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> getById(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy thông tin permission thành công",
                permissionService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PermissionResponse>> create(
            @RequestBody @Valid CreatePermissionRequest request) {
        PermissionResponse created = permissionService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/permissions/" + created.id()))
                .body(ApiResponse.created("Tạo permission thành công", created));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<PermissionResponse>> update(
            @RequestBody @Valid UpdatePermissionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cập nhật permission thành công",
                permissionService.update(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable long id) {
        permissionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa permission thành công", null));
    }
}
