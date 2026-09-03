import { type FormEvent, useState } from 'react'

import type { DebtEntryType, DebtFormValues, LedgerDirection, PartyType } from '../types/debt.types'

interface DebtFormProps {
  isSubmitting: boolean
  onSubmit: (values: DebtFormValues) => void
  onCancel: () => void
}

const empty: DebtFormValues = {
  partyType: 'customer',
  partyId: '',
  entryType: 'CHARGE',
  direction: '',
  amount: '',
  entryDate: '',
  note: '',
}

export function DebtForm({ isSubmitting, onSubmit, onCancel }: DebtFormProps) {
  const [values, setValues] = useState<DebtFormValues>(empty)

  const set = (key: keyof DebtFormValues, value: string) => {
    setValues((current) => ({ ...current, [key]: value }))
  }

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    onSubmit(values)
  }

  return (
    <form className="customer-form" onSubmit={handleSubmit}>
      <h2>Ghi công nợ</h2>

      <label>
        Đối tượng *
        <select
          value={values.partyType}
          onChange={(e) => set('partyType', e.target.value as PartyType)}
        >
          <option value="customer">Khách hàng</option>
          <option value="worker">Thợ</option>
        </select>
      </label>

      <label>
        {values.partyType === 'customer' ? 'Customer ID *' : 'Worker ID *'}
        <input
          required
          type="number"
          min="1"
          value={values.partyId}
          onChange={(e) => set('partyId', e.target.value)}
        />
      </label>

      <label>
        Loại bút toán *
        <select
          required
          value={values.entryType}
          onChange={(e) => set('entryType', e.target.value as DebtEntryType)}
        >
          <option value="CHARGE">Ghi nợ (CHARGE)</option>
          <option value="PAYMENT">Thanh toán (PAYMENT)</option>
          <option value="ADJUST">Điều chỉnh (ADJUST)</option>
        </select>
      </label>

      {values.entryType === 'ADJUST' && (
        <label>
          Hướng *
          <select
            required
            value={values.direction}
            onChange={(e) => set('direction', e.target.value as LedgerDirection | '')}
          >
            <option value="">Chọn hướng</option>
            <option value="INCREASE">Tăng (INCREASE)</option>
            <option value="DECREASE">Giảm (DECREASE)</option>
          </select>
        </label>
      )}

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
        Ngày *
        <input
          required
          type="date"
          value={values.entryDate}
          onChange={(e) => set('entryDate', e.target.value)}
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
