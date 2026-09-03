package Manager_vnd.Manager.feature.permission.dto;

import java.time.Instant;

import Manager_vnd.Manager.feature.permission.Permission;

public record PermissionResponse(
        long id,
        String name,
        String apiPath,
        String method,
        String module,
        Instant createdAt,
        Instant updatedAt
) {
    public static PermissionResponse fromEntity(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getName(),
                permission.getApiPath(),
                permission.getMethod(),
                permission.getModule(),
                permission.getCreatedAt(),
                permission.getUpdatedAt());
    }
}
