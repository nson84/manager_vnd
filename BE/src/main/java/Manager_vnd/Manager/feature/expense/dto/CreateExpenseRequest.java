package Manager_vnd.Manager.feature.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateExpenseRequest(
        @NotNull(message = "categoryId bắt buộc")
        Long categoryId,

        @NotNull(message = "Số tiền bắt buộc")
        @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền phải > 0")
        BigDecimal amount,

        @NotNull(message = "Ngày chi bắt buộc")
        LocalDate expenseDate,

        @Size(max = 500)
        String note
) {
}
