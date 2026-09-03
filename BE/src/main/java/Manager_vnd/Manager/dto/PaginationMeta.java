package Manager_vnd.Manager.dto;

public record PaginationMeta(
        int page,
        int pageSize,
        int pages,
        long total
) {
}
