# Auth

JWT HS256 access token (15 phút) + refresh token (3 ngày, ADR-001).

## Flow
- Access JWT claim `roles` (vd. `"ADMIN USER"`) → `JwtAuthenticationConverter` prefix `ROLE_`
- Users/Companies: `hasRole('ADMIN')`
- API nghiệp vụ: `hasAnyRole('USER','ADMIN')`
- DB lưu SHA-256 của refresh JWT
- Refresh: cookie trước, rồi body; rotation (revoke cũ)
- Logout: Bearer bắt buộc, revoke DB, clear cookie
