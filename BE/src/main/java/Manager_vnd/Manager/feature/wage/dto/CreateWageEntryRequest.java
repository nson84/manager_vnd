package Manager_vnd.Manager.feature.wage.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import Manager_vnd.Manager.feature.worker.WageType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateWageEntryRequest(
        @NotNull(message = "workerId bắt buộc")
        Long workerId,

        @NotNull(message = "Ngày làm bắt buộc")
        LocalDate workDate,

        WageType wageType,

        @NotNull(message = "Số lượng bắt buộc")
        @DecimalMin(value = "0.0", inclusive = false, message = "Số lượng phải > 0")
        BigDecimal quantity,

        @DecimalMin(value = "0.0", inclusive = false, message = "Đơn giá phải > 0")
        BigDecimal unitRate,

        @Size(max = 500)
        String note
) {
}
