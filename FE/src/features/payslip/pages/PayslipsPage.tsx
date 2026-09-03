import { useState } from 'react'

import { ApiError } from '../../../types/api.types'
import { PayslipForm } from '../components/PayslipForm'
import { PayslipList } from '../components/PayslipList'
import '../../customer/components/customer.css'
import { usePayslips } from '../hooks/usePayslips'
import { payslipService } from '../services/payslipService'
import type {
  CreatePayslipRequest,
  PayslipFormValues,
  PayslipResponse,
} from '../types/payslip.types'

function errorMessage(err: unknown): string {
  if (err instanceof ApiError) return err.message
  if (err instanceof Error) return err.message
  return 'Đã xảy ra lỗi'
}

function toCreate(values: PayslipFormValues): CreatePayslipRequest {
  return {
    workerId: Number(values.workerId),
    periodStart: values.periodStart,
    periodEnd: values.periodEnd,
    advanceDeducted: values.advanceDeducted ? Number(values.advanceDeducted) : 0,
    otherDeduction: values.otherDeduction ? Number(values.otherDeduction) : 0,
    note: values.note.trim() || undefined,
  }
}

export function PayslipsPage() {
  const { data, isLoading, error, page, setPage, refetch } = usePayslips()
  const [showForm, setShowForm] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)

  const handleSubmit = async (values: PayslipFormValues) => {
    setIsSubmitting(true)
    setActionError(null)
    try {
      await payslipService.create(toCreate(values))
      setShowForm(false)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    } finally {
      setIsSubmitting(false)
    }
  }

  const runAction = async (
    label: string,
    payslip: PayslipResponse,
    action: (id: number) => Promise<unknown>,
  ) => {
    if (!window.confirm(`${label} phiếu lương #${payslip.id}?`)) return
    setActionError(null)
    try {
      await action(payslip.id)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    }
  }

  const payslips = data?.result ?? []
  const meta = data?.meta

  return (
    <section className="customer-page">
      <header className="customer-header">
        <div>
          <h1>Phiếu lương</h1>
          <p>Tạo DRAFT · Confirm / Pay / Cancel</p>
        </div>
        <button
          type="button"
          className="primary"
          onClick={() => {
            setActionError(null)
            setShowForm(true)
          }}
        >
          + Tạo phiếu lương
        </button>
      </header>

      {(error || actionError) && <p className="customer-error">{error || actionError}</p>}

      {showForm && (
        <PayslipForm
          isSubmitting={isSubmitting}
          onSubmit={handleSubmit}
          onCancel={() => setShowForm(false)}
        />
      )}

      <PayslipList
        payslips={payslips}
        isLoading={isLoading}
        onConfirm={(p) => void runAction('Xác nhận', p, payslipService.confirm)}
        onPay={(p) => void runAction('Thanh toán', p, payslipService.pay)}
        onCancel={(p) => void runAction('Hủy', p, payslipService.cancel)}
      />

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
