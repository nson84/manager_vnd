package Manager_vnd.Manager.feature.customer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(
        @NotNull(message = "Id bắt buộc")
        Long id,

        @Size(max = 100, message = "Tên tối đa 100 ký tự")
        String name,

        @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
        String phone,

        @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
        String address,

        @Size(max = 500, message = "Ghi chú tối đa 500 ký tự")
        String note
) {
}
