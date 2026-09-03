import type { DebtEntryResponse } from '../types/debt.types'
import { DIRECTION_LABELS, ENTRY_TYPE_LABELS, formatVnd } from '../types/debt.types'

interface DebtListProps {
  entries: DebtEntryResponse[]
  isLoading: boolean
}

export function DebtList({ entries, isLoading }: DebtListProps) {
  if (isLoading) {
    return <p className="customer-muted">Đang tải...</p>
  }

  if (entries.length === 0) {
    return <p className="customer-muted">Chưa có bút toán công nợ.</p>
  }

  return (
    <div className="customer-table-wrap">
      <table className="customer-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Đối tượng</th>
            <th>Loại</th>
            <th>Hướng</th>
            <th>Số tiền</th>
            <th>Ngày</th>
            <th>Ghi chú</th>
          </tr>
        </thead>
        <tbody>
          {entries.map((entry) => {
            const party = entry.customerId
              ? `KH #${entry.customerId} ${entry.customerName ?? ''}`
              : `Thợ #${entry.workerId} ${entry.workerName ?? ''}`
            return (
              <tr key={entry.id}>
                <td>{entry.id}</td>
                <td>{party.trim()}</td>
                <td>{ENTRY_TYPE_LABELS[entry.entryType]}</td>
                <td>{DIRECTION_LABELS[entry.direction]}</td>
                <td>{formatVnd(Number(entry.amount))}</td>
                <td>{entry.entryDate}</td>
                <td>{entry.note || '—'}</td>
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}
