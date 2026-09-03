package Manager_vnd.Manager.feature.debt.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import Manager_vnd.Manager.feature.debt.DebtEntry;
import Manager_vnd.Manager.feature.debt.DebtEntryType;
import Manager_vnd.Manager.feature.debt.DebtRefType;
import Manager_vnd.Manager.feature.debt.LedgerDirection;

public record DebtEntryResponse(
        long id,
        Long customerId,
        String customerName,
        Long workerId,
        String workerName,
        DebtEntryType entryType,
        LedgerDirection direction,
        BigDecimal amount,
        LocalDate entryDate,
        String note,
        DebtRefType refType,
        Long refId,
        long createdById,
        Instant createdAt
) {
    public static DebtEntryResponse fromEntity(DebtEntry entry) {
        return new DebtEntryResponse(
                entry.getId(),
                entry.getCustomer() != null ? entry.getCustomer().getId() : null,
                entry.getCustomer() != null ? entry.getCustomer().getName() : null,
                entry.getWorker() != null ? entry.getWorker().getId() : null,
                entry.getWorker() != null ? entry.getWorker().getName() : null,
                entry.getEntryType(),
                entry.getDirection(),
                entry.getAmount(),
                entry.getEntryDate(),
                entry.getNote(),
                entry.getRefType(),
                entry.getRefId(),
                entry.getCreatedBy().getId(),
                entry.getCreatedAt());
    }
}
