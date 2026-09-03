import { useState } from 'react'

import { ApiError } from '../../../types/api.types'
import { PermissionForm } from '../components/PermissionForm'
import { PermissionList } from '../components/PermissionList'
import '../../company/components/company.css'
import { usePermissions } from '../hooks/usePermissions'
import { permissionService } from '../services/permissionService'
import type {
  PermissionFormValues,
  PermissionResponse,
} from '../types/permission.types'

type FormMode = 'create' | 'edit' | null

function errorMessage(err: unknown): string {
  if (err instanceof ApiError) return err.message
  if (err instanceof Error) return err.message
  return 'Đã xảy ra lỗi'
}

export function PermissionsPage() {
  const { data, isLoading, error, page, setPage, refetch } = usePermissions()
  const [formMode, setFormMode] = useState<FormMode>(null)
  const [editing, setEditing] = useState<PermissionResponse | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)

  const handleSubmit = async (values: PermissionFormValues) => {
    setIsSubmitting(true)
    setActionError(null)
    try {
      if (formMode === 'create') {
        await permissionService.create({
          name: values.name.trim(),
          apiPath: values.apiPath.trim(),
          method: values.method,
          module: values.module.trim(),
        })
      } else if (formMode === 'edit' && editing) {
        await permissionService.update({
          id: editing.id,
          name: values.name.trim(),
          apiPath: values.apiPath.trim(),
          method: values.method,
          module: values.module.trim(),
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

  const handleDelete = async (permission: PermissionResponse) => {
    if (!window.confirm(`Xóa permission "${permission.name}"?`)) return
    setActionError(null)
    try {
      await permissionService.delete(permission.id)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    }
  }

  const permissions = data?.result ?? []
  const meta = data?.meta

  return (
    <section className="company-page">
      <header className="company-header">
        <div>
          <h1>Permission</h1>
          <p>Một permission = một API (method + path). Gán vào Role.</p>
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
          + Thêm permission
        </button>
      </header>

      {(error || actionError) && <p className="company-error">{error || actionError}</p>}

      {formMode && (
        <PermissionForm
          mode={formMode}
          permission={editing}
          isSubmitting={isSubmitting}
          onSubmit={handleSubmit}
          onCancel={() => {
            setFormMode(null)
            setEditing(null)
          }}
        />
      )}

      <PermissionList
        permissions={permissions}
        isLoading={isLoading}
        onEdit={(item) => {
          setActionError(null)
          setEditing(item)
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
