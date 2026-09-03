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
  active: boolean
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
  description?: string | null
  address?: string | null
  logo?: string | null
  active: boolean
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

Source: `FE/src/features/company/types/company.types.ts`

---

## Customer

```typescript
export interface CustomerResponse {
  id: number
  name: string
  phone?: string | null
  address?: string | null
  note?: string | null
  active: boolean
  currentDebt: number
  createdAt: string
  updatedAt?: string | null
}

export interface CreateCustomerRequest {
  name: string
  phone?: string
  address?: string
  note?: string
}

export interface UpdateCustomerRequest {
  id: number
  name?: string
  phone?: string
  address?: string
  note?: string
}
```

Source: `FE/src/features/customer/types/customer.types.ts`

---

## Worker

```typescript
export type WageType = 'DAILY' | 'HOURLY' | 'PIECE'

export interface WorkerResponse {
  id: number
  name: string
  phone?: string | null
  address?: string | null
  jobTitle?: string | null
  wageType: WageType
  defaultUnitRate: number
  hireDate?: string | null
  active: boolean
  note?: string | null
  currentAdvance: number
  createdAt: string
  updatedAt?: string | null
}
```

Source: `FE/src/features/worker/types/worker.types.ts`

---

## Wage

```typescript
export interface WageEntryResponse {
  id: number
  workerId: number
  workerName: string
  workDate: string
  wageType: WageType
  quantity: number
  unitRate: number
  amount: number
  note?: string | null
  payslipId?: number | null
  createdById: number
  createdAt: string
  updatedAt?: string | null
}
```

Source: `FE/src/features/wage/types/wage.types.ts`

---

## Debt

```typescript
export type DebtEntryType = 'CHARGE' | 'PAYMENT' | 'ADJUST'
export type LedgerDirection = 'INCREASE' | 'DECREASE'

export interface DebtEntryResponse {
  id: number
  customerId?: number | null
  customerName?: string | null
  workerId?: number | null
  workerName?: string | null
  entryType: DebtEntryType
  direction: LedgerDirection
  amount: number
  entryDate: string
  note?: string | null
  refType?: string | null
  refId?: number | null
  createdById: number
  createdAt: string
}
```

Source: `FE/src/features/debt/types/debt.types.ts`

---

## Expense

```typescript
export type ExpenseStatus = 'POSTED' | 'CANCELLED'

export interface ExpenseResponse {
  id: number
  categoryId: number
  categoryCode: string
  categoryName: string
  amount: number
  expenseDate: string
  note?: string | null
  status: ExpenseStatus
  createdById: number
  createdAt: string
  updatedAt?: string | null
}
```

Source: `FE/src/features/expense/types/expense.types.ts`

---

## Payslip

```typescript
export type PayslipStatus = 'DRAFT' | 'CONFIRMED' | 'PAID' | 'CANCELLED'

export interface PayslipResponse {
  id: number
  workerId: number
  workerName: string
  periodStart: string
  periodEnd: string
  grossAmount: number
  advanceDeducted: number
  otherDeduction: number
  netAmount: number
  status: PayslipStatus
  paidAt?: string | null
  note?: string | null
  createdById: number
  createdAt: string
  updatedAt?: string | null
}
```

Source: `FE/src/features/payslip/types/payslip.types.ts`

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

export interface UpdateRoleRequest {
  id: number
  name?: string
  description?: string
  permissionIds?: number[]
}

export interface CreatePermissionRequest {
  name: string
  apiPath: string
  method: string
  module: string
}

export interface UpdatePermissionRequest {
  id: number
  name?: string
  apiPath?: string
  method?: string
  module?: string
}
```

Source: `FE/src/features/role/types/role.types.ts`, `FE/src/features/permission/types/permission.types.ts`

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

## Cashbook

```typescript
export type CashDirection = 'IN' | 'OUT'
export type CashRefType = 'EXPENSE' | 'PAYSLIP' | 'WORKER_ADVANCE' | 'CUSTOMER_PAYMENT' | 'MANUAL'

export interface CashEntryResponse {
  id: number
  entryDate: string
  direction: CashDirection
  amount: number
  category: { id: number; code: string; name: string } | null
  description?: string | null
  note?: string | null
  checked: boolean
  checkedAt?: string | null
  checkedBy?: { id: number; name: string } | null
  refType: CashRefType
  refId?: number | null
  createdBy: { id: number; name: string }
  createdAt: string
  updatedAt?: string | null
}

export interface CashStatsResponse {
  totalIn: number
  totalOut: number
  balance: number
  countIn: number
  countOut: number
  byCategory: Array<{
    categoryId: number
    categoryName: string
    direction: CashDirection
    total: number
  }>
}
```

Source: `FE/src/features/cashbook/types/cashbook.types.ts`

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
| `CashEntryResponse` | `CashEntryResponse` | BE feature/cashbook/dto |
| `CashStatsResponse` | `CashStatsResponse` | BE feature/cashbook/dto |
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
- Cashbook dates (`entryDate`) are `yyyy-MM-dd`; timestamps format on FE with `Asia/Ho_Chi_Minh`