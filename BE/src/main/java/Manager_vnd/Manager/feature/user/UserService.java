package Manager_vnd.Manager.feature.user;

import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.feature.user.dto.CreateUserRequest;
import Manager_vnd.Manager.feature.user.dto.UpdateUserRequest;
import Manager_vnd.Manager.feature.user.dto.UserResponse;

public interface UserService {

    PaginatedResult<UserResponse> getAllUsers(int page, int size, String sort, Boolean active);

    UserResponse getUserById(long id);

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(UpdateUserRequest request);

    UserResponse disableUser(long id);

    UserResponse enableUser(long id);
}
