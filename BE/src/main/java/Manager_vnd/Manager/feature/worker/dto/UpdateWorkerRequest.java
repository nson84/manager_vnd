package Manager_vnd.Manager.feature.worker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import Manager_vnd.Manager.feature.worker.WageType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateWorkerRequest(
        @NotNull(message = "Id bắt buộc")
        Long id,

        @Size(max = 100)
        String name,

        @Size(max = 20)
        String phone,

        @Size(max = 255)
        String address,

        @Size(max = 100)
        String jobTitle,

        WageType wageType,

        @DecimalMin(value = "0.0", inclusive = false, message = "Đơn giá phải > 0")
        BigDecimal defaultUnitRate,

        LocalDate hireDate,

        @Size(max = 500)
        String note
) {
}
