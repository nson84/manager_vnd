import type { CashEntryResponse } from '../types/cashbook.types'
import { formatVnd } from '../types/cashbook.types'

interface CashEntryTableProps {
  entries: CashEntryResponse[]
  isLoading: boolean
  onToggleChecked: (entry: CashEntryResponse) => void
  onEditNote: (entry: CashEntryResponse) => void
  onDetail: (entry: CashEntryResponse) => void
  onDelete: (entry: CashEntryResponse) => void
}

export function CashEntryTable({
  entries,
  isLoading,
  onToggleChecked,
  onEditNote,
  onDetail,
  onDelete,
}: CashEntryTableProps) {
  if (isLoading) {
    return <p className="cash-muted">Đang tải danh sách...</p>
  }

  if (entries.length === 0) {
    return <p className="cash-muted">Không có dòng sổ quỹ trong bộ lọc hiện tại.</p>
  }

  return (
    <div className="cash-table-wrap">
      <table className="cash-table">
        <thead>
          <tr>
            <th>Đối chiếu</th>
            <th>Ngày</th>
            <th>Chiều</th>
            <th>Số tiền</th>
            <th>Loại</th>
            <th>Mô tả</th>
            <th>Ghi chú</th>
            <th>Nguồn</th>
            <th>Người tạo</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {entries.map((entry) => (
            <tr key={entry.id}>
              <td>
                <input
                  type="checkbox"
                  checked={entry.checked}
                  onChange={() => onToggleChecked(entry)}
                  aria-label={`Đối chiếu dòng ${entry.id}`}
                />
              </td>
              <td>{entry.entryDate}</td>
              <td>
                <span className={`cash-badge ${entry.direction === 'IN' ? 'in' : 'out'}`}>
                  {entry.direction === 'IN' ? 'Thu' : 'Chi'}
                </span>
              </td>
              <td>{formatVnd(Number(entry.amount))}</td>
              <td>{entry.category?.name ?? '—'}</td>
              <td>{entry.description || '—'}</td>
              <td>
                <button type="button" onClick={() => onEditNote(entry)}>
                  {entry.note ? entry.note.slice(0, 24) : 'Thêm ghi chú'}
                </button>
              </td>
              <td>{entry.refType}</td>
              <td>{entry.createdBy?.name ?? '—'}</td>
              <td className="cash-actions">
                <button type="button" onClick={() => onDetail(entry)}>
                  Chi tiết
                </button>
                {entry.refType === 'MANUAL' && (
                  <button type="button" onClick={() => onDelete(entry)}>
                    Xóa
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
