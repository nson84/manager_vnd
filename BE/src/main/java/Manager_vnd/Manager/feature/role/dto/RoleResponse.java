package Manager_vnd.Manager.feature.role.dto;

import java.time.Instant;
import java.util.List;

import Manager_vnd.Manager.feature.permission.dto.PermissionResponse;
import Manager_vnd.Manager.feature.role.Role;

public record RoleResponse(
        long id,
        String name,
        String description,
        List<PermissionResponse> permissions,
        Instant createdAt,
        Instant updatedAt
) {
    public static RoleResponse fromEntity(Role role) {
        List<PermissionResponse> permissions = role.getPermissions() == null
                ? List.of()
                : role.getPermissions().stream().map(PermissionResponse::fromEntity).toList();
        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                permissions,
                role.getCreatedAt(),
                role.getUpdatedAt());
    }
}
