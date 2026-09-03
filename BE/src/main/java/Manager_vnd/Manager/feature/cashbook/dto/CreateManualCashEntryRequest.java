package Manager_vnd.Manager.feature.cashbook.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import Manager_vnd.Manager.feature.cashbook.CashDirection;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record CreateManualCashEntryRequest(
        LocalDate entryDate,

        @NotNull(message = "Chiều thu/chi bắt buộc")
        CashDirection direction,

        @NotNull(message = "Số tiền bắt buộc")
        @DecimalMin(value = "0.01", message = "Số tiền phải lớn hơn 0")
        BigDecimal amount,

        @NotNull(message = "Loại thu/chi bắt buộc")
        Long categoryId,

        String description,

        String note
) {
}
