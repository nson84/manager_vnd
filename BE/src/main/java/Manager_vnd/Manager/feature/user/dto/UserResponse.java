package Manager_vnd.Manager.feature.user.dto;

import java.time.Instant;
import java.util.List;

import Manager_vnd.Manager.feature.company.Company;
import Manager_vnd.Manager.feature.role.Role;
import Manager_vnd.Manager.feature.user.Gender;
import Manager_vnd.Manager.feature.user.User;

public record UserResponse(
        long id,
        String name,
        String email,
        Integer age,
        Gender gender,
        String address,
        String avatar,
        boolean active,
        CompanySummary company,
        List<RoleSummary> roles,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getGender(),
                user.getAddress(),
                user.getAvatar(),
                user.isActive(),
                toCompanySummary(user.getCompany()),
                user.getRoles().stream().map(UserResponse::toRoleSummary).toList(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private static CompanySummary toCompanySummary(Company company) {
        if (company == null) {
            return null;
        }
        return new CompanySummary(company.getId(), company.getName());
    }

    private static RoleSummary toRoleSummary(Role role) {
        return new RoleSummary(role.getId(), role.getName());
    }
}
