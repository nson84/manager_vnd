package Manager_vnd.Manager.feature.user;

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
import Manager_vnd.Manager.feature.user.dto.CreateUserRequest;
import Manager_vnd.Manager.feature.user.dto.UpdateUserRequest;
import Manager_vnd.Manager.feature.user.dto.UserResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResult<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort,
            @RequestParam(required = false) Boolean active) {
        PaginatedResult<UserResponse> result = userService.getAllUsers(page, size, sort, active);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách user thành công", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin user thành công", user));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @RequestBody @Valid CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity
                .created(URI.create("/api/v1/users/" + user.id()))
                .body(ApiResponse.created("Tạo user thành công", user));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @RequestBody @Valid UpdateUserRequest request) {
        UserResponse user = userService.updateUser(request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật user thành công", user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> disableUser(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Vô hiệu hóa user thành công",
                userService.disableUser(id)));
    }

    @PostMapping("/{id}/enable")
    public ResponseEntity<ApiResponse<UserResponse>> enableUser(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Kích hoạt user thành công",
                userService.enableUser(id)));
    }
}
