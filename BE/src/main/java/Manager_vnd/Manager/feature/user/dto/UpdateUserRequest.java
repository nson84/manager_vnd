package Manager_vnd.Manager.feature.user.dto;

import java.util.List;

import Manager_vnd.Manager.feature.user.Gender;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
        @NotNull(message = "Id is required")
        Long id,

        String name,

        Integer age,

        Gender gender,

        String address,

        String avatar,

        Long companyId,

        List<Long> roleIds
) {
}
