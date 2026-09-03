import { useState } from 'react'

import { ApiError } from '../../../types/api.types'
import { WorkerForm } from '../components/WorkerForm'
import { WorkerList } from '../components/WorkerList'
import '../../customer/components/customer.css'
import { useWorkers } from '../hooks/useWorkers'
import { workerService } from '../services/workerService'
import type {
  ActiveFilter,
  CreateWorkerRequest,
  UpdateWorkerRequest,
  WorkerFormValues,
  WorkerResponse,
} from '../types/worker.types'

type FormMode = 'create' | 'edit' | null

function errorMessage(err: unknown): string {
  if (err instanceof ApiError) return err.message
  if (err instanceof Error) return err.message
  return 'Đã xảy ra lỗi'
}

function toCreate(values: WorkerFormValues): CreateWorkerRequest {
  return {
    name: values.name.trim(),
    phone: values.phone.trim() || undefined,
    address: values.address.trim() || undefined,
    jobTitle: values.jobTitle.trim() || undefined,
    wageType: values.wageType,
    defaultUnitRate: Number(values.defaultUnitRate),
    hireDate: values.hireDate || undefined,
    note: values.note.trim() || undefined,
  }
}

function toUpdate(id: number, values: WorkerFormValues): UpdateWorkerRequest {
  return {
    id,
    name: values.name.trim(),
    phone: values.phone.trim(),
    address: values.address.trim(),
    jobTitle: values.jobTitle.trim(),
    wageType: values.wageType,
    defaultUnitRate: Number(values.defaultUnitRate),
    hireDate: values.hireDate || undefined,
    note: values.note.trim(),
  }
}

export function WorkersPage() {
  const {
    data,
    isLoading,
    error,
    page,
    setPage,
    activeFilter,
    setActiveFilter,
    q,
    setQ,
    applySearch,
    refetch,
  } = useWorkers()

  const [formMode, setFormMode] = useState<FormMode>(null)
  const [editing, setEditing] = useState<WorkerResponse | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)

  const handleSubmit = async (values: WorkerFormValues) => {
    setIsSubmitting(true)
    setActionError(null)
    try {
      if (formMode === 'create') {
        await workerService.create(toCreate(values))
      } else if (formMode === 'edit' && editing) {
        await workerService.update(toUpdate(editing.id, values))
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

  const handleDisable = async (worker: WorkerResponse) => {
    if (!window.confirm(`Vô hiệu hóa thợ "${worker.name}"? (không xóa dữ liệu)`)) return
    setActionError(null)
    try {
      await workerService.disable(worker.id)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    }
  }

  const handleEnable = async (worker: WorkerResponse) => {
    setActionError(null)
    try {
      await workerService.enable(worker.id)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    }
  }

  const workers = data?.result ?? []
  const meta = data?.meta

  return (
    <section className="customer-page">
      <header className="customer-header">
        <div>
          <h1>Thợ</h1>
          <p>CRUD — vô hiệu hóa thay vì xóa · ứng trước chỉ xem</p>
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
          + Thêm thợ
        </button>
      </header>

      {(error || actionError) && <p className="customer-error">{error || actionError}</p>}

      <div className="customer-toolbar">
        <label>
          Trạng thái
          <select
            value={activeFilter}
            onChange={(e) => setActiveFilter(e.target.value as ActiveFilter)}
          >
            <option value="">Tất cả</option>
            <option value="true">Đang hoạt động</option>
            <option value="false">Đã vô hiệu hóa</option>
          </select>
        </label>
        <label>
          Tìm kiếm
          <input
            value={q}
            placeholder="Tên / SĐT / ghi chú"
            onChange={(e) => setQ(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter') applySearch()
            }}
          />
        </label>
        <button type="button" onClick={applySearch}>
          Tìm
        </button>
      </div>

      {formMode && (
        <WorkerForm
          mode={formMode}
          worker={editing}
          isSubmitting={isSubmitting}
          onSubmit={handleSubmit}
          onCancel={() => {
            setFormMode(null)
            setEditing(null)
          }}
        />
      )}

      <WorkerList
        workers={workers}
        isLoading={isLoading}
        onEdit={(worker) => {
          setActionError(null)
          setEditing(worker)
          setFormMode('edit')
        }}
        onDisable={handleDisable}
        onEnable={handleEnable}
      />

      {meta && meta.pages > 1 && (
        <footer className="customer-pagination">
          <button type="button" disabled={page <= 1 || isLoading} onClick={() => setPage(page - 1)}>
            Trước
          </button>
          <span>
            Trang {meta.page} / {meta.pages} ({meta.total} thợ)
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
