package Manager_vnd.Manager.feature.cashbook.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateCashCheckedRequest(
        @NotNull(message = "Trạng thái đối chiếu bắt buộc")
        Boolean checked
) {
}
