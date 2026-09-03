package Manager_vnd.Manager.feature.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull(message = "Công ty bắt buộc")
        Long companyId,

        @NotBlank(message = "Email bắt buộc")
        @Email(message = "Email không hợp lệ")
        String email,

        @NotBlank(message = "Mật khẩu bắt buộc")
        String password
) {
}
