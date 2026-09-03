# DebtEntry

Sổ công nợ append-only. Cập nhật `currentDebt` / `currentAdvance`.

## Cash auto-post
- Customer PAYMENT → CashEntry IN `CUSTOMER_REPAY`
- Worker CHARGE → CashEntry OUT `WORKER_ADVANCE`
- ADJUST: chỉ cache, không quỹ (v1)

## API
`/api/v1/debts` — GET, GET/{id}, POST
