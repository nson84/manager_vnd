# Yêu cầu chi tiết — Sổ quỹ (Cashbook)

> Nguồn triển khai. Domain: ADR-003 + DATABASE.md `cash_entries`.

## Mục tiêu

Quản lý sổ quỹ tiền mặt: list + filter, thống kê, chi tiết, ghi chú, đánh dấu đối chiếu, xuất PDF, tạo phiếu MANUAL. Timezone: `Asia/Ho_Chi_Minh`.

## Quyết định

| Điểm | Quyết định |
|------|------------|
| Đánh dấu tích | `checked` = đã đối chiếu, lưu DB |
| Ghi chú | Cột `note` (khác `description`) |
| Ghi từ UI | Chỉ POST MANUAL |
| Xóa | Chỉ MANUAL → 204; hệ thống → 409 |
| Auth | permitAll; `createdBy` stub user id=1 nếu chưa JWT |

## Cột bổ sung

`note`, `checked`, `checked_at`, `checked_by`, `updated_at` — xem DATABASE.md.

## API `/api/v1/cashbook`

| Method | Path | HTTP success |
|--------|------|--------------|
| GET | `/` | 200 paginated + filters |
| GET | `/stats` | 200 stats |
| GET | `/{id}` | 200 detail |
| POST | `/` | 201 MANUAL |
| PATCH | `/{id}/note` | 200 |
| PATCH | `/{id}/checked` | 200 |
| DELETE | `/{id}` | 204 MANUAL only |
| GET | `/export/pdf` | 200 PDF binary |

Response JSON: `ResponseEntity<ApiResponse<T>>` (trừ DELETE 204, PDF).

## FE

`features/cashbook`: stats, filter, table (tick + note), detail modal, create MANUAL, export PDF.
