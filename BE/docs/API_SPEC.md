# API Specification

> All endpoints return `ApiResponse<T>` wrapper.
> Update this file whenever endpoints change.

---

## Base URL

```
Development: http://localhost:8080/api/v1
Production:  https://api.example.com/api/v1
```

---

## Authentication

All endpoints require JWT in `Authorization: Bearer <accessToken>` header,
except those marked as  **Public**.

---

## 1. Auth

### POST /auth/login

Login: BCrypt + JWT. Trả `accessToken` + `refreshToken`. Cookie HttpOnly `refresh_token` (Path=`/api/v1/auth`, Max-Age=259200).

**Request Body:**
```json
{
  "companyId": 1,
  "email": "admin@local.dev",
  "password": "password123"
}
```

**Success (200):** `ApiResponse<TokenResponse>` `{ accessToken, refreshToken }`. Access JWT có `roles` + `companyId`.

**Errors:** 400 validation | 401 sai mật khẩu / tài khoản tắt | 403 không thuộc cửa hàng | 404 cửa hàng không tồn tại.

---

### POST /auth/register 

Register a new user account.

**Request Body:**
```json
{
  "name": "Nguyen Van A",
  "email": "user@example.com",
  "password": "password123",
  "age": 25,
  "gender": "MALE",
  "address": "Ho Chi Minh City"
}
```

**Success Response (201):**
```json
{
  "statusCode": 201,
  "data": {
    "id": 1,
    "name": "Nguyen Van A",
    "email": "user@example.com",
    "age": 25,
    "gender": "MALE",
    "address": "Ho Chi Minh City",
    "createdAt": "20xx-02-28T10:00:00Z"
  },
  "message": "User registered",
  "timestamp": "20xx-02-28T10:00:00"
}
```

**Errors:**
| Status | When |
|--------|------|
| 400 | Validation failed (blank name, invalid email, password < 8 chars) |
| 409 | Email already exists |

---

### POST /auth/refresh 

Get new access token using refresh token.

**Sources (backend checks in order):**
1. Cookie `refresh_token` (SPA — browser sends automatically)
2. Request body `refreshToken` (Mobile — sends explicitly)

**Request Body (mobile only):**
```json
{
  "refreshToken": "eyJ..."
}
```

**Success Response (200):**
```json
{
  "statusCode": 200,
  "data": {
    "accessToken": "eyJ...(new)",
    "refreshToken": "eyJ...(new)"
  },
  "message": "Token refreshed",
  "timestamp": "20xx-02-28T10:15:00"
}
```

Also sets new `refresh_token` cookie (replaces old one).

**Errors:**
| Status | When |
|--------|------|
| 401 | No refresh token provided |
| 401 | Refresh token expired or revoked |

---

### POST /auth/logout

Invalidate refresh token and clear cookie.

**Request:** No body needed. Token taken from cookie or Authorization header.

**Success Response (200):**
```json
{
  "statusCode": 200,
  "data": null,
  "message": "Logged out",
  "timestamp": "20xx-02-28T11:00:00"
}
```

**Also clears cookie:**
```
Set-Cookie: refresh_token=; Max-Age=0; Path=/api/v1/auth
```

---

### GET /auth/me

Get current logged-in user info.

**Success Response (200):**
```json
{
  "statusCode": 200,
  "data": {
    "id": 1,
    "name": "Nguyen Van A",
    "email": "user@example.com",
    "age": 25,
    "gender": "MALE",
    "address": "Ho Chi Minh City",
    "avatar": null,
    "company": {
      "id": 1,
      "name": "HoiDanIT"
    },
    "roles": [
      { "id": 1, "name": "ADMIN" }
    ]
  },
  "message": "Success",
  "timestamp": "20xx-02-28T10:00:00"
}
```

---

## 2. Users

### GET /users 🔒

List all users with pagination.

**Query Parameters:**
| Param | Type | Default | Description |
|-------|------|---------|-------------|
| page | int | 1 | Page number (1-based) |
| size | int | 10 | Items per page |
| sort | string | id,asc | Sort field and direction |

**Success Response (200):**
```json
{
  "statusCode": 200,
  "data": {
    "meta": {
      "page": 1,
      "pageSize": 10,
      "pages": 5,
      "total": 50
    },
    "result": [
      {
        "id": 1,
        "name": "Nguyen Van A",
        "email": "user@example.com",
        "age": 25,
        "gender": "MALE",
        "address": "Ho Chi Minh City",
        "avatar": null,
        "company": { "id": 1, "name": "HoiDanIT" },
        "roles": [{ "id": 1, "name": "ADMIN" }],
        "createdAt": "20xx-02-28T10:00:00Z",
        "updatedAt": null
      }
    ]
  },
  "message": "Fetch all users",
  "timestamp": "20xx-02-28T10:00:00"
}
```

