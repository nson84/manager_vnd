package Manager_vnd.Manager.feature.permission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatePermissionRequest(
        @NotBlank(message = "Tên permission bắt buộc")
        @Size(max = 100, message = "Tên tối đa 100 ký tự")
        String name,

        @NotBlank(message = "apiPath bắt buộc")
        @Size(max = 255, message = "apiPath tối đa 255 ký tự")
        String apiPath,

        @NotBlank(message = "HTTP method bắt buộc")
        @Pattern(regexp = "(?i)GET|POST|PUT|PATCH|DELETE", message = "Method phải là GET, POST, PUT, PATCH hoặc DELETE")
        String method,

        @NotBlank(message = "Module bắt buộc")
        @Size(max = 100, message = "Module tối đa 100 ký tự")
        String module
) {
}
