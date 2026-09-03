# Data Model — TypeScript Types

> Frontend types matching BE entities and DTOs.
> Update this file whenever types change.
> Source of truth for BE schema: `../BE/docs/DATABASE.md`

---

## Shared Types

```typescript
// src/types/api.types.ts

export interface ApiResponse<T> {
  statusCode: number
  data: T
  message: string
  timestamp: string
}

export interface PaginationMeta {
  page: number
  pageSize: number
  pages: number
  total: number
}

export interface PaginatedResult<T> {
  meta: PaginationMeta
  result: T[]
}
```

---

## Auth

```typescript
// src/features/auth/types/auth.types.ts

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  name: string
  email: string
  password: string
  age?: number
  gender?: Gender
  address?: string
}

export interface TokenResponse {
  accessToken: string
  refreshToken: string
}

export interface AuthUser {
  id: number
  name: string
  email: string
  age?: number
  gender?: Gender
  address?: string
  avatar?: string | null
  company?: CompanySummary | null
  roles: RoleSummary[]
}
```

---

## User

```typescript
export interface UserResponse {
  id: number
  name: string
  email: string
  age?: number
  gender?: Gender
  address?: string
  avatar?: string | null
  company?: CompanySummary | null
  roles: RoleSummary[]
  createdAt: string
  updatedAt?: string | null
}

export interface CreateUserRequest {
  name: string
  email: string
  password: string
  age?: number
  gender?: Gender
  address?: string
  companyId?: number
  roleIds?: number[]
}

export interface UpdateUserRequest {
  id: number
  name?: string
  age?: number
  gender?: Gender
  address?: string
  companyId?: number
  roleIds?: number[]
  avatar?: string
}
```

---

## Company

```typescript
export interface CompanyResponse {
  id: number
  name: string
  description?: string
  address?: string
  logo?: string | null
  createdAt: string
  updatedAt?: string | null
}

export interface CompanySummary {
  id: number
  name: string
}

export interface CreateCompanyRequest {
  name: string
  description?: string
  address?: string
  logo?: string
}

export interface UpdateCompanyRequest {
  id: number
  name?: string
  description?: string
  address?: string
  logo?: string
}
```

---

## Role & Permission

```typescript
export interface RoleResponse {
  id: number
  name: string
  description?: string
  permissions: PermissionResponse[]
  createdAt: string
  updatedAt?: string | null
}

export interface RoleSummary {
  id: number
  name: string
}

export interface PermissionResponse {
  id: number
  name: string
  apiPath: string
  method: string
  module: string
  createdAt: string
  updatedAt?: string | null
}

export interface CreateRoleRequest {
  name: string
  description?: string
  permissionIds: number[]
}

export interface CreatePermissionRequest {
  name: string
  apiPath: string
  method: string
  module: string
}
```

---

## File Upload

```typescript
export interface FileUploadResponse {
  fileName: string
  folder: string
  fileUrl: string
  size: number
  uploadedAt: string
}
```

---

## Enums

```typescript
export type Gender = 'MALE' | 'FEMALE' | 'OTHER'

export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE'
```

---

## Type Mapping to BE

| FE Type | BE DTO/Entity | Location |
|---------|--------------|----------|
| `UserResponse` | `UserResponse` record | BE feature/user/dto |
| `CreateUserRequest` | `CreateUserRequest` record | BE feature/user/dto |
| `CompanyResponse` | `CompanyResponse` record | BE feature/company/dto |
| `TokenResponse` | `TokenResponse` record | BE feature/auth/dto |
| `ApiResponse<T>` | `ApiResponse<T>` record | BE dto/ |
| `PaginatedResult<T>` | pagination wrapper | BE dto/ |

Field names use camelCase on both sides (JSON serialization).

---

## Notes

- All date fields are ISO 8601 strings (`string`, not `Date`)
- Optional fields use `?` — match BE nullable columns
- `avatar` and `logo` store file name (not full URL) — prepend `/uploads/{folder}/`
- Never include `password` in response types