---

### GET /users/{id} 🔒

Get a single user by ID.

**Success Response (200):**
```json
{
  "statusCode": 200,
  "data": {
    "id": 1,
    "name": "Nguyen Van A",
    "email": "user@example.com",
    "age": 25,
    "gender": "MALE",
    "address": "Ho Chi Minh City",
    "avatar": null,
    "company": { "id": 1, "name": "HoiDanIT" },
    "roles": [{ "id": 1, "name": "ADMIN" }],
    "createdAt": "20xx-02-28T10:00:00Z",
    "updatedAt": null
  },
  "message": "Fetch user",
  "timestamp": "20xx-02-28T10:00:00"
}
```

**Errors:**
| Status | When |
|--------|------|
| 404 | User not found |

---

### POST /users 🔒

Create a new user (admin operation — assigns company and roles).

**Request Body:**
```json
{
  "name": "Tran Thi B",
  "email": "tran@example.com",
  "password": "password123",
  "age": 30,
  "gender": "FEMALE",
  "address": "Ha Noi",
  "companyId": 1,
  "roleIds": [3, 5]
}
```

**Success Response (201):**
```json
{
  "statusCode": 201,
  "data": {
    "id": 2,
    "name": "Tran Thi B",
    "email": "tran@example.com",
    "age": 30,
    "gender": "FEMALE",
    "address": "Ha Noi",
    "avatar": null,
    "company": { "id": 1, "name": "HoiDanIT" },
    "roles": [
      { "id": 3, "name": "HR" },
      { "id": 5, "name": "USER" }
    ],
    "createdAt": "20xx-02-28T10:00:00Z"
  },
  "message": "User created",
  "timestamp": "20xx-02-28T10:00:00"
}
```

**Errors:**
| Status | When |
|--------|------|
| 400 | Validation failed |
| 404 | Company or Role not found |
| 409 | Email already exists |

---

### PUT /users 🔒

Update an existing user.

**Request Body:**
```json
{
  "id": 2,
  "name": "Tran Thi B Updated",
  "age": 31,
  "gender": "FEMALE",
  "address": "Da Nang",
  "companyId": 2,
  "roleIds": [3]
}
```

**Success Response (200):**
```json
{
  "statusCode": 200,
  "data": {
    "id": 2,
    "name": "Tran Thi B Updated",
    "email": "tran@example.com",
    "age": 31,
    "gender": "FEMALE",
    "address": "Da Nang",
    "avatar": null,
    "company": { "id": 2, "name": "FPT Software" },
    "roles": [{ "id": 3, "name": "HR" }],
    "updatedAt": "20xx-02-28T11:00:00Z"
  },
  "message": "User updated",
  "timestamp": "20xx-02-28T11:00:00"
}
```

**Errors:**
| Status | When |
|--------|------|
| 400 | Validation failed |
| 404 | User, Company, or Role not found |

**Note:** `email` and `password` are NOT updatable through this endpoint.

---

### DELETE /users/{id} 🔒

**Soft disable** — set `active = false`, revoke refresh tokens, **không xóa** bản ghi.

**Success (200):** `ApiResponse` với user `active=false`.

**Errors:** 404 | 409 (đã vô hiệu hóa).

### POST /users/{id}/enable 🔒

Kích hoạt lại (`active = true`). **200** | **404** | **409**.

---

## 3. Companies

### GET /companies/public 🌐 Public

List cửa hàng `active=true` để chọn trước login. `{ id, name, description, address, logo }`.

---

### GET /companies 🔒

List companies with pagination.

**Query Parameters:** `page`, `size`, `sort`, `active` (optional boolean — lọc đang hoạt động / đã vô hiệu).

**Success Response (200):** paginated `CompanyResponse` gồm `active`.

---

### GET /companies/{id} 🔒

**Success (200):** company detail. **404** nếu không tồn tại.

---

### POST /companies 🔒

**Request:** `{ "name", "description?", "address?", "logo?" }` → **201** + Location. **400** validation, **409** trùng tên.

---

### PUT /companies 🔒

**Request:** `{ "id", "name?", "description?", "address?", "logo?" }` → **200**. **404** / **409** trùng tên.

