package Manager_vnd.Manager.feature.role.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoleRequest(
        @NotBlank(message = "Tên role bắt buộc")
        @Size(max = 100, message = "Tên tối đa 100 ký tự")
        String name,

        @Size(max = 255, message = "Mô tả tối đa 255 ký tự")
        String description,

        List<Long> permissionIds
) {
}
