package Manager_vnd.Manager.feature.permission.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdatePermissionRequest(
        @NotNull(message = "Id bắt buộc")
        Long id,

        @Size(max = 100, message = "Tên tối đa 100 ký tự")
        String name,

        @Size(max = 255, message = "apiPath tối đa 255 ký tự")
        String apiPath,

        @Pattern(regexp = "(?i)GET|POST|PUT|PATCH|DELETE", message = "Method phải là GET, POST, PUT, PATCH hoặc DELETE")
        String method,

        @Size(max = 100, message = "Module tối đa 100 ký tự")
        String module
) {
}