---

### DELETE /companies/{id} 🔒

**Soft disable** — set `active = false`, **không xóa** bản ghi, **không** null `users.company_id`.

**Success (200):**
```json
{
  "statusCode": 200,
  "data": { "id": 2, "name": "...", "active": false },
  "message": "Vô hiệu hóa công ty thành công"
}
```

**Errors:** 404 | 409 (đã bị vô hiệu hóa).

---

### POST /companies/{id}/enable 🔒

Kích hoạt lại (`active = true`). **200** | **404** | **409** (đã active).

---

## 4. Roles

**ADMIN only.**

Pagination: `page` (1-based), `size`, `sort` (`id,asc`).

`ADMIN` / `USER` are system roles (seeder): cannot rename or delete.

### GET /roles 🔒

List all roles with pagination.

**Success Response (200):**
```json
{
  "statusCode": 200,
  "data": {
    "meta": { "page": 1, "pageSize": 10, "pages": 1, "total": 5 },
    "result": [
      {
        "id": 1,
        "name": "ADMIN",
        "description": "Full system access",
        "permissions": [
          { "id": 1, "name": "CREATE_USER", "apiPath": "/api/v1/users", "method": "POST", "module": "USER" },
          { "id": 4, "name": "VIEW_USERS", "apiPath": "/api/v1/users", "method": "GET", "module": "USER" }
        ],
        "createdAt": "20xx-01-01T00:00:00Z",
        "updatedAt": null
      }
    ]
  },
  "message": "Fetch all roles",
  "timestamp": "20xx-02-28T10:00:00"
}
```

---

### GET /roles/{id} 🔒

**Success Response (200):** single role with permissions array (same structure as list item above).

**Errors:**
| Status | When |
|--------|------|
| 404 | Role not found |

---

### POST /roles 🔒

**Request Body:**
```json
{
  "name": "HR",
  "description": "Human resources management",
  "permissionIds": [1, 2, 4, 8]
}
```

**Success Response (201):**
```json
{
  "statusCode": 201,
  "data": {
    "id": 3,
    "name": "HR",
    "description": "Human resources management",
    "permissions": [
      { "id": 1, "name": "CREATE_USER", "apiPath": "/api/v1/users", "method": "POST", "module": "USER" },
      { "id": 2, "name": "UPDATE_USER", "apiPath": "/api/v1/users", "method": "PUT", "module": "USER" },
      { "id": 4, "name": "VIEW_USERS", "apiPath": "/api/v1/users", "method": "GET", "module": "USER" },
      { "id": 8, "name": "VIEW_COMPANIES", "apiPath": "/api/v1/companies", "method": "GET", "module": "COMPANY" }
    ],
    "createdAt": "20xx-02-28T10:00:00Z"
  },
  "message": "Role created",
  "timestamp": "20xx-02-28T10:00:00"
}
```

**Errors:**
| Status | When |
|--------|------|
| 400 | Validation failed (blank name) |
| 404 | One or more Permission IDs not found |
| 409 | Duplicate role name |

---

### PUT /roles 🔒

**Request Body:**
```json
{
  "id": 3,
  "name": "HR",
  "description": "Updated description",
  "permissionIds": [1, 2, 4, 5, 8]
}
```

**Success Response (200):** same structure as POST.

**Note:** `permissionIds` replaces the entire permission list (not additive).

**Errors:**
| Status | When |
|--------|------|
| 400 | Validation failed |
| 404 | Role or Permission not found |
| 409 | Duplicate role name, or rename of system role ADMIN/USER |

---

### DELETE /roles/{id} 🔒

**Success Response (200):**
```json
{
  "statusCode": 200,
  "data": null,
  "message": "Role deleted",
  "timestamp": "20xx-02-28T12:00:00"
}
```

**Note:** Also removes this role from all users who had it (clears join table entries). System roles `ADMIN` / `USER` cannot be deleted.

**Errors:**
| Status | When |
|--------|------|
| 404 | Role not found |
| 409 | System role ADMIN/USER cannot be deleted |

---

## 5. Permissions

**ADMIN only.**

Pagination: `page` (1-based), `size`, `sort`. Unique `(apiPath, method)`.

### GET /permissions 🔒

List all permissions with pagination.

