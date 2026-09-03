import type { WorkerResponse } from '../types/worker.types'
import { WAGE_TYPE_LABELS, formatVnd } from '../types/worker.types'

interface WorkerListProps {
  workers: WorkerResponse[]
  isLoading: boolean
  onEdit: (worker: WorkerResponse) => void
  onDisable: (worker: WorkerResponse) => void
  onEnable: (worker: WorkerResponse) => void
}

export function WorkerList({
  workers,
  isLoading,
  onEdit,
  onDisable,
  onEnable,
}: WorkerListProps) {
  if (isLoading) {
    return <p className="customer-muted">Đang tải...</p>
  }

  if (workers.length === 0) {
    return <p className="customer-muted">Chưa có thợ nào.</p>
  }

  return (
    <div className="customer-table-wrap">
      <table className="customer-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tên</th>
            <th>SĐT</th>
            <th>Công việc</th>
            <th>Loại công</th>
            <th>Đơn giá</th>
            <th>Ứng trước</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {workers.map((worker) => (
            <tr key={worker.id}>
              <td>{worker.id}</td>
              <td>
                <div>{worker.name}</div>
                {worker.note && <div className="customer-muted">{worker.note}</div>}
              </td>
              <td>{worker.phone || '—'}</td>
              <td>{worker.jobTitle || '—'}</td>
              <td>{WAGE_TYPE_LABELS[worker.wageType]}</td>
              <td>{formatVnd(Number(worker.defaultUnitRate))}</td>
              <td>{formatVnd(Number(worker.currentAdvance))}</td>
              <td>
                <span className={`customer-badge ${worker.active ? 'on' : 'off'}`}>
                  {worker.active ? 'Hoạt động' : 'Đã tắt'}
                </span>
              </td>
              <td className="customer-actions">
                <button type="button" onClick={() => onEdit(worker)}>
                  Sửa
                </button>
                {worker.active ? (
                  <button type="button" className="danger" onClick={() => onDisable(worker)}>
                    Vô hiệu hóa
                  </button>
                ) : (
                  <button type="button" className="success" onClick={() => onEnable(worker)}>
                    Kích hoạt
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
