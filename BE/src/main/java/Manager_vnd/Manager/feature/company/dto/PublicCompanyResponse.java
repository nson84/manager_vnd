package Manager_vnd.Manager.feature.company.dto;

import Manager_vnd.Manager.feature.company.Company;

public record PublicCompanyResponse(
        long id,
        String name,
        String description,
        String address,
        String logo
) {
    public static PublicCompanyResponse fromEntity(Company company) {
        return new PublicCompanyResponse(
                company.getId(),
                company.getName(),
                company.getDescription(),
                company.getAddress(),
                company.getLogo());
    }
}
