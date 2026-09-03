package Manager_vnd.Manager.feature.auth;

import java.time.Duration;

import org.springframework.http.ResponseCookie;

public final class RefreshTokenCookie {

    public static final String NAME = "refresh_token";
    public static final String PATH = "/api/v1/auth";
    public static final long MAX_AGE_SECONDS = 259200;

    private RefreshTokenCookie() {
    }

    public static ResponseCookie issue(String refreshJwt, boolean secure) {
        return ResponseCookie.from(NAME, refreshJwt)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path(PATH)
                .maxAge(Duration.ofSeconds(MAX_AGE_SECONDS))
                .build();
    }

    public static ResponseCookie clear(boolean secure) {
        return ResponseCookie.from(NAME, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path(PATH)
                .maxAge(Duration.ZERO)
                .build();
    }
}
