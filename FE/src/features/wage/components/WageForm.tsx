import { type FormEvent, useEffect, useState } from 'react'

import type { WageEntryResponse, WageFormValues, WageType } from '../types/wage.types'
import { formatVnd } from '../types/wage.types'

interface WageFormProps {
  mode: 'create' | 'edit'
  entry?: WageEntryResponse | null
  isSubmitting: boolean
  onSubmit: (values: WageFormValues) => void
  onCancel: () => void
}

const empty: WageFormValues = {
  workerId: '',
  workDate: '',
  wageType: '',
  quantity: '',
  unitRate: '',
  note: '',
}

export function WageForm({ mode, entry, isSubmitting, onSubmit, onCancel }: WageFormProps) {
  const [values, setValues] = useState<WageFormValues>(empty)

  useEffect(() => {
    if (mode === 'edit' && entry) {
      setValues({
        workerId: String(entry.workerId),
        workDate: entry.workDate,
        wageType: entry.wageType,
        quantity: String(entry.quantity),
        unitRate: String(entry.unitRate),
        note: entry.note ?? '',
      })
      return
    }
    setValues(empty)
  }, [mode, entry])

  const set = (key: keyof WageFormValues, value: string) => {
    setValues((current) => ({ ...current, [key]: value }))
  }

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    onSubmit(values)
  }

  return (
    <form className="customer-form" onSubmit={handleSubmit}>
      <h2>{mode === 'create' ? 'Thêm ghi công' : 'Sửa ghi công'}</h2>

      <label>
        Worker ID *
        <input
          required
          type="number"
          min="1"
          disabled={mode === 'edit'}
          value={values.workerId}
          onChange={(e) => set('workerId', e.target.value)}
        />
      </label>

      <label>
        Ngày làm *
        <input
          required
          type="date"
          value={values.workDate}
          onChange={(e) => set('workDate', e.target.value)}
        />
      </label>

      <label>
        Loại công
        <select
          value={values.wageType}
          onChange={(e) => set('wageType', e.target.value as WageType | '')}
        >
          <option value="">Mặc định theo thợ</option>
          <option value="DAILY">Theo ngày</option>
          <option value="HOURLY">Theo giờ</option>
          <option value="PIECE">Theo sản phẩm</option>
        </select>
      </label>

      <label>
        Số lượng *
        <input
          required
          type="number"
          min="0.01"
          step="any"
          value={values.quantity}
          onChange={(e) => set('quantity', e.target.value)}
        />
      </label>

      <label>
        Đơn giá
        <input
          type="number"
          min="0.01"
          step="any"
          value={values.unitRate}
          onChange={(e) => set('unitRate', e.target.value)}
          placeholder="Để trống = đơn giá thợ"
        />
      </label>

      {mode === 'edit' && entry && (
        <label>
          Thành tiền (chỉ xem)
          <input readOnly value={formatVnd(Number(entry.amount))} />
        </label>
      )}

      <label>
        Ghi chú
        <textarea rows={3} value={values.note} onChange={(e) => set('note', e.target.value)} />
      </label>

      <div className="customer-form-actions">
        <button type="button" onClick={onCancel} disabled={isSubmitting}>
          Hủy
        </button>
        <button type="submit" className="primary" disabled={isSubmitting}>
          {isSubmitting ? 'Đang lưu...' : mode === 'create' ? 'Tạo' : 'Cập nhật'}
        </button>
      </div>
    </form>
  )
}
