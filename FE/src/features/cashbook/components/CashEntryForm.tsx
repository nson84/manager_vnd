import { type FormEvent, useState } from 'react'

import type {
  CashDirection,
  CategorySummary,
  CreateManualCashEntryRequest,
} from '../types/cashbook.types'
import { vietnamToday } from '../types/cashbook.types'

interface CashEntryFormProps {
  categories: CategorySummary[]
  isSubmitting: boolean
  onSubmit: (data: CreateManualCashEntryRequest) => void
  onCancel: () => void
}

export function CashEntryForm({ categories, isSubmitting, onSubmit, onCancel }: CashEntryFormProps) {
  const [entryDate, setEntryDate] = useState(vietnamToday())
  const [direction, setDirection] = useState<CashDirection>('OUT')
  const [amount, setAmount] = useState('')
  const [categoryId, setCategoryId] = useState(categories[0] ? String(categories[0].id) : '')
  const [description, setDescription] = useState('')
  const [note, setNote] = useState('')

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    onSubmit({
      entryDate,
      direction,
      amount: Number(amount),
      categoryId: Number(categoryId),
      description: description.trim() || undefined,
      note: note.trim() || undefined,
    })
  }

  return (
    <div className="cash-modal-backdrop" role="dialog" aria-modal="true">
      <div className="cash-modal">
        <h2>Thêm phiếu thu/chi (MANUAL)</h2>
        <form className="cash-form" onSubmit={handleSubmit}>
          <label>
            Ngày *
            <input type="date" required value={entryDate} onChange={(e) => setEntryDate(e.target.value)} />
          </label>
          <label>
            Chiều *
            <select
              value={direction}
              onChange={(e) => setDirection(e.target.value as CashDirection)}
            >
              <option value="IN">Thu</option>
              <option value="OUT">Chi</option>
            </select>
          </label>
          <label>
            Số tiền *
            <input
              type="number"
              min={1}
              required
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
            />
          </label>
          <label>
            Loại *
            <select required value={categoryId} onChange={(e) => setCategoryId(e.target.value)}>
              {categories.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
          </label>
          <label>
            Mô tả
            <input value={description} onChange={(e) => setDescription(e.target.value)} />
          </label>
          <label>
            Ghi chú
            <textarea rows={3} value={note} onChange={(e) => setNote(e.target.value)} />
          </label>
          <div className="cash-form-actions">
            <button type="button" onClick={onCancel} disabled={isSubmitting}>
              Hủy
            </button>
            <button type="submit" className="primary" disabled={isSubmitting || !categoryId}>
              {isSubmitting ? 'Đang lưu...' : 'Tạo phiếu'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
