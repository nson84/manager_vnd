package Manager_vnd.Manager.feature.user.dto;

import java.util.List;

import Manager_vnd.Manager.feature.user.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be 8-100 characters")
        String password,

        Integer age,

        Gender gender,

        String address,

        Long companyId,

        List<Long> roleIds
) {
}
