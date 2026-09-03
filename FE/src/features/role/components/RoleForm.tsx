import { type FormEvent, useEffect, useState } from 'react'

import type { PermissionResponse } from '../../permission'
import type { RoleFormValues, RoleResponse } from '../types/role.types'
import { isSystemRole } from '../types/role.types'

interface RoleFormProps {
  mode: 'create' | 'edit'
  role?: RoleResponse | null
  permissions: PermissionResponse[]
  isSubmitting: boolean
  onSubmit: (values: RoleFormValues) => void
  onCancel: () => void
}

const empty: RoleFormValues = {
  name: '',
  description: '',
  permissionIds: [],
}

export function RoleForm({
  mode,
  role,
  permissions,
  isSubmitting,
  onSubmit,
  onCancel,
}: RoleFormProps) {
  const [values, setValues] = useState<RoleFormValues>(empty)
  const nameLocked = mode === 'edit' && role != null && isSystemRole(role.name)

  useEffect(() => {
    if (mode === 'edit' && role) {
      setValues({
        name: role.name,
        description: role.description ?? '',
        permissionIds: role.permissions.map((item) => item.id),
      })
      return
    }
    setValues(empty)
  }, [mode, role])

  const togglePermission = (id: number) => {
    setValues((current) => {
      const has = current.permissionIds.includes(id)
      return {
        ...current,
        permissionIds: has
          ? current.permissionIds.filter((item) => item !== id)
          : [...current.permissionIds, id],
      }
    })
  }

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    onSubmit(values)
  }

  return (
    <form className="company-form" onSubmit={handleSubmit}>
      <h2>{mode === 'create' ? 'Thêm role' : 'Sửa role'}</h2>

      <label>
        Tên *
        <input
          required
          value={values.name}
          disabled={nameLocked}
          onChange={(e) => setValues((current) => ({ ...current, name: e.target.value }))}
        />
      </label>

      <label>
        Mô tả
        <textarea
          rows={2}
          value={values.description}
          onChange={(e) => setValues((current) => ({ ...current, description: e.target.value }))}
        />
      </label>

      <fieldset className="role-perm-list">
        <legend>Permissions</legend>
        {permissions.length === 0 ? (
          <p className="company-muted">Chưa có permission. Tạo ở tab Permission trước.</p>
        ) : (
          permissions.map((permission) => (
            <label key={permission.id}>
              <input
                type="checkbox"
                checked={values.permissionIds.includes(permission.id)}
                onChange={() => togglePermission(permission.id)}
              />
              <span>
                {permission.method} {permission.apiPath}
                <span className="company-muted"> — {permission.name}</span>
              </span>
            </label>
          ))
        )}
      </fieldset>

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
