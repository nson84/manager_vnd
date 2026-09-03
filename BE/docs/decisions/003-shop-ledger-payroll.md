# ADR-003: Sổ quỹ thống nhất + công nợ + công thợ

## Status
Accepted

## Context

Cửa hàng cần cùng lúc:
- Ghi nợ / thu nợ khách
- Ghi công thợ, ứng lương, trả lương
- Thống kê chi tiêu theo thời gian và loại

Spring Data JPA dùng `ddl-auto=update`: bảng sinh từ entity, không viết SQL CREATE tay.

Nếu mỗi nghiệp vụ một bảng tiền riêng (phiếu lương, phiếu chi, phiếu thu…), báo cáo chi tiêu phải `UNION` nhiều bảng, dễ lệch số.

## Decision

1. **Chứng từ nghiệp vụ** (user tạo/sửa): `Customer`, `Worker`, `WageEntry`, `Payslip`, `Expense`, `DebtEntry`
2. **Sổ quỹ `CashEntry`** là nguồn sự thật cho tiền mặt **vào/ra**. Mọi thống kê chi tiêu chỉ đọc bảng này.
3. **Sổ công nợ `DebtEntry`** là nguồn sự thật cho số dư khách và số ứng của thợ. Không sửa tay `currentDebt`.
4. **Công nhân (`Worker`) ≠ `User`**. Thợ không đăng nhập. `User` là chủ/nhân viên cửa hàng ghi phiếu.
5. Tiền dùng `BigDecimal` + `DECIMAL(15,2)`. Enum dùng `STRING`.

## Rules

- Ghi công (`WageEntry`) **không** tạo `CashEntry` — chưa chi tiền.
- Trả lương (`Payslip` → PAID) mới tạo `CashEntry` OUT = `netAmount`.
- Ứng lương: `DebtEntry` CHARGE (thợ) + `CashEntry` OUT.
- Ghi nợ khách: chỉ `DebtEntry` CHARGE — chưa có tiền mặt.
- Khách trả nợ: `DebtEntry` PAYMENT + `CashEntry` IN.
- Hủy chứng từ: không xóa dòng sổ; đảo bút (`ADJUST` / `CashEntry` ngược dấu) hoặc đánh dấu `CANCELLED`.

## Consequences

- Báo cáo chi tiêu: `GROUP BY category_id, DATE` trên `cash_entries` where `direction = OUT`.
- Service ghi chứng từ và sổ quỹ **trong cùng `@Transactional`**.
- `ddl-auto=update` không tạo CHECK (ví dụ XOR customer/worker). Validate trong service.
