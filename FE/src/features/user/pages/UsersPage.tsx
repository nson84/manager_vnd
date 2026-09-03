import { useState } from 'react'

import { ApiError } from '../../../types/api.types'
import {
  parseGender,
  parseOptionalNumber,
  parseRoleIds,
  UserForm,
} from '../components/UserForm'
import { UserList } from '../components/UserList'
import { useUsers } from '../hooks/useUsers'
import { userService } from '../services/userService'
import type {
  ActiveFilter,
  CreateUserRequest,
  UpdateUserRequest,
  UserFormValues,
  UserResponse,
} from '../types/user.types'
import '../components/users.css'

type FormMode = 'create' | 'edit' | null

function toCreateRequest(values: UserFormValues): CreateUserRequest {
  return {
    name: values.name.trim(),
    email: values.email.trim(),
    password: values.password,
    age: parseOptionalNumber(values.age),
    gender: parseGender(values.gender),
    address: values.address.trim() || undefined,
    companyId: parseOptionalNumber(values.companyId),
    roleIds: parseRoleIds(values.roleIds),
  }
}

function toUpdateRequest(userId: number, values: UserFormValues): UpdateUserRequest {
  return {
    id: userId,
    name: values.name.trim(),
    age: parseOptionalNumber(values.age),
    gender: parseGender(values.gender),
    address: values.address.trim() || undefined,
    companyId: parseOptionalNumber(values.companyId),
    roleIds: parseRoleIds(values.roleIds),
  }
}

function getErrorMessage(err: unknown): string {
  if (err instanceof ApiError) {
    return err.message
  }
  if (err instanceof Error) {
    return err.message
  }
  return 'Đã xảy ra lỗi'
}

export function UsersPage() {
  const {
    data,
    isLoading,
    error,
    page,
    setPage,
    activeFilter,
    setActiveFilter,
    refetch,
  } = useUsers()
  const [formMode, setFormMode] = useState<FormMode>(null)
  const [editingUser, setEditingUser] = useState<UserResponse | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)

  const handleCreateClick = () => {
    setActionError(null)
    setEditingUser(null)
    setFormMode('create')
  }

  const handleEdit = (user: UserResponse) => {
    setActionError(null)
    setEditingUser(user)
    setFormMode('edit')
  }

  const handleCancelForm = () => {
    setFormMode(null)
    setEditingUser(null)
  }

  const handleSubmit = async (values: UserFormValues) => {
    setIsSubmitting(true)
    setActionError(null)
    try {
      if (formMode === 'create') {
        await userService.create(toCreateRequest(values))
      } else if (formMode === 'edit' && editingUser) {
        await userService.update(toUpdateRequest(editingUser.id, values))
      }
      setFormMode(null)
      setEditingUser(null)
      await refetch()
    } catch (err) {
      setActionError(getErrorMessage(err))
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleDisable = async (user: UserResponse) => {
    if (!window.confirm(`Vô hiệu hóa user "${user.name}"? (không xóa dữ liệu)`)) {
      return
    }
    setActionError(null)
    try {
      await userService.disable(user.id)
      await refetch()
    } catch (err) {
      setActionError(getErrorMessage(err))
    }
  }

  const handleEnable = async (user: UserResponse) => {
    setActionError(null)
    try {
      await userService.enable(user.id)
      await refetch()
    } catch (err) {
      setActionError(getErrorMessage(err))
    }
  }

  const users = data?.result ?? []
  const meta = data?.meta

  return (
    <section className="users-page">
      <header className="users-header">
        <div>
          <h1>Quản lý User</h1>
          <p className="user-muted">CRUD — vô hiệu hóa thay vì xóa cứng</p>
        </div>
        <button type="button" onClick={handleCreateClick}>
          + Thêm user
        </button>
      </header>

      {error && <p className="user-error">{error}</p>}
      {actionError && <p className="user-error">{actionError}</p>}

      <div style={{ marginBottom: '1rem' }}>
        <label>
          Trạng thái{' '}
          <select
            value={activeFilter}
            onChange={(e) => setActiveFilter(e.target.value as ActiveFilter)}
          >
            <option value="">Tất cả</option>
            <option value="true">Đang hoạt động</option>
            <option value="false">Đã vô hiệu hóa</option>
          </select>
        </label>
      </div>

      {formMode && (
        <UserForm
          mode={formMode}
          user={editingUser}
          isSubmitting={isSubmitting}
          onSubmit={handleSubmit}
          onCancel={handleCancelForm}
        />
      )}

      <UserList
        users={users}
        isLoading={isLoading}
        onEdit={handleEdit}
        onDisable={handleDisable}
        onEnable={handleEnable}
      />

      {meta && meta.pages > 1 && (
        <footer className="users-pagination">
          <button
            type="button"
            disabled={page <= 1 || isLoading}
            onClick={() => setPage(page - 1)}
          >
            Trước
          </button>
          <span>
            Trang {meta.page} / {meta.pages} ({meta.total} user)
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
