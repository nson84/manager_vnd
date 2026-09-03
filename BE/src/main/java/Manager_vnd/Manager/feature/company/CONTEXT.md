# Company Feature — Backend

## Scope

CRUD công ty. **Không xóa cứng** — `DELETE` set `is_active = false`. Kích hoạt lại: `POST /{id}/enable`.

## Endpoints

| Method | Path | Notes |
|--------|------|-------|
| GET | `/api/v1/companies` | `active` filter optional |
| GET | `/api/v1/companies/{id}` | |
| POST | `/api/v1/companies` | 201 |
| PUT | `/api/v1/companies` | body có `id` |
| DELETE | `/api/v1/companies/{id}` | soft disable → 200 + body |
| POST | `/api/v1/companies/{id}/enable` | active = true |

## Response

`ResponseEntity<ApiResponse<T>>` — message tiếng Việt.
