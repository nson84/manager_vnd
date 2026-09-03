import { type FormEvent, useEffect, useState } from 'react'

import type { WorkerFormValues, WorkerResponse } from '../types/worker.types'
import { formatVnd } from '../types/worker.types'

interface WorkerFormProps {
  mode: 'create' | 'edit'
  worker?: WorkerResponse | null
  isSubmitting: boolean
  onSubmit: (values: WorkerFormValues) => void
  onCancel: () => void
}

const empty: WorkerFormValues = {
  name: '',
  phone: '',
  address: '',
  jobTitle: '',
  wageType: 'DAILY',
  defaultUnitRate: '',
  hireDate: '',
  note: '',
}

export function WorkerForm({
  mode,
  worker,
  isSubmitting,
  onSubmit,
  onCancel,
}: WorkerFormProps) {
  const [values, setValues] = useState<WorkerFormValues>(empty)

  useEffect(() => {
    if (mode === 'edit' && worker) {
      setValues({
        name: worker.name,
        phone: worker.phone ?? '',
        address: worker.address ?? '',
        jobTitle: worker.jobTitle ?? '',
        wageType: worker.wageType,
        defaultUnitRate: String(worker.defaultUnitRate),
        hireDate: worker.hireDate ?? '',
        note: worker.note ?? '',
      })
      return
    }
    setValues(empty)
  }, [mode, worker])

  const set = (key: keyof WorkerFormValues, value: string) => {
    setValues((current) => ({ ...current, [key]: value }))
  }

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    onSubmit(values)
  }

  return (
    <form className="customer-form" onSubmit={handleSubmit}>
      <h2>{mode === 'create' ? 'Thêm thợ' : 'Sửa thợ'}</h2>

      <label>
        Tên *
        <input required value={values.name} onChange={(e) => set('name', e.target.value)} />
      </label>

      <label>
        Số điện thoại
        <input value={values.phone} onChange={(e) => set('phone', e.target.value)} />
      </label>

      <label>
        Địa chỉ
        <input value={values.address} onChange={(e) => set('address', e.target.value)} />
      </label>

      <label>
        Công việc
        <input value={values.jobTitle} onChange={(e) => set('jobTitle', e.target.value)} />
      </label>

      <label>
        Loại công *
        <select
          required
          value={values.wageType}
          onChange={(e) => set('wageType', e.target.value as WorkerFormValues['wageType'])}
        >
          <option value="DAILY">Theo ngày</option>
          <option value="HOURLY">Theo giờ</option>
          <option value="PIECE">Theo sản phẩm</option>
        </select>
      </label>

      <label>
        Đơn giá mặc định *
        <input
          required
          type="number"
          min="0.01"
          step="any"
          value={values.defaultUnitRate}
          onChange={(e) => set('defaultUnitRate', e.target.value)}
        />
      </label>

      <label>
        Ngày vào làm
        <input
          type="date"
          value={values.hireDate}
          onChange={(e) => set('hireDate', e.target.value)}
        />
      </label>

      {mode === 'edit' && worker && (
        <label>
          Ứng trước (chỉ xem)
          <input readOnly value={formatVnd(Number(worker.currentAdvance))} />
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
