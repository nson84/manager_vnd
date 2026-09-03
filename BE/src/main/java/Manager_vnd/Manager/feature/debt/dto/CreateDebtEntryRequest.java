package Manager_vnd.Manager.feature.debt.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import Manager_vnd.Manager.feature.debt.DebtEntryType;
import Manager_vnd.Manager.feature.debt.LedgerDirection;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateDebtEntryRequest(
        Long customerId,
        Long workerId,

        @NotNull(message = "Loại bút toán bắt buộc")
        DebtEntryType entryType,

        LedgerDirection direction,

        @NotNull(message = "Số tiền bắt buộc")
        @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền phải > 0")
        BigDecimal amount,

        @NotNull(message = "Ngày bắt buộc")
        LocalDate entryDate,

        @Size(max = 500)
        String note
) {
}
