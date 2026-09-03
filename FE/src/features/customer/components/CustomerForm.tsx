import { type FormEvent, useEffect, useState } from 'react'

import type { CustomerFormValues, CustomerResponse } from '../types/customer.types'

interface CustomerFormProps {
  mode: 'create' | 'edit'
  customer?: CustomerResponse | null
  isSubmitting: boolean
  onSubmit: (values: CustomerFormValues) => void
  onCancel: () => void
}

const empty: CustomerFormValues = {
  name: '',
  phone: '',
  address: '',
  note: '',
}

export function CustomerForm({
  mode,
  customer,
  isSubmitting,
  onSubmit,
  onCancel,
}: CustomerFormProps) {
  const [values, setValues] = useState<CustomerFormValues>(empty)

  useEffect(() => {
    if (mode === 'edit' && customer) {
      setValues({
        name: customer.name,
        phone: customer.phone ?? '',
        address: customer.address ?? '',
        note: customer.note ?? '',
      })
      return
    }
    setValues(empty)
  }, [mode, customer])

  const set = (key: keyof CustomerFormValues, value: string) => {
    setValues((current) => ({ ...current, [key]: value }))
  }

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    onSubmit(values)
  }

  return (
    <form className="customer-form" onSubmit={handleSubmit}>
      <h2>{mode === 'create' ? 'Thêm khách hàng' : 'Sửa khách hàng'}</h2>

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
