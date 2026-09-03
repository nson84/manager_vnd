package Manager_vnd.Manager.feature.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email bắt buộc")
        @Email(message = "Email không hợp lệ")
        String email,

        @NotBlank(message = "Mật khẩu bắt buộc")
        String password
) {
}
