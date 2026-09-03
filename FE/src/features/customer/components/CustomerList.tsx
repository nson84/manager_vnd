import type { CustomerResponse } from '../types/customer.types'
import { formatVnd } from '../types/customer.types'

interface CustomerListProps {
  customers: CustomerResponse[]
  isLoading: boolean
  onEdit: (customer: CustomerResponse) => void
  onDisable: (customer: CustomerResponse) => void
  onEnable: (customer: CustomerResponse) => void
}

export function CustomerList({
  customers,
  isLoading,
  onEdit,
  onDisable,
  onEnable,
}: CustomerListProps) {
  if (isLoading) {
    return <p className="customer-muted">Đang tải...</p>
  }

  if (customers.length === 0) {
    return <p className="customer-muted">Chưa có khách hàng nào.</p>
  }

  return (
    <div className="customer-table-wrap">
      <table className="customer-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tên</th>
            <th>SĐT</th>
            <th>Địa chỉ</th>
            <th>Công nợ</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {customers.map((customer) => (
            <tr key={customer.id}>
              <td>{customer.id}</td>
              <td>
                <div>{customer.name}</div>
                {customer.note && <div className="customer-muted">{customer.note}</div>}
              </td>
              <td>{customer.phone || '—'}</td>
              <td>{customer.address || '—'}</td>
              <td>{formatVnd(Number(customer.currentDebt))}</td>
              <td>
                <span className={`customer-badge ${customer.active ? 'on' : 'off'}`}>
                  {customer.active ? 'Hoạt động' : 'Đã tắt'}
                </span>
              </td>
              <td className="customer-actions">
                <button type="button" onClick={() => onEdit(customer)}>
                  Sửa
                </button>
                {customer.active ? (
                  <button type="button" className="danger" onClick={() => onDisable(customer)}>
                    Vô hiệu hóa
                  </button>
                ) : (
                  <button type="button" className="success" onClick={() => onEnable(customer)}>
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
