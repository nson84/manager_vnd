import { useState } from 'react'

import { ApiError } from '../../../types/api.types'
import { RoleForm } from '../components/RoleForm'
import { RoleList } from '../components/RoleList'
import '../../company/components/company.css'
import '../components/role.css'
import { usePermissionCatalog } from '../hooks/usePermissionCatalog'
import { useRoles } from '../hooks/useRoles'
import { roleService } from '../services/roleService'
import type { RoleFormValues, RoleResponse } from '../types/role.types'

type FormMode = 'create' | 'edit' | null

function errorMessage(err: unknown): string {
  if (err instanceof ApiError) return err.message
  if (err instanceof Error) return err.message
  return 'Đã xảy ra lỗi'
}

export function RolesPage() {
  const { data, isLoading, error, page, setPage, refetch } = useRoles()
  const catalog = usePermissionCatalog()
  const [formMode, setFormMode] = useState<FormMode>(null)
  const [editing, setEditing] = useState<RoleResponse | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)

  const handleSubmit = async (values: RoleFormValues) => {
    setIsSubmitting(true)
    setActionError(null)
    try {
      if (formMode === 'create') {
        await roleService.create({
          name: values.name.trim(),
          description: values.description.trim() || undefined,
          permissionIds: values.permissionIds,
        })
      } else if (formMode === 'edit' && editing) {
        await roleService.update({
          id: editing.id,
          name: values.name.trim(),
          description: values.description.trim() || undefined,
          permissionIds: values.permissionIds,
        })
      }
      setFormMode(null)
      setEditing(null)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleDelete = async (role: RoleResponse) => {
    if (!window.confirm(`Xóa role "${role.name}"? User đang gắn role này sẽ mất role đó.`)) {
      return
    }
    setActionError(null)
    try {
      await roleService.delete(role.id)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    }
  }

  const roles = data?.result ?? []
  const meta = data?.meta
  const pageError = error || catalog.error || actionError

  return (
    <section className="company-page">
      <header className="company-header">
        <div>
          <h1>Role</h1>
          <p>ADMIN/USER là role hệ thống — không xóa, không đổi tên.</p>
        </div>
        <button
          type="button"
          className="primary"
          onClick={() => {
            setActionError(null)
            setEditing(null)
            setFormMode('create')
          }}
        >
          + Thêm role
        </button>
      </header>

      {pageError && <p className="company-error">{pageError}</p>}

      {formMode && (
        <RoleForm
          mode={formMode}
          role={editing}
          permissions={catalog.permissions}
          isSubmitting={isSubmitting}
          onSubmit={handleSubmit}
          onCancel={() => {
            setFormMode(null)
            setEditing(null)
          }}
        />
      )}

      <RoleList
        roles={roles}
        isLoading={isLoading}
        onEdit={(role) => {
          setActionError(null)
          setEditing(role)
          setFormMode('edit')
        }}
        onDelete={handleDelete}
      />

      {meta && meta.pages > 1 && (
        <footer className="company-pagination">
          <button type="button" disabled={page <= 1 || isLoading} onClick={() => setPage(page - 1)}>
            Trước
          </button>
          <span>
            Trang {meta.page} / {meta.pages} ({meta.total})
          </span>
          <button
            type="button"
            disabled={page >= meta.pages || isLoading}
            onClick={() => setPage(page + 1)}
          >
            Sau
          </button>
        </footer>
      )}
    </section>
  )
}
