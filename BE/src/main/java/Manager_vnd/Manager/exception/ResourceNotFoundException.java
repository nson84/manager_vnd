package Manager_vnd.Manager.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String resource, String field, Object value) {
        super("Không tìm thấy %s với %s: %s".formatted(resource, field, value), HttpStatus.NOT_FOUND);
    }
}
