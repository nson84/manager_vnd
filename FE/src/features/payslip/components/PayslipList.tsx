import type { PayslipResponse } from '../types/payslip.types'
import { PAYSLIP_STATUS_LABELS, formatVnd } from '../types/payslip.types'

interface PayslipListProps {
  payslips: PayslipResponse[]
  isLoading: boolean
  onConfirm: (payslip: PayslipResponse) => void
  onPay: (payslip: PayslipResponse) => void
  onCancel: (payslip: PayslipResponse) => void
}

export function PayslipList({
  payslips,
  isLoading,
  onConfirm,
  onPay,
  onCancel,
}: PayslipListProps) {
  if (isLoading) {
    return <p className="customer-muted">Đang tải...</p>
  }

  if (payslips.length === 0) {
    return <p className="customer-muted">Chưa có phiếu lương nào.</p>
  }

  return (
    <div className="customer-table-wrap">
      <table className="customer-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Thợ</th>
            <th>Kỳ</th>
            <th>Gross</th>
            <th>Trừ ứng</th>
            <th>Khác</th>
            <th>Net</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {payslips.map((payslip) => (
            <tr key={payslip.id}>
              <td>{payslip.id}</td>
              <td>
                #{payslip.workerId} {payslip.workerName}
                {payslip.note && <div className="customer-muted">{payslip.note}</div>}
              </td>
              <td>
                {payslip.periodStart} → {payslip.periodEnd}
              </td>
              <td>{formatVnd(Number(payslip.grossAmount))}</td>
              <td>{formatVnd(Number(payslip.advanceDeducted))}</td>
              <td>{formatVnd(Number(payslip.otherDeduction))}</td>
              <td>{formatVnd(Number(payslip.netAmount))}</td>
              <td>
                <span
                  className={`customer-badge ${
                    payslip.status === 'PAID'
                      ? 'on'
                      : payslip.status === 'CANCELLED'
                        ? 'off'
                        : 'on'
                  }`}
                >
                  {PAYSLIP_STATUS_LABELS[payslip.status]}
                </span>
              </td>
              <td className="customer-actions">
                {payslip.status === 'DRAFT' && (
                  <button type="button" className="success" onClick={() => onConfirm(payslip)}>
                    Confirm
                  </button>
                )}
                {payslip.status === 'CONFIRMED' && (
                  <button type="button" className="primary" onClick={() => onPay(payslip)}>
                    Pay
                  </button>
                )}
                {payslip.status !== 'CANCELLED' && (
                  <button type="button" className="danger" onClick={() => onCancel(payslip)}>
                    Cancel
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
