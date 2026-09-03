package Manager_vnd.Manager.feature.cashbook.dto;

import java.math.BigDecimal;
import java.util.List;

import Manager_vnd.Manager.feature.cashbook.CashDirection;

public record CashStatsResponse(
        BigDecimal totalIn,
        BigDecimal totalOut,
        BigDecimal balance,
        long countIn,
        long countOut,
        List<CategoryStat> byCategory
) {
    public record CategoryStat(
            long categoryId,
            String categoryName,
            CashDirection direction,
            BigDecimal total
    ) {
    }
}
