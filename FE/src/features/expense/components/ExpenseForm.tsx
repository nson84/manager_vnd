import { type FormEvent, useEffect, useState } from 'react'

import { cashbookService } from '../../cashbook'
import type { CategorySummary } from '../../cashbook'
import type { ExpenseFormValues } from '../types/expense.types'

interface ExpenseFormProps {
  isSubmitting: boolean
  onSubmit: (values: ExpenseFormValues) => void
  onCancel: () => void
}

const empty: ExpenseFormValues = {
  categoryId: '',
  amount: '',
  expenseDate: '',
  note: '',
}

export function ExpenseForm({ isSubmitting, onSubmit, onCancel }: ExpenseFormProps) {
  const [values, setValues] = useState<ExpenseFormValues>(empty)
  const [categories, setCategories] = useState<CategorySummary[]>([])
  const [loadError, setLoadError] = useState<string | null>(null)

  useEffect(() => {
    void (async () => {
      try {
        const response = await cashbookService.listCategories()
        setCategories(response.data ?? [])
      } catch (err) {
        setLoadError(err instanceof Error ? err.message : 'Không tải được danh mục')
      }
    })()
  }, [])

  const set = (key: keyof ExpenseFormValues, value: string) => {
    setValues((current) => ({ ...current, [key]: value }))
  }

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    onSubmit(values)
  }

  return (
    <form className="customer-form" onSubmit={handleSubmit}>
      <h2>Tạo phiếu chi</h2>

      {loadError && <p className="customer-error">{loadError}</p>}

      <label>
        Danh mục *
        {categories.length > 0 ? (
          <select
            required
            value={values.categoryId}
            onChange={(e) => set('categoryId', e.target.value)}
          >
            <option value="">Chọn danh mục</option>
            {categories
              .filter((c) => c.code !== 'WAGE')
              .map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name} ({c.code})
                </option>
              ))}
          </select>
        ) : (
          <input
            required
            type="number"
            min="1"
            value={values.categoryId}
            onChange={(e) => set('categoryId', e.target.value)}
            placeholder="categoryId"
          />
        )}
      </label>

      <label>
        Số tiền *
        <input
          required
          type="number"
          min="0.01"
          step="any"
          value={values.amount}
          onChange={(e) => set('amount', e.target.value)}
        />
      </label>

      <label>
        Ngày chi *
        <input
          required
          type="date"
          value={values.expenseDate}
          onChange={(e) => set('expenseDate', e.target.value)}
        />
      </label>

      <label>
        Ghi chú
        <textarea rows={3} value={values.note} onChange={(e) => set('note', e.target.value)} />
      </label>

      <div className="customer-form-actions">
        <button type="button" onClick={onCancel} disabled={isSubmitting}>
          Hủy
        </button>
        <button type="submit" className="primary" disabled={isSubmitting}>
          {isSubmitting ? 'Đang lưu...' : 'Tạo'}
        </button>
      </div>
    </form>
  )
}
