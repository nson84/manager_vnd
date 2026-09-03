package Manager_vnd.Manager.feature.cashbook.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import Manager_vnd.Manager.feature.cashbook.CashDirection;
import Manager_vnd.Manager.feature.cashbook.CashEntry;
import Manager_vnd.Manager.feature.cashbook.CashRefType;
import Manager_vnd.Manager.feature.expense.ExpenseCategory;
import Manager_vnd.Manager.feature.user.User;

public record CashEntryResponse(
        long id,
        LocalDate entryDate,
        CashDirection direction,
        BigDecimal amount,
        CategorySummary category,
        String description,
        String note,
        boolean checked,
        Instant checkedAt,
        UserSummary checkedBy,
        CashRefType refType,
        Long refId,
        UserSummary createdBy,
        Instant createdAt,
        Instant updatedAt
) {
    public static CashEntryResponse fromEntity(CashEntry entry) {
        return new CashEntryResponse(
                entry.getId(),
                entry.getEntryDate(),
                entry.getDirection(),
                entry.getAmount(),
                toCategory(entry.getCategory()),
                entry.getDescription(),
                entry.getNote(),
                entry.isChecked(),
                entry.getCheckedAt(),
                toUser(entry.getCheckedBy()),
                entry.getRefType(),
                entry.getRefId(),
                toUser(entry.getCreatedBy()),
                entry.getCreatedAt(),
                entry.getUpdatedAt());
    }

    private static CategorySummary toCategory(ExpenseCategory category) {
        if (category == null) {
            return null;
        }
        return new CategorySummary(category.getId(), category.getCode(), category.getName());
    }

    private static UserSummary toUser(User user) {
        if (user == null) {
            return null;
        }
        return new UserSummary(user.getId(), user.getName());
    }
}
