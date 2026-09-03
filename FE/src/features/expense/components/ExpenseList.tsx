import type { ExpenseResponse } from '../types/expense.types'
import { EXPENSE_STATUS_LABELS, formatVnd } from '../types/expense.types'

interface ExpenseListProps {
  expenses: ExpenseResponse[]
  isLoading: boolean
  onCancel: (expense: ExpenseResponse) => void
}

export function ExpenseList({ expenses, isLoading, onCancel }: ExpenseListProps) {
  if (isLoading) {
    return <p className="customer-muted">Đang tải...</p>
  }

  if (expenses.length === 0) {
    return <p className="customer-muted">Chưa có phiếu chi nào.</p>
  }

  return (
    <div className="customer-table-wrap">
      <table className="customer-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Danh mục</th>
            <th>Số tiền</th>
            <th>Ngày</th>
            <th>Trạng thái</th>
            <th>Ghi chú</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {expenses.map((expense) => (
            <tr key={expense.id}>
              <td>{expense.id}</td>
              <td>
                {expense.categoryName}
                <div className="customer-muted">{expense.categoryCode}</div>
              </td>
              <td>{formatVnd(Number(expense.amount))}</td>
              <td>{expense.expenseDate}</td>
              <td>
                <span className={`customer-badge ${expense.status === 'POSTED' ? 'on' : 'off'}`}>
                  {EXPENSE_STATUS_LABELS[expense.status]}
                </span>
              </td>
              <td>{expense.note || '—'}</td>
              <td className="customer-actions">
                {expense.status === 'POSTED' && (
                  <button type="button" className="danger" onClick={() => onCancel(expense)}>
                    Hủy
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
