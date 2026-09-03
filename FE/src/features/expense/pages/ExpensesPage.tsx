import { useState } from 'react'

import { ApiError } from '../../../types/api.types'
import { ExpenseForm } from '../components/ExpenseForm'
import { ExpenseList } from '../components/ExpenseList'
import '../../customer/components/customer.css'
import { useExpenses } from '../hooks/useExpenses'
import { expenseService } from '../services/expenseService'
import type {
  CreateExpenseRequest,
  ExpenseFormValues,
  ExpenseResponse,
} from '../types/expense.types'

function errorMessage(err: unknown): string {
  if (err instanceof ApiError) return err.message
  if (err instanceof Error) return err.message
  return 'Đã xảy ra lỗi'
}

function toCreate(values: ExpenseFormValues): CreateExpenseRequest {
  return {
    categoryId: Number(values.categoryId),
    amount: Number(values.amount),
    expenseDate: values.expenseDate,
    note: values.note.trim() || undefined,
  }
}

export function ExpensesPage() {
  const { data, isLoading, error, page, setPage, refetch } = useExpenses()
  const [showForm, setShowForm] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)

  const handleSubmit = async (values: ExpenseFormValues) => {
    setIsSubmitting(true)
    setActionError(null)
    try {
      await expenseService.create(toCreate(values))
      setShowForm(false)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleCancel = async (expense: ExpenseResponse) => {
    if (!window.confirm(`Hủy phiếu chi #${expense.id}?`)) return
    setActionError(null)
    try {
      await expenseService.cancel(expense.id)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    }
  }

  const expenses = data?.result ?? []
  const meta = data?.meta

  return (
    <section className="customer-page">
      <header className="customer-header">
        <div>
          <h1>Phiếu chi</h1>
          <p>Tạo phiếu chi · hủy khi cần</p>
        </div>
        <button
          type="button"
          className="primary"
          onClick={() => {
            setActionError(null)
            setShowForm(true)
          }}
        >
          + Tạo phiếu chi
        </button>
      </header>

      {(error || actionError) && <p className="customer-error">{error || actionError}</p>}

      {showForm && (
        <ExpenseForm
          isSubmitting={isSubmitting}
          onSubmit={handleSubmit}
          onCancel={() => setShowForm(false)}
        />
      )}

      <ExpenseList expenses={expenses} isLoading={isLoading} onCancel={handleCancel} />

      {meta && meta.pages > 1 && (
        <footer className="customer-pagination">
          <button type="button" disabled={page <= 1 || isLoading} onClick={() => setPage(page - 1)}>
            Trước
          </button>
          <span>
            Trang {meta.page} / {meta.pages} ({meta.total} phiếu)
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
