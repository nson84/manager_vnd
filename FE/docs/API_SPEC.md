# API Integration Spec — Frontend

> FE service layer mapping to BE endpoints.
> BE full spec: `../BE/docs/API_SPEC.md`
> Update this file whenever FE integrates new endpoints.

---

## Base URL

```
Development: /api/v1          (Vite proxy → http://localhost:8080)
Production:  ${VITE_API_BASE}  (from .env.production)
```

---

## Shared Client

All calls use `src/services/apiClient.ts`:

```typescript
apiClient<T>(path: string, options?: RequestInit): Promise<ApiResponse<T>>
```

Auth header injected automatically from AuthContext.

---

## 1. Auth Service — `src/features/auth/services/authService.ts`

| Function | Method | BE Endpoint | Request | Response |
|----------|--------|-------------|---------|----------|
| `login` | POST | `/auth/login` | `LoginRequest` | `TokenResponse` |
| `register` | POST | `/auth/register` | `RegisterRequest` | `UserResponse` |
| `refresh` | POST | `/auth/refresh` | — (cookie auto) | `TokenResponse` |
| `logout` | POST | `/auth/logout` | — | `null` |
| `getMe` | GET | `/auth/me` | — | `AuthUser` |

### Notes
- Login: store `accessToken` in AuthContext, refresh cookie set by BE automatically
- Refresh: called by apiClient on 401, cookie sent automatically
- Logout: clear AuthContext, BE clears cookie

---

## 2. User Service — `src/features/user/services/userService.ts`

| Function | Method | BE Endpoint | Request | Response |
|----------|--------|-------------|---------|----------|
| `getAll` | GET | `/users?page&size&sort` | — | `PaginatedResult<UserResponse>` |
| `getById` | GET | `/users/{id}` | — | `UserResponse` |
| `create` | POST | `/users` | `CreateUserRequest` | `UserResponse` |
| `update` | PUT | `/users` | `UpdateUserRequest` | `UserResponse` |
| `delete` | DELETE | `/users/{id}` | — | `null` |

---

## 3. Company Service — `src/features/company/services/companyService.ts`

| Function | Method | BE Endpoint | Request | Response |
|----------|--------|-------------|---------|----------|
| `getAll` | GET | `/companies?page&size&sort` | — | `PaginatedResult<CompanyResponse>` |
| `getById` | GET | `/companies/{id}` | — | `CompanyResponse` |
| `create` | POST | `/companies` | `CreateCompanyRequest` | `CompanyResponse` |
| `update` | PUT | `/companies` | `UpdateCompanyRequest` | `CompanyResponse` |
| `delete` | DELETE | `/companies/{id}` | — | `null` |

---

## 4. Role Service — `src/features/role/services/roleService.ts`

| Function | Method | BE Endpoint | Request | Response |
|----------|--------|-------------|---------|----------|
| `getAll` | GET | `/roles?page&size&sort` | — | `PaginatedResult<RoleResponse>` |
| `getById` | GET | `/roles/{id}` | — | `RoleResponse` |
| `create` | POST | `/roles` | `CreateRoleRequest` | `RoleResponse` |
| `update` | PUT | `/roles` | `UpdateRoleRequest` | `RoleResponse` |
| `delete` | DELETE | `/roles/{id}` | — | `null` |

---

## 5. Permission Service — `src/features/permission/services/permissionService.ts`

| Function | Method | BE Endpoint | Request | Response |
|----------|--------|-------------|---------|----------|
| `getAll` | GET | `/permissions?page&size&sort` | — | `PaginatedResult<PermissionResponse>` |
| `getById` | GET | `/permissions/{id}` | — | `PermissionResponse` |
| `create` | POST | `/permissions` | `CreatePermissionRequest` | `PermissionResponse` |
| `update` | PUT | `/permissions` | `UpdatePermissionRequest` | `PermissionResponse` |
| `delete` | DELETE | `/permissions/{id}` | — | `null` |

---

## 6. File Service — `src/features/file/services/fileService.ts`

| Function | Method | BE Endpoint | Request | Response |
|----------|--------|-------------|---------|----------|
| `upload` | POST | `/files` | `FormData { file, folder }` | `FileUploadResponse` |

### Upload Flow
```
1. fileService.upload(file, 'avatars')
   → POST /api/v1/files (multipart/form-data)
   → returns { fileName, fileUrl, ... }

2. userService.update({ id, avatar: fileName })
   → PUT /api/v1/users
```

---

## Error Handling

All errors return BE `ApiResponse` with error status:

```typescript
// 400 Validation
{ statusCode: 400, data: { email: "Invalid email" }, message: "Validation failed" }

// 401 Unauthorized
{ statusCode: 401, data: null, message: "Unauthorized" }

// 404 Not Found
{ statusCode: 404, data: null, message: "User not found with id: 99" }
```

FE `apiClient` throws `ApiError` — hooks catch and expose to UI.

---

## Status Legend

| Symbol | Meaning |
|--------|---------|
| ✅ | Implemented in FE |
| 🔲 | Planned — BE spec ready |
| — | Not started |

Current status: all endpoints are 🔲 (planned). Implement as BE features are completed.
