import { type FormEvent, useState } from 'react'

import type { PayslipFormValues } from '../types/payslip.types'

interface PayslipFormProps {
  isSubmitting: boolean
  onSubmit: (values: PayslipFormValues) => void
  onCancel: () => void
}

const empty: PayslipFormValues = {
  workerId: '',
  periodStart: '',
  periodEnd: '',
  advanceDeducted: '0',
  otherDeduction: '0',
  note: '',
}

export function PayslipForm({ isSubmitting, onSubmit, onCancel }: PayslipFormProps) {
  const [values, setValues] = useState<PayslipFormValues>(empty)

  const set = (key: keyof PayslipFormValues, value: string) => {
    setValues((current) => ({ ...current, [key]: value }))
  }

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    onSubmit(values)
  }

  return (
    <form className="customer-form" onSubmit={handleSubmit}>
      <h2>Tạo phiếu lương (DRAFT)</h2>

      <label>
        Worker ID *
        <input
          required
          type="number"
          min="1"
          value={values.workerId}
          onChange={(e) => set('workerId', e.target.value)}
        />
      </label>

      <label>
        Từ ngày *
        <input
          required
          type="date"
          value={values.periodStart}
          onChange={(e) => set('periodStart', e.target.value)}
        />
      </label>

      <label>
        Đến ngày *
        <input
          required
          type="date"
          value={values.periodEnd}
          onChange={(e) => set('periodEnd', e.target.value)}
        />
      </label>

      <label>
        Trừ ứng trước
        <input
          type="number"
          min="0"
          step="any"
          value={values.advanceDeducted}
          onChange={(e) => set('advanceDeducted', e.target.value)}
        />
      </label>

      <label>
        Khấu trừ khác
        <input
          type="number"
          min="0"
          step="any"
          value={values.otherDeduction}
          onChange={(e) => set('otherDeduction', e.target.value)}
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
          {isSubmitting ? 'Đang lưu...' : 'Tạo DRAFT'}
        </button>
      </div>
    </form>
  )
}