**Success Response (200):**
```json
{
  "statusCode": 200,
  "data": {
    "meta": { "page": 1, "pageSize": 10, "pages": 1, "total": 10 },
    "result": [
      {
        "id": 1,
        "name": "CREATE_USER",
        "apiPath": "/api/v1/users",
        "method": "POST",
        "module": "USER",
        "createdAt": "20xx-01-01T00:00:00Z",
        "updatedAt": null
      }
    ]
  },
  "message": "Fetch all permissions",
  "timestamp": "20xx-02-28T10:00:00"
}
```

---

### GET /permissions/{id} 🔒

**Success Response (200):** single permission (same structure as list item above).

**Errors:**
| Status | When |
|--------|------|
| 404 | Permission not found |

---

### POST /permissions 🔒

**Request Body:**
```json
{
  "name": "CREATE_USER",
  "apiPath": "/api/v1/users",
  "method": "POST",
  "module": "USER"
}
```

**Success Response (201):**
```json
{
  "statusCode": 201,
  "data": {
    "id": 1,
    "name": "CREATE_USER",
    "apiPath": "/api/v1/users",
    "method": "POST",
    "module": "USER",
    "createdAt": "20xx-02-28T10:00:00Z"
  },
  "message": "Permission created",
  "timestamp": "20xx-02-28T10:00:00"
}
```

**Errors:**
| Status | When |
|--------|------|
| 400 | Validation failed (blank name, invalid method) |
| 409 | Duplicate apiPath + method combination |

---

### PUT /permissions 🔒

**Request Body:**
```json
{
  "id": 1,
  "name": "CREATE_USER",
  "apiPath": "/api/v1/users",
  "method": "POST",
  "module": "USER"
}
```

**Success Response (200):** same structure as POST.

**Errors:**
| Status | When |
|--------|------|
| 400 | Validation failed |
| 404 | Permission not found |
| 409 | Duplicate apiPath + method (if changed to existing combo) |

---

### DELETE /permissions/{id} 🔒

**Success Response (200):**
```json
{
  "statusCode": 200,
  "data": null,
  "message": "Permission deleted",
  "timestamp": "20xx-02-28T12:00:00"
}
```

**Note:** Also removes this permission from all roles (clears join table entries).

**Errors:**
| Status | When |
|--------|------|
| 404 | Permission not found |

---

## Error Response Format

All errors follow this structure:

```json
{
  "statusCode": 400,
  "data": null,
  "message": "Detailed error message",
  "timestamp": "20xx-02-28T10:00:00"
}
```

Validation errors include field details:

```json
{
  "statusCode": 400,
  "data": {
    "email": "Invalid email format",
    "password": "Password must be 8-100 characters",
    "name": "Name is required"
  },
  "message": "Validation failed",
  "timestamp": "20xx-02-28T10:00:00"
}
```

---

## 6. Files

### POST /files 🔒

Upload a single file to the server. The returned `fileName` is then used to update
`avatar` (user) or `logo` (company) via their respective PUT endpoints.

**Request:** `multipart/form-data`

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| file | file | yes | The file to upload |
| folder | string | yes | Target sub-folder: `avatars` or `logos` |

**Validation rules (rejected with 400 if violated):**

| Rule | Constraint |
|------|-----------|
| File name | Must not be blank, no special characters except `-` `_` `.` |
| Allowed extensions | `jpg`, `jpeg`, `png`, `gif`, `webp` |
| Max file size | 5 MB (5,242,880 bytes) |
| Allowed folders | `avatars`, `logos` |

**Success Response (201):**
```json
{
  "statusCode": 201,
  "data": {
    "fileName": "1709123456789_avatar.jpg",
    "folder": "avatars",
    "fileUrl": "/uploads/avatars/1709123456789_avatar.jpg",
    "size": 204800,
    "uploadedAt": "20xx-02-28T10:00:00Z"
  },
  "message": "File uploaded",
  "timestamp": "20xx-02-28T10:00:00"
}
```

**Errors:**
| Status | When |
|--------|------|
| 400 | No file provided |
| 400 | File name is blank or contains invalid characters |
| 400 | File extension not allowed (only jpg/jpeg/png/gif/webp) |
| 400 | File size exceeds 5 MB |
| 400 | Folder value is not `avatars` or `logos` |

**Usage flow:**

```
1. POST /api/v1/files  →  { fileName: "1709123456789_avatar.jpg", ... }
2a. PUT /api/v1/users  →  { id: 1, ..., avatar: "1709123456789_avatar.jpg" }
2b. PUT /api/v1/companies  →  { id: 1, ..., logo: "1709123456789_logo.png" }
```

