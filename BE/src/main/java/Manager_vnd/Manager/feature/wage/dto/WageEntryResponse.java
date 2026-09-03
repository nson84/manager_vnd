package Manager_vnd.Manager.feature.wage.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import Manager_vnd.Manager.feature.wage.WageEntry;
import Manager_vnd.Manager.feature.worker.WageType;

public record WageEntryResponse(
        long id,
        long workerId,
        String workerName,
        LocalDate workDate,
        WageType wageType,
        BigDecimal quantity,
        BigDecimal unitRate,
        BigDecimal amount,
        String note,
        Long payslipId,
        long createdById,
        Instant createdAt,
        Instant updatedAt
) {
    public static WageEntryResponse fromEntity(WageEntry entry) {
        return new WageEntryResponse(
                entry.getId(),
                entry.getWorker().getId(),
                entry.getWorker().getName(),
                entry.getWorkDate(),
                entry.getWageType(),
                entry.getQuantity(),
                entry.getUnitRate(),
                entry.getAmount(),
                entry.getNote(),
                entry.getPayslip() != null ? entry.getPayslip().getId() : null,
                entry.getCreatedBy().getId(),
                entry.getCreatedAt(),
                entry.getUpdatedAt());
    }
}
