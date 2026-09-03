package Manager_vnd.Manager.feature.payslip.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import Manager_vnd.Manager.feature.payslip.Payslip;
import Manager_vnd.Manager.feature.payslip.PayslipStatus;

public record PayslipResponse(
        long id,
        long workerId,
        String workerName,
        LocalDate periodStart,
        LocalDate periodEnd,
        BigDecimal grossAmount,
        BigDecimal advanceDeducted,
        BigDecimal otherDeduction,
        BigDecimal netAmount,
        PayslipStatus status,
        Instant paidAt,
        String note,
        long createdById,
        Instant createdAt,
        Instant updatedAt
) {
    public static PayslipResponse fromEntity(Payslip payslip) {
        return new PayslipResponse(
                payslip.getId(),
                payslip.getWorker().getId(),
                payslip.getWorker().getName(),
                payslip.getPeriodStart(),
                payslip.getPeriodEnd(),
                payslip.getGrossAmount(),
                payslip.getAdvanceDeducted(),
                payslip.getOtherDeduction(),
                payslip.getNetAmount(),
                payslip.getStatus(),
                payslip.getPaidAt(),
                payslip.getNote(),
                payslip.getCreatedBy().getId(),
                payslip.getCreatedAt(),
                payslip.getUpdatedAt());
    }
}
