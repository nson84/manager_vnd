package Manager_vnd.Manager.dto;

import java.util.List;

public record PaginatedResult<T>(
        PaginationMeta meta,
        List<T> result
) {
}
