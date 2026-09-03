package Manager_vnd.Manager.feature.auth;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Manager_vnd.Manager.exception.AppException;
import Manager_vnd.Manager.exception.InvalidRequestException;
import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.auth.dto.LoginRequest;
import Manager_vnd.Manager.feature.auth.dto.TokenResponse;
import Manager_vnd.Manager.feature.company.Company;
import Manager_vnd.Manager.feature.company.CompanyRepository;
import Manager_vnd.Manager.feature.user.User;
import Manager_vnd.Manager.feature.user.UserRepository;
import Manager_vnd.Manager.feature.user.dto.UserResponse;
import Manager_vnd.Manager.security.CustomUserDetails;
import Manager_vnd.Manager.util.TokenHashes;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final long accessTokenSeconds;
    private final long refreshTokenSeconds;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtEncoder jwtEncoder,
            UserRepository userRepository,
            CompanyRepository companyRepository,
            RefreshTokenRepository refreshTokenRepository,
            @Value("${jwt.access-token-seconds:900}") long accessTokenSeconds,
            @Value("${jwt.refresh-token-seconds:259200}") long refreshTokenSeconds) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.accessTokenSeconds = accessTokenSeconds;
        this.refreshTokenSeconds = refreshTokenSeconds;
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request, String deviceInfo, String ipAddress) {
        Company company = requireActiveCompany(request.companyId());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim(), request.password()));
        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findWithDetailsById(details.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", details.getId()));
        assertCanEnterCompany(user, company);
        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
        return issueTokenPair(user, company.getId(), deviceInfo, ipAddress);
    }

    @Override
    @Transactional
    public TokenResponse refresh(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new AppException("Không có refresh token", HttpStatus.UNAUTHORIZED);
        }
        RefreshToken stored = refreshTokenRepository.findByToken(TokenHashes.sha256(rawRefreshToken))
                .orElseThrow(() -> new AppException("Refresh token không hợp lệ", HttpStatus.UNAUTHORIZED));
        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new AppException("Refresh token đã hết hạn hoặc bị thu hồi", HttpStatus.UNAUTHORIZED);
        }
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
        User user = userRepository.findWithDetailsById(stored.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", stored.getUser().getId()));
        return issueTokenPair(user, companyIdOf(user), stored.getDeviceInfo(), stored.getIpAddress());
    }

    @Override
    @Transactional
    public void logout(String rawRefreshToken) {
        if (rawRefreshToken != null && !rawRefreshToken.isBlank()) {
            refreshTokenRepository.findByToken(TokenHashes.sha256(rawRefreshToken))
                    .ifPresent(token -> {
                        token.setRevoked(true);
                        refreshTokenRepository.save(token);
                    });
        }
        Jwt jwt = currentJwt();
        User user = userRepository.findByEmail(jwt.getSubject())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", jwt.getSubject()));
        refreshTokenRepository.revokeAllByUserId(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        Jwt jwt = currentJwt();
        User user = userRepository.findByEmail(jwt.getSubject())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", jwt.getSubject()));
        return UserResponse.fromEntity(user);
    }

    private TokenResponse issueTokenPair(User user, Long companyId, String deviceInfo, String ipAddress) {
        Instant now = Instant.now();
        String accessToken = encodeJwt(user, companyId, now, now.plusSeconds(accessTokenSeconds), "access");
        Instant refreshExpires = now.plusSeconds(refreshTokenSeconds);
        String refreshToken = encodeJwt(user, companyId, now, refreshExpires, "refresh");

        RefreshToken record = new RefreshToken();
        record.setToken(TokenHashes.sha256(refreshToken));
        record.setUser(user);
        record.setExpiresAt(refreshExpires);
        record.setRevoked(false);
        record.setDeviceInfo(blankToNull(deviceInfo));
        record.setIpAddress(blankToNull(ipAddress));
        refreshTokenRepository.save(record);

        return new TokenResponse(accessToken, refreshToken);
    }

    private String encodeJwt(User user, Long companyId, Instant issuedAt, Instant expiresAt, String tokenType) {
        var claims = JwtClaimsSet.builder()
                .subject(user.getEmail())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("userId", user.getId())
                .claim("tokenType", tokenType);
        if (companyId != null) {
            claims.claim("companyId", companyId);
        }
        if ("access".equals(tokenType)) {
            claims.claim("roles", roleClaim(user));
        }
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
    }

    private Company requireActiveCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
        if (!company.isActive()) {
            throw new InvalidRequestException("Cửa hàng đã ngừng hoạt động");
        }
        return company;
    }

    private void assertCanEnterCompany(User user, Company company) {
        if (hasRole(user, "ADMIN")) {
            return;
        }
        if (user.getCompany() == null || user.getCompany().getId() != company.getId()) {
            throw new AppException("Tài khoản không thuộc cửa hàng này", HttpStatus.FORBIDDEN);
        }
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRoles() != null && user.getRoles().stream()
                .anyMatch(role -> roleName.equalsIgnoreCase(role.getName()));
    }

    private Long companyIdOf(User user) {
        return user.getCompany() == null ? null : user.getCompany().getId();
    }

    private String roleClaim(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return "USER";
        }
        return user.getRoles().stream()
                .map(role -> role.getName().toUpperCase().replace(' ', '_'))
                .reduce((a, b) -> a + " " + b)
                .orElse("USER");
    }

    private Jwt currentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AppException("Chưa đăng nhập", HttpStatus.UNAUTHORIZED);
        }
        return jwt;
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
