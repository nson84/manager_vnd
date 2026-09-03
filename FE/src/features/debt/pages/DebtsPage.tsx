import { useState } from 'react'

import { ApiError } from '../../../types/api.types'
import { DebtForm } from '../components/DebtForm'
import { DebtList } from '../components/DebtList'
import '../../customer/components/customer.css'
import { useDebts } from '../hooks/useDebts'
import { debtService } from '../services/debtService'
import type { CreateDebtEntryRequest, DebtFormValues } from '../types/debt.types'

function errorMessage(err: unknown): string {
  if (err instanceof ApiError) return err.message
  if (err instanceof Error) return err.message
  return 'Đã xảy ra lỗi'
}

function toCreate(values: DebtFormValues): CreateDebtEntryRequest {
  const partyId = Number(values.partyId)
  return {
    customerId: values.partyType === 'customer' ? partyId : undefined,
    workerId: values.partyType === 'worker' ? partyId : undefined,
    entryType: values.entryType,
    direction: values.entryType === 'ADJUST' ? values.direction || undefined : undefined,
    amount: Number(values.amount),
    entryDate: values.entryDate,
    note: values.note.trim() || undefined,
  }
}

export function DebtsPage() {
  const { data, isLoading, error, page, setPage, refetch } = useDebts()
  const [showForm, setShowForm] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)

  const handleSubmit = async (values: DebtFormValues) => {
    if (values.entryType === 'ADJUST' && !values.direction) {
      setActionError('ADJUST cần chọn hướng INCREASE hoặc DECREASE')
      return
    }
    setIsSubmitting(true)
    setActionError(null)
    try {
      await debtService.create(toCreate(values))
      setShowForm(false)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    } finally {
      setIsSubmitting(false)
    }
  }

  const entries = data?.result ?? []
  const meta = data?.meta

  return (
    <section className="customer-page">
      <header className="customer-header">
        <div>
          <h1>Công nợ</h1>
          <p>Chỉ tạo mới — không sửa / xóa</p>
        </div>
        <button
          type="button"
          className="primary"
          onClick={() => {
            setActionError(null)
            setShowForm(true)
          }}
        >
          + Ghi công nợ
        </button>
      </header>

      {(error || actionError) && <p className="customer-error">{error || actionError}</p>}

      {showForm && (
        <DebtForm
          isSubmitting={isSubmitting}
          onSubmit={handleSubmit}
          onCancel={() => setShowForm(false)}
        />
      )}

      <DebtList entries={entries} isLoading={isLoading} />

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
