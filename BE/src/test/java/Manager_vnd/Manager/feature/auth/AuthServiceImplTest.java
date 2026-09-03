package Manager_vnd.Manager.feature.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import Manager_vnd.Manager.exception.AppException;
import Manager_vnd.Manager.feature.auth.dto.LoginRequest;
import Manager_vnd.Manager.feature.company.Company;
import Manager_vnd.Manager.feature.company.CompanyRepository;
import Manager_vnd.Manager.feature.role.Role;
import Manager_vnd.Manager.feature.user.User;
import Manager_vnd.Manager.feature.user.UserRepository;
import Manager_vnd.Manager.security.CustomUserDetails;
import Manager_vnd.Manager.util.TokenHashes;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtEncoder jwtEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                authenticationManager,
                jwtEncoder,
                userRepository,
                companyRepository,
                refreshTokenRepository,
                900,
                259200);
    }

    @Test
    @DisplayName("Login lưu refresh token hash và trả cả 2 token")
    void login_persistsHashedRefreshToken() {
        User user = adminUser(1L);
        CustomUserDetails details = new CustomUserDetails(user);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(shop(1L)));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities()));
        when(userRepository.findWithDetailsById(1L)).thenReturn(Optional.of(user));
        stubEncoder();

        var result = authService.login(
                new LoginRequest(1L, "admin@local.dev", "password123"), "Chrome", "127.0.0.1");

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertEquals(TokenHashes.sha256(result.refreshToken()), captor.getValue().getToken());
        assertEquals("access-1", result.accessToken());
        assertEquals("refresh-2", result.refreshToken());
    }

    @Test
    @DisplayName("Refresh rotation: revoke cũ, tạo mới")
    void refresh_rotatesToken() {
        User user = user(1L);
        String oldRaw = "old-refresh";
        RefreshToken stored = new RefreshToken();
        stored.setUser(user);
        stored.setToken(TokenHashes.sha256(oldRaw));
        stored.setRevoked(false);
        stored.setExpiresAt(Instant.now().plusSeconds(3600));
        when(refreshTokenRepository.findByToken(TokenHashes.sha256(oldRaw))).thenReturn(Optional.of(stored));
        when(userRepository.findWithDetailsById(1L)).thenReturn(Optional.of(user));
        stubEncoder();

        var result = authService.refresh(oldRaw);

        assertTrue(stored.isRevoked());
        verify(refreshTokenRepository).save(stored);
        assertEquals("access-1", result.accessToken());
    }

    @Test
    @DisplayName("Refresh thiếu token → 401")
    void refresh_missing_unauthorized() {
        assertThrows(AppException.class, () -> authService.refresh(null));
    }

    @Test
    @DisplayName("Nhân viên không thuộc cửa hàng bị 403")
    void login_staffWrongShop_forbidden() {
        User staff = user(2L);
        staff.setCompany(shop(1L));
        CustomUserDetails details = new CustomUserDetails(staff);
        when(companyRepository.findById(9L)).thenReturn(Optional.of(shop(9L)));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities()));
        when(userRepository.findWithDetailsById(2L)).thenReturn(Optional.of(staff));

        assertThrows(AppException.class, () -> authService.login(
                new LoginRequest(9L, "staff@local.dev", "password123"), null, null));
    }

    private void stubEncoder() {
        AtomicInteger seq = new AtomicInteger();
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenAnswer(inv -> {
            int n = seq.incrementAndGet();
            String value = (n % 2 == 1 ? "access-" : "refresh-") + n;
            return Jwt.withTokenValue(value)
                    .header("alg", "HS256")
                    .subject("admin@local.dev")
                    .claim("userId", 1L)
                    .build();
        });
    }

    private User adminUser(long id) {
        User user = user(id);
        Role admin = new Role();
        admin.setName("ADMIN");
        user.setRoles(List.of(admin));
        return user;
    }

    private User user(long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("admin@local.dev");
        user.setPassword("hash");
        user.setActive(true);
        return user;
    }

    private Company shop(long id) {
        Company company = new Company();
        company.setId(id);
        company.setName("Tạp Hóa Phúc Sơn");
        company.setActive(true);
        return company;
    }
}
