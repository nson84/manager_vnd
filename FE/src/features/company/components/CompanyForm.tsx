import { type FormEvent, useEffect, useState } from 'react'

import type { CompanyFormValues, CompanyResponse } from '../types/company.types'

interface CompanyFormProps {
  mode: 'create' | 'edit'
  company?: CompanyResponse | null
  isSubmitting: boolean
  onSubmit: (values: CompanyFormValues) => void
  onCancel: () => void
}

const empty: CompanyFormValues = {
  name: '',
  description: '',
  address: '',
  logo: '',
}

export function CompanyForm({ mode, company, isSubmitting, onSubmit, onCancel }: CompanyFormProps) {
  const [values, setValues] = useState<CompanyFormValues>(empty)

  useEffect(() => {
    if (mode === 'edit' && company) {
      setValues({
        name: company.name,
        description: company.description ?? '',
        address: company.address ?? '',
        logo: company.logo ?? '',
      })
      return
    }
    setValues(empty)
  }, [mode, company])

  const set = (key: keyof CompanyFormValues, value: string) => {
    setValues((current) => ({ ...current, [key]: value }))
  }

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    onSubmit(values)
  }

  return (
    <form className="company-form" onSubmit={handleSubmit}>
      <h2>{mode === 'create' ? 'Thêm công ty' : 'Sửa công ty'}</h2>

      <label>
        Tên *
        <input required value={values.name} onChange={(e) => set('name', e.target.value)} />
      </label>

      <label>
        Mô tả
        <textarea
          rows={3}
          value={values.description}
          onChange={(e) => set('description', e.target.value)}
        />
      </label>

      <label>
        Địa chỉ
        <input value={values.address} onChange={(e) => set('address', e.target.value)} />
      </label>

      <label>
        Logo (đường dẫn / tên file)
        <input value={values.logo} onChange={(e) => set('logo', e.target.value)} />
      </label>

      <div className="company-form-actions">
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