**File storage:**
- Files are saved under `{upload-dir}/{folder}/` on the server file system
- `upload-dir` is configured via `app.upload.base-dir` in `application.yml`
- Stored file name = `{epochMillis}_{sanitizedOriginalName}` to avoid collisions
- Served as static resources at `/uploads/**`

---

## Endpoint Summary

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | /auth/login | 🔓 | Login (access + refresh cookie) |
| POST | /auth/refresh | 🔓 | Rotate refresh (cookie or body) |
| GET | /auth/me | 🔒 | Current user |
| POST | /auth/logout | 🔒 | Logout (revoke + clear cookie) |
| GET | /users | 🔒 | List users |
| GET | /users/{id} | 🔒 | Get user |
| POST | /users | 🔒 | Create user |
| PUT | /users | 🔒 | Update user |
| DELETE | /users/{id} | 🔒 | Soft disable user |
| POST | /users/{id}/enable | 🔒 | Re-enable user |
| GET | /companies | 🔒 | List companies |
| GET | /companies/{id} | 🔒 | Get company |
| POST | /companies | 🔒 | Create company |
| PUT | /companies | 🔒 | Update company |
| DELETE | /companies/{id} | 🔒 | Soft disable company |
| POST | /companies/{id}/enable | 🔒 | Re-enable company |
| GET | /roles | 🔒 | List roles |
| GET | /roles/{id} | 🔒 | Get role |
| POST | /roles | 🔒 | Create role |
| PUT | /roles | 🔒 | Update role |
| DELETE | /roles/{id} | 🔒 | Delete role |
| GET | /permissions | 🔒 | List permissions |
| GET | /permissions/{id} | 🔒 | Get permission |
| POST | /permissions | 🔒 | Create permission |
| PUT | /permissions | 🔒 | Update permission |
| DELETE | /permissions/{id} | 🔒 | Delete permission |
| POST | /files | 🔒 | Upload file (avatar / logo) |
| GET | /cashbook | 🔒 | List cash entries (filter) |
| GET | /cashbook/stats | 🔒 | Cashbook stats |
| GET | /cashbook/categories | 🔒 | Expense categories for filter |
| GET | /cashbook/export/pdf | 🔒 | Export PDF |
| GET | /cashbook/{id} | 🔒 | Cash entry detail |
| POST | /cashbook | 🔒 | Create MANUAL entry (201) |
| PATCH | /cashbook/{id}/note | 🔒 | Update note |
| PATCH | /cashbook/{id}/checked | 🔒 | Toggle reconciled |
| DELETE | /cashbook/{id} | 🔒 | Delete MANUAL (204) |
| GET | /customers | 🔒 | List customers |
| GET | /customers/{id} | 🔒 | Get customer |
| POST | /customers | 🔒 | Create customer |
| PUT | /customers | 🔒 | Update customer |
| DELETE | /customers/{id} | 🔒 | Soft disable customer |
| POST | /customers/{id}/enable | 🔒 | Re-enable customer |
| GET | /workers | 🔒 | List workers |
| GET | /workers/{id} | 🔒 | Get worker |
| POST | /workers | 🔒 | Create worker |
| PUT | /workers | 🔒 | Update worker |
| DELETE | /workers/{id} | 🔒 | Soft disable worker |
| POST | /workers/{id}/enable | 🔒 | Re-enable worker |
| GET | /wages | 🔒 | List wage entries |
| POST | /wages | 🔒 | Create wage entry |
| PUT | /wages | 🔒 | Update unpaid wage |
| DELETE | /wages/{id} | 🔒 | Delete unpaid wage (204) |
| GET | /debts | 🔒 | List debt entries |
| POST | /debts | 🔒 | Create debt entry |
| GET | /expenses | 🔒 | List expenses |
| POST | /expenses | 🔒 | Create expense |
| POST | /expenses/{id}/cancel | 🔒 | Cancel expense |
| GET | /payslips | 🔒 | List payslips |
| POST | /payslips | 🔒 | Create DRAFT payslip |
| PUT | /payslips | 🔒 | Update DRAFT |
| POST | /payslips/{id}/confirm | 🔒 | Confirm |
| POST | /payslips/{id}/pay | 🔒 | Pay + ledger |
| POST | /payslips/{id}/cancel | 🔒 | Cancel |

---

## 9. Cashbook (Sổ quỹ)

Base path: `/api/v1/cashbook`. Timezone mặc định filter: `Asia/Ho_Chi_Minh`. Chi tiết: `docs/features/cashbook-requirements.md`.

### GET /cashbook

