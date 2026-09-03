# User Feature — Implementation Context

> Module: `feature/user/` | Phase 3 (partial — ahead of Permission/Company/Role CRUD)

## Overview

CRUD API for login accounts (`users` table). Each user may belong to one Company and hold many Roles.

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/users` | Paginated list (page 1-based, size, sort) |
| GET | `/api/v1/users/{id}` | Get by ID |
| POST | `/api/v1/users` | Create (BCrypt password, assign company/roles) |
| PUT | `/api/v1/users` | Update (id in body; email/password not updatable) |
| DELETE | `/api/v1/users/{id}` | Soft disable (`active=false`) + revoke tokens |
| POST | `/api/v1/users/{id}/enable` | Re-enable user |

All responses: `ResponseEntity<ApiResponse<T>>`.

## Dependencies

- `CompanyRepository` — validate `companyId` on create/update
- `RoleRepository` — validate `roleIds` on create/update
- `RefreshTokenRepository` — revoke + delete tokens on user delete
- `PasswordEncoder` (BCrypt strength 12)

## Security

Currently `SecurityConfig` permits all requests (JWT not enforced yet).

## Tests

- `UserServiceImplTest` — unit tests (12 cases)
- Integration tests deferred until `application-test.yml` + test MySQL configured

## Related Docs

- `BE/docs/API_SPEC.md` — Users section
- `BE/docs/DATABASE.md` — `users`, `user_role`, `refresh_tokens`
