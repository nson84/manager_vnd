# Company Feature — Frontend

## Scope

CRUD công ty qua `/api/v1/companies`. **Vô hiệu hóa** (`DELETE`) / **Kích hoạt** (`POST /{id}/enable`) — không xóa cứng.

## Files

```
features/company/
  types/company.types.ts
  services/companyService.ts
  hooks/useCompanies.ts
  components/ CompanyList, CompanyForm, company.css
  pages/CompaniesPage.tsx
```

## UI

- Filter active / inactive
- Form tạo/sửa
- Bảng: Sửa | Vô hiệu hóa | Kích hoạt
