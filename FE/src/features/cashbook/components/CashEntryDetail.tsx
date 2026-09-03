import type { CashEntryResponse } from '../types/cashbook.types'
import { formatVnDateTime, formatVnd } from '../types/cashbook.types'

interface CashEntryDetailProps {
  entry: CashEntryResponse
  onClose: () => void
}

export function CashEntryDetail({ entry, onClose }: CashEntryDetailProps) {
  return (
    <div className="cash-modal-backdrop" role="dialog" aria-modal="true">
      <div className="cash-modal cash-detail">
        <h2>Chi tiết dòng #{entry.id}</h2>
        <dl>
          <dt>Ngày</dt>
          <dd>{entry.entryDate}</dd>
          <dt>Chiều</dt>
          <dd>{entry.direction === 'IN' ? 'Thu' : 'Chi'}</dd>
          <dt>Số tiền</dt>
          <dd>{formatVnd(Number(entry.amount))}</dd>
          <dt>Loại</dt>
          <dd>{entry.category?.name ?? '—'}</dd>
          <dt>Mô tả</dt>
          <dd>{entry.description || '—'}</dd>
          <dt>Ghi chú</dt>
          <dd>{entry.note || '—'}</dd>
          <dt>Đối chiếu</dt>
          <dd>
            {entry.checked ? 'Đã tick' : 'Chưa'}
            {entry.checkedAt ? ` — ${formatVnDateTime(entry.checkedAt)}` : ''}
          </dd>
          <dt>Nguồn</dt>
          <dd>
            {entry.refType}
            {entry.refId != null ? ` #${entry.refId}` : ''}
          </dd>
          <dt>Người tạo</dt>
          <dd>{entry.createdBy?.name ?? '—'}</dd>
          <dt>Tạo lúc</dt>
          <dd>{formatVnDateTime(entry.createdAt)}</dd>
        </dl>
        <div className="cash-form-actions">
          <button type="button" onClick={onClose}>
            Đóng
          </button>
        </div>
      </div>
    </div>
  )
}
