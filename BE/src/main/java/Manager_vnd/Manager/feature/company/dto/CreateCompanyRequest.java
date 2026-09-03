package Manager_vnd.Manager.feature.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCompanyRequest(
        @NotBlank(message = "Tên công ty bắt buộc")
        @Size(max = 255, message = "Tên tối đa 255 ký tự")
        String name,

        String description,

        @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
        String address,

        @Size(max = 255, message = "Logo tối đa 255 ký tự")
        String logo
) {
}
