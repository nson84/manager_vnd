# Auth

JWT HS256 access token (15 phút) + refresh token (3 ngày, ADR-001).

## Flow
- Access JWT claim `roles` + `companyId` (cửa hàng đang vào)
- Users/Companies/Roles/Permissions: `hasRole('ADMIN')`
- Sổ quỹ / phiếu chi / phiếu lương: `MANAGER` + `ADMIN`
- Nghiệp vụ còn lại: `STAFF` / `USER` / `MANAGER` / `ADMIN`
- Login body có `companyId`; ADMIN vào được mọi cửa hàng, role khác phải đúng công ty
- Seed cửa hàng `Tạp Hóa Phúc Sơn`
- DB lưu SHA-256 của refresh JWT
- Refresh: cookie trước, rồi body; rotation (revoke cũ)
- Logout: Bearer bắt buộc, revoke DB, clear cookie
