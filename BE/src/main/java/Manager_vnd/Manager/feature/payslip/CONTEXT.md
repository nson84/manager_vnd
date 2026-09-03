# Payslip

Phiếu lương: DRAFT → CONFIRMED → PAID / CANCELLED.

## Pay
- CashEntry OUT category WAGE = netAmount
- DebtEntry PAYMENT worker = advanceDeducted (không post cash)

## Cancel
- Chưa PAID: gỡ wage.payslip_id
- Đã PAID: đảo quỹ + hoàn advance (CHARGE không cash)

## API
`/api/v1/payslips`
