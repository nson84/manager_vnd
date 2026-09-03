package Manager_vnd.Manager.feature.worker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import Manager_vnd.Manager.feature.worker.WageType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateWorkerRequest(
        @NotBlank(message = "Tên thợ bắt buộc")
        @Size(max = 100)
        String name,

        @Size(max = 20)
        String phone,

        @Size(max = 255)
        String address,

        @Size(max = 100)
        String jobTitle,

        @NotNull(message = "Loại công bắt buộc")
        WageType wageType,

        @NotNull(message = "Đơn giá mặc định bắt buộc")
        @DecimalMin(value = "0.0", inclusive = false, message = "Đơn giá phải > 0")
        BigDecimal defaultUnitRate,

        LocalDate hireDate,

        @Size(max = 500)
        String note
) {
}
