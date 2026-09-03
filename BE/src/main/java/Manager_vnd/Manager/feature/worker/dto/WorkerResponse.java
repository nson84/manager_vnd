package Manager_vnd.Manager.feature.worker.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import Manager_vnd.Manager.feature.worker.WageType;
import Manager_vnd.Manager.feature.worker.Worker;

public record WorkerResponse(
        long id,
        String name,
        String phone,
        String address,
        String jobTitle,
        WageType wageType,
        BigDecimal defaultUnitRate,
        LocalDate hireDate,
        boolean active,
        String note,
        BigDecimal currentAdvance,
        Instant createdAt,
        Instant updatedAt
) {
    public static WorkerResponse fromEntity(Worker worker) {
        return new WorkerResponse(
                worker.getId(),
                worker.getName(),
                worker.getPhone(),
                worker.getAddress(),
                worker.getJobTitle(),
                worker.getWageType(),
                worker.getDefaultUnitRate(),
                worker.getHireDate(),
                worker.isActive(),
                worker.getNote(),
                worker.getCurrentAdvance(),
                worker.getCreatedAt(),
                worker.getUpdatedAt());
    }
}
