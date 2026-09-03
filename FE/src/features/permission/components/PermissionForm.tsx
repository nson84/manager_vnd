import { type FormEvent, useEffect, useState } from 'react'

import type { PermissionFormValues, PermissionResponse } from '../types/permission.types'
import { HTTP_METHODS } from '../types/permission.types'

interface PermissionFormProps {
  mode: 'create' | 'edit'
  permission?: PermissionResponse | null
  isSubmitting: boolean
  onSubmit: (values: PermissionFormValues) => void
  onCancel: () => void
}

const empty: PermissionFormValues = {
  name: '',
  apiPath: '/api/v1/',
  method: 'GET',
  module: '',
}

export function PermissionForm({
  mode,
  permission,
  isSubmitting,
  onSubmit,
  onCancel,
}: PermissionFormProps) {
  const [values, setValues] = useState<PermissionFormValues>(empty)

  useEffect(() => {
    if (mode === 'edit' && permission) {
      setValues({
        name: permission.name,
        apiPath: permission.apiPath,
        method: permission.method,
        module: permission.module,
      })
      return
    }
    setValues(empty)
  }, [mode, permission])

  const set = (key: keyof PermissionFormValues, value: string) => {
    setValues((current) => ({ ...current, [key]: value }))
  }

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    onSubmit(values)
  }

  return (
    <form className="company-form" onSubmit={handleSubmit}>
      <h2>{mode === 'create' ? 'Thêm permission' : 'Sửa permission'}</h2>

      <label>
        Tên *
        <input required value={values.name} onChange={(e) => set('name', e.target.value)} />
      </label>

      <label>
        API path *
        <input required value={values.apiPath} onChange={(e) => set('apiPath', e.target.value)} />
      </label>

      <label>
        Method *
        <select value={values.method} onChange={(e) => set('method', e.target.value)}>
          {HTTP_METHODS.map((method) => (
            <option key={method} value={method}>
              {method}
            </option>
          ))}
        </select>
      </label>

      <label>
        Module *
        <input required value={values.module} onChange={(e) => set('module', e.target.value)} />
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
