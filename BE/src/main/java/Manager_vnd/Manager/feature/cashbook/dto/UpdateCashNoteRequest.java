package Manager_vnd.Manager.feature.cashbook.dto;

import jakarta.validation.constraints.Size;

public record UpdateCashNoteRequest(
        @Size(max = 500, message = "Ghi chú tối đa 500 ký tự")
        String note
) {
}
