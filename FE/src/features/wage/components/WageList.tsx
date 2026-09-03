import type { WageEntryResponse } from '../types/wage.types'
import { WAGE_TYPE_LABELS, formatVnd } from '../types/wage.types'

interface WageListProps {
  entries: WageEntryResponse[]
  isLoading: boolean
  onEdit: (entry: WageEntryResponse) => void
  onDelete: (entry: WageEntryResponse) => void
}

export function WageList({ entries, isLoading, onEdit, onDelete }: WageListProps) {
  if (isLoading) {
    return <p className="customer-muted">Đang tải...</p>
  }

  if (entries.length === 0) {
    return <p className="customer-muted">Chưa có ghi công nào.</p>
  }

  return (
    <div className="customer-table-wrap">
      <table className="customer-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Thợ</th>
            <th>Ngày</th>
            <th>Loại</th>
            <th>SL</th>
            <th>Đơn giá</th>
            <th>Thành tiền</th>
            <th>Phiếu lương</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {entries.map((entry) => (
            <tr key={entry.id}>
              <td>{entry.id}</td>
              <td>
                <div>
                  #{entry.workerId} {entry.workerName}
                </div>
                {entry.note && <div className="customer-muted">{entry.note}</div>}
              </td>
              <td>{entry.workDate}</td>
              <td>{WAGE_TYPE_LABELS[entry.wageType]}</td>
              <td>{entry.quantity}</td>
              <td>{formatVnd(Number(entry.unitRate))}</td>
              <td>{formatVnd(Number(entry.amount))}</td>
              <td>{entry.payslipId ?? '—'}</td>
              <td className="customer-actions">
                {!entry.payslipId && (
                  <>
                    <button type="button" onClick={() => onEdit(entry)}>
                      Sửa
                    </button>
                    <button type="button" className="danger" onClick={() => onDelete(entry)}>
                      Xóa
                    </button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
