# Cashbook Feature — Backend

## Endpoints

`/api/v1/cashbook` — list, stats, categories, export/pdf, CRUD MANUAL, PATCH note/checked.

## Rules

- Timezone `Asia/Ho_Chi_Minh` via `VietnamTime`
- Delete chỉ MANUAL → 204
- Actor stub User id=1
- PDF: OpenPDF (`CashbookPdfExporter`)

## Docs

`BE/docs/features/cashbook-requirements.md`
