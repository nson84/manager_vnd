package Manager_vnd.Manager.feature.company.dto;

import java.time.Instant;

import Manager_vnd.Manager.feature.company.Company;

public record CompanyResponse(
        long id,
        String name,
        String description,
        String address,
        String logo,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static CompanyResponse fromEntity(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getDescription(),
                company.getAddress(),
                company.getLogo(),
                company.isActive(),
                company.getCreatedAt(),
                company.getUpdatedAt());
    }
}