Query: `page`, `size`, `sort`, `fromDate`, `toDate`, `direction`, `categoryId`, `refType`, `refId`, `createdBy`, `checked`, `amountMin`, `amountMax`, `q`.

**Success (200):** `ApiResponse<PaginatedResult<CashEntryResponse>>`.

### GET /cashbook/stats

Cùng filter ngày/loại… — trả `totalIn`, `totalOut`, `balance`, `countIn`, `countOut`, `byCategory[]`.

### GET /cashbook/export/pdf

Cùng filter — `Content-Type: application/pdf`.

### POST /cashbook

Body: `{ entryDate?, direction, amount, categoryId, description?, note? }` → **201** MANUAL.

### PATCH /cashbook/{id}/note | /checked

Cập nhật ghi chú / đối chiếu → **200**.

### DELETE /cashbook/{id}

Chỉ MANUAL → **204**; dòng hệ thống → **409**.

---

## 10. Customers

Base path: `/api/v1/customers`. Soft disable (`is_active`). `currentDebt` chỉ đọc.

### GET /customers

Query: `page`, `size`, `sort`, `active`, `q` (name/phone/note/address).

**200:** `PaginatedResult<CustomerResponse>`.

### GET /customers/{id}

**200** | **404**.

### POST /customers

Body: `{ name, phone?, address?, note? }` → **201**. Phone unique nếu có. `currentDebt = 0`.

### PUT /customers

Body: `{ id, name?, phone?, address?, note? }` → **200**.

### DELETE /customers/{id}

Soft disable → **200** + `active=false`. **409** nếu đã tắt.

### POST /customers/{id}/enable

**200** + `active=true`.

---

## 11. Workers

Base path: `/api/v1/workers`. Soft disable. `currentAdvance` chỉ đọc.

### GET /workers
Query: `page`, `size`, `sort`, `active`, `q`.

### POST /workers
Body: `{ name, phone?, address?, jobTitle?, wageType, defaultUnitRate, hireDate?, note? }` → **201**.

### PUT /workers
Body: `{ id, name?, phone?, address?, jobTitle?, wageType?, defaultUnitRate?, hireDate?, note? }`.

### DELETE /workers/{id} | POST /workers/{id}/enable
Soft disable / enable.

---

## 12. Wages (WageEntry)

Base path: `/api/v1/wages`. Không tạo CashEntry. `amount = quantity × unitRate`.

### GET /wages
Query: `workerId`, `fromDate`, `toDate`, `unpaidOnly`.

### POST /wages
Body: `{ workerId, workDate, wageType?, quantity, unitRate?, note? }` → **201**.

### PUT /wages
Chỉ khi chưa gắn payslip. Body: `{ id, workDate?, wageType?, quantity?, unitRate?, note? }`.

### DELETE /wages/{id}
**204** nếu chưa gắn payslip; **409** nếu đã gắn.

---

## 13. Debts (DebtEntry)

Base path: `/api/v1/debts`. Append-only (không PUT/DELETE).

### GET /debts
Query: `customerId`, `workerId`, `fromDate`, `toDate`, `entryType`.

### POST /debts
Body: `{ customerId?, workerId?, entryType, direction?, amount, entryDate, note? }` → **201**.
XOR đúng 1 trong customer/worker. CHARGE→INCREASE, PAYMENT→DECREASE, ADJUST cần `direction`.
Auto CashEntry: customer PAYMENT → IN `CUSTOMER_REPAY`; worker CHARGE → OUT `WORKER_ADVANCE`.

---

## 14. Expenses

Base path: `/api/v1/expenses`.

### POST /expenses
Body: `{ categoryId, amount, expenseDate, note? }` → **201** POSTED + CashEntry OUT. Cấm category `WAGE`.

### POST /expenses/{id}/cancel
CANCELLED + CashEntry IN đảo.

---

## 15. Payslips

Base path: `/api/v1/payslips`.

### POST /payslips
Body: `{ workerId, periodStart, periodEnd, advanceDeducted?, otherDeduction?, note? }` → **201** DRAFT.
Gộp wage chưa quyết toán trong kỳ; `net = gross − advance − other`.

### PUT /payslips
Chỉ DRAFT: `{ id, advanceDeducted?, otherDeduction?, note? }`.

### POST /payslips/{id}/confirm | /pay | /cancel
Confirm → CONFIRMED. Pay → PAID + CashEntry OUT `WAGE` + Debt PAYMENT trừ ứng. Cancel: gỡ wage; nếu đã PAID thì đảo quỹ + hoàn ứng.
