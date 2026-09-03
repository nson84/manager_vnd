package Manager_vnd.Manager.feature.permission;

import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.feature.permission.dto.CreatePermissionRequest;
import Manager_vnd.Manager.feature.permission.dto.PermissionResponse;
import Manager_vnd.Manager.feature.permission.dto.UpdatePermissionRequest;

public interface PermissionService {

    PaginatedResult<PermissionResponse> getAll(int page, int size, String sort);

    PermissionResponse getById(long id);

    PermissionResponse create(CreatePermissionRequest request);

    PermissionResponse update(UpdatePermissionRequest request);

    void delete(long id);
}
