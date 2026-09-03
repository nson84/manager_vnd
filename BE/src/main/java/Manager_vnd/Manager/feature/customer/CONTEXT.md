# Customer Feature — Backend

## Scope

CRUD khách hàng. Soft disable (`is_active`), không xóa cứng. `currentDebt` chỉ đọc (cache từ DebtEntry).

## Endpoints `/api/v1/customers`

| Method | Path | Notes |
|--------|------|-------|
| GET | `/` | `active`, `q` (name/phone/note/address) |
| GET | `/{id}` | |
| POST | `/` | 201, debt=0 |
| PUT | `/` | body có `id` |
| DELETE | `/{id}` | soft disable |
| POST | `/{id}/enable` | enable |

## Rules

- Phone unique khi không null; blank → null
- Không cho set `currentDebt` từ API CRUD
