package Manager_vnd.Manager.feature.role;

import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.feature.role.dto.CreateRoleRequest;
import Manager_vnd.Manager.feature.role.dto.RoleResponse;
import Manager_vnd.Manager.feature.role.dto.UpdateRoleRequest;

public interface RoleService {

    PaginatedResult<RoleResponse> getAll(int page, int size, String sort);

    RoleResponse getById(long id);

    RoleResponse create(CreateRoleRequest request);

    RoleResponse update(UpdateRoleRequest request);

    void delete(long id);
}
