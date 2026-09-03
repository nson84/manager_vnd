package Manager_vnd.Manager.feature.payslip.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdatePayslipRequest(
        @NotNull(message = "Id bắt buộc")
        Long id,

        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal advanceDeducted,

        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal otherDeduction,

        @Size(max = 500)
        String note
) {
}
