package Manager_vnd.Manager.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        int statusCode,
        T data,
        String message,
        LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> success(T data) {
        return success("Thành công", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return of(HttpStatus.OK.value(), data, message);
    }

    public static <T> ApiResponse<T> created(T data) {
        return created("Tạo thành công", data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return of(HttpStatus.CREATED.value(), data, message);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return of(HttpStatus.NOT_FOUND.value(), null, message);
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return of(HttpStatus.BAD_REQUEST.value(), null, message);
    }

    public static ApiResponse<List<String>> badRequest(String message, List<String> details) {
        return of(HttpStatus.BAD_REQUEST.value(), details, message);
    }

    public static <T> ApiResponse<T> conflict(String message) {
        return of(HttpStatus.CONFLICT.value(), null, message);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return of(HttpStatus.UNAUTHORIZED.value(), null, message);
    }

    public static <T> ApiResponse<T> forbidden(String message) {
        return of(HttpStatus.FORBIDDEN.value(), null, message);
    }

    public static <T> ApiResponse<T> error(String message) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, message);
    }

    public static <T> ApiResponse<T> of(int statusCode, T data, String message) {
        return new ApiResponse<>(statusCode, data, message, LocalDateTime.now());
    }
}
