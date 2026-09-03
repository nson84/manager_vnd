import { type FormEvent, useEffect, useState } from 'react'

import type { Gender, UserFormValues, UserResponse } from '../types/user.types'

interface UserFormProps {
  mode: 'create' | 'edit'
  user?: UserResponse | null
  isSubmitting: boolean
  onSubmit: (values: UserFormValues) => void
  onCancel: () => void
}

const emptyValues: UserFormValues = {
  name: '',
  email: '',
  password: '',
  age: '',
  gender: '',
  address: '',
  companyId: '',
  roleIds: '',
}

function toFormValues(user: UserResponse): UserFormValues {
  return {
    name: user.name,
    email: user.email,
    password: '',
    age: user.age != null ? String(user.age) : '',
    gender: user.gender ?? '',
    address: user.address ?? '',
    companyId: user.company != null ? String(user.company.id) : '',
    roleIds: user.roles.map((role) => role.id).join(', '),
  }
}

export function UserForm({ mode, user, isSubmitting, onSubmit, onCancel }: UserFormProps) {
  const [values, setValues] = useState<UserFormValues>(emptyValues)

  useEffect(() => {
    if (mode === 'edit' && user) {
      setValues(toFormValues(user))
      return
    }
    setValues(emptyValues)
  }, [mode, user])

  const handleChange = (field: keyof UserFormValues, value: string) => {
    setValues((current) => ({ ...current, [field]: value }))
  }

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    onSubmit(values)
  }

  return (
    <form className="user-form" onSubmit={handleSubmit}>
      <h2>{mode === 'create' ? 'Thêm user' : 'Sửa user'}</h2>

      <label>
        Tên *
        <input
          required
          value={values.name}
          onChange={(event) => handleChange('name', event.target.value)}
        />
      </label>

      <label>
        Email *
        <input
          required
          type="email"
          value={values.email}
          disabled={mode === 'edit'}
          onChange={(event) => handleChange('email', event.target.value)}
        />
      </label>

      {mode === 'create' && (
        <label>
          Mật khẩu *
          <input
            required
            type="password"
            minLength={8}
            value={values.password}
            onChange={(event) => handleChange('password', event.target.value)}
          />
        </label>
      )}

      <label>
        Tuổi
        <input
          type="number"
          min={0}
          value={values.age}
          onChange={(event) => handleChange('age', event.target.value)}
        />
      </label>

      <label>
        Giới tính
        <select
          value={values.gender}
          onChange={(event) => handleChange('gender', event.target.value)}
        >
          <option value="">—</option>
          <option value="MALE">Nam</option>
          <option value="FEMALE">Nữ</option>
          <option value="OTHER">Khác</option>
        </select>
      </label>

      <label>
        Địa chỉ
        <input
          value={values.address}
          onChange={(event) => handleChange('address', event.target.value)}
        />
      </label>

      <label>
        Company ID
        <input
          type="number"
          min={1}
          placeholder="VD: 1"
          value={values.companyId}
          onChange={(event) => handleChange('companyId', event.target.value)}
        />
      </label>

      <label>
        Role IDs
        <input
          placeholder="VD: 1, 2"
          value={values.roleIds}
          onChange={(event) => handleChange('roleIds', event.target.value)}
        />
      </label>

      <div className="user-form-actions">
        <button type="button" onClick={onCancel} disabled={isSubmitting}>
          Hủy
        </button>
        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Đang lưu...' : mode === 'create' ? 'Tạo' : 'Cập nhật'}
        </button>
      </div>
    </form>
  )
}

export function parseRoleIds(value: string): number[] | undefined {
  const trimmed = value.trim()
  if (!trimmed) {
    return undefined
  }
  return trimmed
    .split(',')
    .map((part) => Number(part.trim()))
    .filter((id) => !Number.isNaN(id) && id > 0)
}

export function parseOptionalNumber(value: string): number | undefined {
  const trimmed = value.trim()
  if (!trimmed) {
    return undefined
  }
  const parsed = Number(trimmed)
  return Number.isNaN(parsed) ? undefined : parsed
}

export function parseGender(value: Gender | ''): Gender | undefined {
  return value || undefined
}
