# Cashbook Feature — Frontend

## Scope

Sổ quỹ UI: stats, filter (ngày VN), table (tick/note), detail, tạo MANUAL, xuất PDF.

## API

`cashbookService` → `/api/v1/cashbook`

## Notes

- Mount qua tab trong `App.tsx` (chưa React Router)
- Tạo MANUAL cần User id=1 tồn tại trên BE (stub actor)
- PDF download blob từ BE OpenPDF
