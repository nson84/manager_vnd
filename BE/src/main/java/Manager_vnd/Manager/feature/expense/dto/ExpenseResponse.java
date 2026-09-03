package Manager_vnd.Manager.feature.expense.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import Manager_vnd.Manager.feature.expense.Expense;
import Manager_vnd.Manager.feature.expense.ExpenseStatus;

public record ExpenseResponse(
        long id,
        long categoryId,
        String categoryCode,
        String categoryName,
        BigDecimal amount,
        LocalDate expenseDate,
        String note,
        ExpenseStatus status,
        long createdById,
        Instant createdAt,
        Instant updatedAt
) {
    public static ExpenseResponse fromEntity(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getCategory().getId(),
                expense.getCategory().getCode(),
                expense.getCategory().getName(),
                expense.getAmount(),
                expense.getExpenseDate(),
                expense.getNote(),
                expense.getStatus(),
                expense.getCreatedBy().getId(),
                expense.getCreatedAt(),
                expense.getUpdatedAt());
    }
}
