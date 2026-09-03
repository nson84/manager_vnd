package Manager_vnd.Manager.feature.payslip.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePayslipRequest(
        @NotNull(message = "workerId bắt buộc")
        Long workerId,

        @NotNull(message = "periodStart bắt buộc")
        LocalDate periodStart,

        @NotNull(message = "periodEnd bắt buộc")
        LocalDate periodEnd,

        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal advanceDeducted,

        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal otherDeduction,

        @Size(max = 500)
        String note
) {
}
