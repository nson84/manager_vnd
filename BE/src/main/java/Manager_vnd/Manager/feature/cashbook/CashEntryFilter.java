package Manager_vnd.Manager.feature.cashbook;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CashEntryFilter(
        LocalDate fromDate,
        LocalDate toDate,
        CashDirection direction,
        Long categoryId,
        CashRefType refType,
        Long refId,
        Long createdBy,
        Boolean checked,
        BigDecimal amountMin,
        BigDecimal amountMax,
        String q
) {
}
