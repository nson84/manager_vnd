# Worker

CRUD thợ / công nhân. Soft disable (`is_active`). Không có login.

## Rules
- `currentAdvance` chỉ đọc — cập nhật qua DebtEntry / Payslip
- Phone unique khi non-null

## API
`/api/v1/workers` — GET list, GET id, POST, PUT, DELETE disable, POST enable
