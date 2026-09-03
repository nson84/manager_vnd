import type { CashbookFilters, CategorySummary } from '../types/cashbook.types'

interface CashFilterBarProps {
  filters: CashbookFilters
  categories: CategorySummary[]
  onChange: (next: CashbookFilters) => void
  onApply: () => void
  onReset: () => void
}

export function CashFilterBar({
  filters,
  categories,
  onChange,
  onApply,
  onReset,
}: CashFilterBarProps) {
  const set = <K extends keyof CashbookFilters>(key: K, value: CashbookFilters[K]) => {
    onChange({ ...filters, [key]: value })
  }

  return (
    <div className="cash-filter">
      <label>
        Từ ngày
        <input
          type="date"
          value={filters.fromDate}
          onChange={(e) => set('fromDate', e.target.value)}
        />
      </label>
      <label>
        Đến ngày
        <input type="date" value={filters.toDate} onChange={(e) => set('toDate', e.target.value)} />
      </label>
      <label>
        Chiều
        <select
          value={filters.direction}
          onChange={(e) => set('direction', e.target.value as CashbookFilters['direction'])}
        >
          <option value="">Tất cả</option>
          <option value="IN">Thu</option>
          <option value="OUT">Chi</option>
        </select>
      </label>
      <label>
        Loại
        <select
          value={filters.categoryId}
          onChange={(e) => set('categoryId', e.target.value)}
        >
          <option value="">Tất cả</option>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>
              {c.name}
            </option>
          ))}
        </select>
      </label>
      <label>
        Nguồn
        <select
          value={filters.refType}
          onChange={(e) => set('refType', e.target.value as CashbookFilters['refType'])}
        >
          <option value="">Tất cả</option>
          <option value="MANUAL">MANUAL</option>
          <option value="EXPENSE">EXPENSE</option>
          <option value="PAYSLIP">PAYSLIP</option>
          <option value="WORKER_ADVANCE">WORKER_ADVANCE</option>
          <option value="CUSTOMER_PAYMENT">CUSTOMER_PAYMENT</option>
        </select>
      </label>
      <label>
        Đối chiếu
        <select
          value={filters.checked}
          onChange={(e) => set('checked', e.target.value as CashbookFilters['checked'])}
        >
          <option value="">Tất cả</option>
          <option value="true">Đã tick</option>
          <option value="false">Chưa tick</option>
        </select>
      </label>
      <label>
        Tìm kiếm
        <input
          value={filters.q}
          placeholder="Mô tả / ghi chú"
          onChange={(e) => set('q', e.target.value)}
        />
      </label>
      <div className="cash-filter-actions">
        <button type="button" onClick={onApply}>
          Áp dụng
        </button>
        <button type="button" onClick={onReset}>
          Xóa lọc
        </button>
      </div>
    </div>
  )
}
