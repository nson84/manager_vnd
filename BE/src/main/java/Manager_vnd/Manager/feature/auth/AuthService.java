package Manager_vnd.Manager.feature.auth;

import Manager_vnd.Manager.feature.auth.dto.LoginRequest;
import Manager_vnd.Manager.feature.auth.dto.TokenResponse;
import Manager_vnd.Manager.feature.user.dto.UserResponse;

public interface AuthService {

    TokenResponse login(LoginRequest request, String deviceInfo, String ipAddress);

    TokenResponse refresh(String rawRefreshToken);

    void logout(String rawRefreshToken);

    UserResponse getCurrentUser();
}
