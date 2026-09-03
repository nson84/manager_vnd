import { useState } from 'react'

import { ApiError } from '../../../types/api.types'
import { WageForm } from '../components/WageForm'
import { WageList } from '../components/WageList'
import '../../customer/components/customer.css'
import { useWages } from '../hooks/useWages'
import { wageService } from '../services/wageService'
import type {
  CreateWageEntryRequest,
  UpdateWageEntryRequest,
  WageEntryResponse,
  WageFormValues,
  WageType,
} from '../types/wage.types'

type FormMode = 'create' | 'edit' | null

function errorMessage(err: unknown): string {
  if (err instanceof ApiError) return err.message
  if (err instanceof Error) return err.message
  return 'Đã xảy ra lỗi'
}

function toCreate(values: WageFormValues): CreateWageEntryRequest {
  return {
    workerId: Number(values.workerId),
    workDate: values.workDate,
    wageType: values.wageType || undefined,
    quantity: Number(values.quantity),
    unitRate: values.unitRate ? Number(values.unitRate) : undefined,
    note: values.note.trim() || undefined,
  }
}

function toUpdate(id: number, values: WageFormValues): UpdateWageEntryRequest {
  return {
    id,
    workDate: values.workDate,
    wageType: (values.wageType || undefined) as WageType | undefined,
    quantity: Number(values.quantity),
    unitRate: values.unitRate ? Number(values.unitRate) : undefined,
    note: values.note.trim(),
  }
}

export function WagesPage() {
  const {
    data,
    isLoading,
    error,
    page,
    setPage,
    workerIdFilter,
    setWorkerIdFilter,
    unpaidOnly,
    setUnpaidOnly,
    applyFilters,
    refetch,
  } = useWages()

  const [formMode, setFormMode] = useState<FormMode>(null)
  const [editing, setEditing] = useState<WageEntryResponse | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)

  const handleSubmit = async (values: WageFormValues) => {
    setIsSubmitting(true)
    setActionError(null)
    try {
      if (formMode === 'create') {
        await wageService.create(toCreate(values))
      } else if (formMode === 'edit' && editing) {
        await wageService.update(toUpdate(editing.id, values))
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

  const handleDelete = async (entry: WageEntryResponse) => {
    if (entry.payslipId) return
    if (!window.confirm(`Xóa ghi công #${entry.id}?`)) return
    setActionError(null)
    try {
      await wageService.delete(entry.id)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    }
  }

  const entries = data?.result ?? []
  const meta = data?.meta

  return (
    <section className="customer-page">
      <header className="customer-header">
        <div>
          <h1>Ghi công</h1>
          <p>Tạo / sửa / xóa khi chưa gắn phiếu lương · thành tiền chỉ xem</p>
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
          + Ghi công
        </button>
      </header>

      {(error || actionError) && <p className="customer-error">{error || actionError}</p>}

      <div className="customer-toolbar">
        <label>
          Worker ID
          <input
            type="number"
            min="1"
            value={workerIdFilter}
            placeholder="Tất cả"
            onChange={(e) => setWorkerIdFilter(e.target.value)}
          />
        </label>
        <label>
          Chỉ chưa quyết toán
          <select
            value={unpaidOnly ? 'true' : 'false'}
            onChange={(e) => setUnpaidOnly(e.target.value === 'true')}
          >
            <option value="false">Tất cả</option>
            <option value="true">Chưa gắn phiếu lương</option>
          </select>
        </label>
        <button type="button" onClick={applyFilters}>
          Lọc
        </button>
      </div>

      {formMode && (
        <WageForm
          mode={formMode}
          entry={editing}
          isSubmitting={isSubmitting}
          onSubmit={handleSubmit}
          onCancel={() => {
            setFormMode(null)
            setEditing(null)
          }}
        />
      )}

      <WageList
        entries={entries}
        isLoading={isLoading}
        onEdit={(entry) => {
          setActionError(null)
          setEditing(entry)
          setFormMode('edit')
        }}
        onDelete={handleDelete}
      />

      {meta && meta.pages > 1 && (
        <footer className="customer-pagination">
          <button type="button" disabled={page <= 1 || isLoading} onClick={() => setPage(page - 1)}>
            Trước
          </button>
          <span>
            Trang {meta.page} / {meta.pages} ({meta.total} dòng)
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
