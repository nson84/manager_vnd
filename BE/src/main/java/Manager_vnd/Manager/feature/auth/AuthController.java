package Manager_vnd.Manager.feature.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Manager_vnd.Manager.dto.ApiResponse;
import Manager_vnd.Manager.feature.auth.dto.LoginRequest;
import Manager_vnd.Manager.feature.auth.dto.RefreshRequest;
import Manager_vnd.Manager.feature.auth.dto.TokenResponse;
import Manager_vnd.Manager.feature.user.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final boolean cookieSecure;

    public AuthController(
            AuthService authService,
            @Value("${jwt.cookie-secure:false}") boolean cookieSecure) {
        this.authService = authService;
        this.cookieSecure = cookieSecure;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest httpRequest) {
        TokenResponse tokens = authService.login(
                request,
                httpRequest.getHeader("User-Agent"),
                clientIp(httpRequest));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, RefreshTokenCookie.issue(tokens.refreshToken(), cookieSecure).toString())
                .body(ApiResponse.success("Đăng nhập thành công", tokens));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @CookieValue(name = RefreshTokenCookie.NAME, required = false) String cookieToken,
            @RequestBody(required = false) RefreshRequest body) {
        String raw = firstNonBlank(cookieToken, body != null ? body.refreshToken() : null);
        TokenResponse tokens = authService.refresh(raw);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, RefreshTokenCookie.issue(tokens.refreshToken(), cookieSecure).toString())
                .body(ApiResponse.success("Làm mới token thành công", tokens));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me() {
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin phiên thành công", authService.getCurrentUser()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = RefreshTokenCookie.NAME, required = false) String cookieToken,
            @RequestBody(required = false) RefreshRequest body) {
        String raw = firstNonBlank(cookieToken, body != null ? body.refreshToken() : null);
        authService.logout(raw);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, RefreshTokenCookie.clear(cookieSecure).toString())
                .body(ApiResponse.success("Logout successful", null));
    }

    private String firstNonBlank(String cookieToken, String bodyToken) {
        if (cookieToken != null && !cookieToken.isBlank()) {
            return cookieToken;
        }
        if (bodyToken != null && !bodyToken.isBlank()) {
            return bodyToken;
        }
        return null;
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
