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
| `login` | POST | `/auth/login` | `LoginRequest` `{ companyId, email, password }` | `TokenResponse` |
| `getMe` | GET | `/auth/me` | — | `AuthUser` |
| `logout` | POST | `/auth/logout` | — | `null` |

### Notes
- Login: store `accessToken` in AuthContext, refresh cookie set by BE automatically
- Refresh: called by apiClient on 401, cookie sent automatically
- Logout: clear AuthContext, BE clears cookie

---

## 2. User Service — `src/features/user/services/userService.ts`

| Function | Method | BE Endpoint | Request | Response |
|----------|--------|-------------|---------|----------|
| `getAll` | GET | `/users?page&size&sort&active` | — | `PaginatedResult<UserResponse>` |
| `getById` | GET | `/users/{id}` | — | `UserResponse` |
| `create` | POST | `/users` | `CreateUserRequest` | `UserResponse` |
| `update` | PUT | `/users` | `UpdateUserRequest` | `UserResponse` |
| `disable` | DELETE | `/users/{id}` | — | `UserResponse` (`active=false`) |
| `enable` | POST | `/users/{id}/enable` | — | `UserResponse` (`active=true`) |

---

## 2b. Cashbook Service — `src/features/cashbook/services/cashbookService.ts`

| Function | Method | BE Endpoint | Request | Response |
|----------|--------|-------------|---------|----------|
| `getAll` | GET | `/cashbook?...filters` | — | `PaginatedResult<CashEntryResponse>` |
| `getStats` | GET | `/cashbook/stats?...` | — | `CashStatsResponse` |
| `listCategories` | GET | `/cashbook/categories` | — | `CategorySummary[]` |
| `getById` | GET | `/cashbook/{id}` | — | `CashEntryResponse` |
| `createManual` | POST | `/cashbook` | `CreateManualCashEntryRequest` | `CashEntryResponse` (201) |
| `updateNote` | PATCH | `/cashbook/{id}/note` | `{ note }` | `CashEntryResponse` |
| `updateChecked` | PATCH | `/cashbook/{id}/checked` | `{ checked }` | `CashEntryResponse` |
| `delete` | DELETE | `/cashbook/{id}` | — | `204` |
| `exportPdf` | GET | `/cashbook/export/pdf?...` | — | PDF blob download |

---

## 3. Company Service — `src/features/company/services/companyService.ts`

| Function | Method | BE Endpoint | Request | Response |
|----------|--------|-------------|---------|----------|
| `listPublic` | GET | `/companies/public` | — | `PublicCompany[]` |
| `getAll` | GET | `/companies?page&size&sort&active` | — | `PaginatedResult<CompanyResponse>` |
| `getById` | GET | `/companies/{id}` | — | `CompanyResponse` |
| `create` | POST | `/companies` | `CreateCompanyRequest` | `CompanyResponse` (201) |
| `update` | PUT | `/companies` | `UpdateCompanyRequest` | `CompanyResponse` |
| `disable` | DELETE | `/companies/{id}` | — | `CompanyResponse` (`active=false`) |
| `enable` | POST | `/companies/{id}/enable` | — | `CompanyResponse` (`active=true`) |

---

## 3b. Customer Service — `src/features/customer/services/customerService.ts`

| Function | Method | BE Endpoint | Request | Response |
|----------|--------|-------------|---------|----------|
| `getAll` | GET | `/customers?page&size&sort&active&q` | — | `PaginatedResult<CustomerResponse>` |
| `getById` | GET | `/customers/{id}` | — | `CustomerResponse` |
| `create` | POST | `/customers` | `CreateCustomerRequest` | `CustomerResponse` (201) |
| `update` | PUT | `/customers` | `UpdateCustomerRequest` | `CustomerResponse` |
| `disable` | DELETE | `/customers/{id}` | — | `CustomerResponse` (`active=false`) |
| `enable` | POST | `/customers/{id}/enable` | — | `CustomerResponse` (`active=true`) |

---

## 3c. Worker Service — `src/features/worker/services/workerService.ts`

| Function | Method | BE Endpoint | Request | Response |
|----------|--------|-------------|---------|----------|
| `getAll` | GET | `/workers?page&size&sort&active&q` | — | `PaginatedResult<WorkerResponse>` |
| `getById` | GET | `/workers/{id}` | — | `WorkerResponse` |
| `create` | POST | `/workers` | `CreateWorkerRequest` | `WorkerResponse` (201) |
| `update` | PUT | `/workers` | `UpdateWorkerRequest` | `WorkerResponse` |
| `disable` | DELETE | `/workers/{id}` | — | `WorkerResponse` |
| `enable` | POST | `/workers/{id}/enable` | — | `WorkerResponse` |

---

## 3d. Wage Service — `src/features/wage/services/wageService.ts`

| Function | Method | BE Endpoint | Request | Response |
|----------|--------|-------------|---------|----------|
| `getAll` | GET | `/wages?page&size&sort&workerId&unpaidOnly` | — | `PaginatedResult<WageEntryResponse>` |
| `create` | POST | `/wages` | `CreateWageEntryRequest` | `WageEntryResponse` (201) |
| `update` | PUT | `/wages` | `UpdateWageEntryRequest` | `WageEntryResponse` |
| `delete` | DELETE | `/wages/{id}` | — | `204` |

---

## 3e. Debt Service — `src/features/debt/services/debtService.ts`

| Function | Method | BE Endpoint | Request | Response |
|----------|--------|-------------|---------|----------|
| `getAll` | GET | `/debts?page&size&sort` | — | `PaginatedResult<DebtEntryResponse>` |
| `create` | POST | `/debts` | `CreateDebtEntryRequest` | `DebtEntryResponse` (201) |

---

## 3f. Expense Service — `src/features/expense/services/expenseService.ts`

| Function | Method | BE Endpoint | Request | Response |
|----------|--------|-------------|---------|----------|
| `getAll` | GET | `/expenses?page&size&sort` | — | `PaginatedResult<ExpenseResponse>` |
| `create` | POST | `/expenses` | `CreateExpenseRequest` | `ExpenseResponse` (201) |
| `cancel` | POST | `/expenses/{id}/cancel` | — | `ExpenseResponse` |

Categories: `cashbookService.listCategories()` → `GET /cashbook/categories`.

---

## 3g. Payslip Service — `src/features/payslip/services/payslipService.ts`

| Function | Method | BE Endpoint | Request | Response |
|----------|--------|-------------|---------|----------|
| `getAll` | GET | `/payslips?page&size&sort` | — | `PaginatedResult<PayslipResponse>` |
| `create` | POST | `/payslips` | `CreatePayslipRequest` | `PayslipResponse` (201 DRAFT) |
| `confirm` | POST | `/payslips/{id}/confirm` | — | `PayslipResponse` |
| `pay` | POST | `/payslips/{id}/pay` | — | `PayslipResponse` |
| `cancel` | POST | `/payslips/{id}/cancel` | — | `PayslipResponse` |

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
