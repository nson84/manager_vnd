import { useState } from 'react'

import { ApiError } from '../../../types/api.types'
import { CashEntryDetail } from '../components/CashEntryDetail'
import { CashEntryForm } from '../components/CashEntryForm'
import { CashEntryTable } from '../components/CashEntryTable'
import { CashFilterBar } from '../components/CashFilterBar'
import { CashStats } from '../components/CashStats'
import '../components/cashbook.css'
import { useCashbook } from '../hooks/useCashbook'
import { cashbookService } from '../services/cashbookService'
import type { CashEntryResponse, CreateManualCashEntryRequest } from '../types/cashbook.types'

function errorMessage(err: unknown): string {
  if (err instanceof ApiError) return err.message
  if (err instanceof Error) return err.message
  return 'Đã xảy ra lỗi'
}

export function CashbookPage() {
  const {
    filters,
    setFilters,
    applied,
    applyFilters,
    resetFilters,
    data,
    stats,
    categories,
    isLoading,
    error,
    page,
    setPage,
    refetch,
  } = useCashbook()

  const [showCreate, setShowCreate] = useState(false)
  const [detail, setDetail] = useState<CashEntryResponse | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)

  const handleCreate = async (payload: CreateManualCashEntryRequest) => {
    setIsSubmitting(true)
    setActionError(null)
    try {
      await cashbookService.createManual(payload)
      setShowCreate(false)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleToggleChecked = async (entry: CashEntryResponse) => {
    setActionError(null)
    try {
      await cashbookService.updateChecked(entry.id, !entry.checked)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    }
  }

  const handleEditNote = async (entry: CashEntryResponse) => {
    const next = window.prompt('Ghi chú', entry.note ?? '')
    if (next === null) return
    setActionError(null)
    try {
      await cashbookService.updateNote(entry.id, next.trim() || null)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    }
  }

  const handleDelete = async (entry: CashEntryResponse) => {
    if (!window.confirm(`Xóa phiếu MANUAL #${entry.id}?`)) return
    setActionError(null)
    try {
      await cashbookService.delete(entry.id)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    }
  }

  const handleExport = async () => {
    setActionError(null)
    try {
      await cashbookService.exportPdf(applied)
    } catch (err) {
      setActionError(errorMessage(err))
    }
  }

  const entries = data?.result ?? []
  const meta = data?.meta

  return (
    <section className="cashbook-page">
      <header className="cashbook-header">
        <div>
          <h1>Sổ quỹ</h1>
          <p>Thống kê · lọc theo ngày (VN) · đối chiếu · xuất PDF</p>
        </div>
        <div className="cashbook-header-actions">
          <button type="button" onClick={handleExport}>
            Xuất PDF
          </button>
          <button type="button" className="primary" onClick={() => setShowCreate(true)}>
            + Thêm phiếu
          </button>
        </div>
      </header>

      {(error || actionError) && <p className="cash-error">{error || actionError}</p>}

      <CashStats stats={stats} isLoading={isLoading} />

      <CashFilterBar
        filters={filters}
        categories={categories}
        onChange={setFilters}
        onApply={applyFilters}
        onReset={resetFilters}
      />

      <CashEntryTable
        entries={entries}
        isLoading={isLoading}
        onToggleChecked={handleToggleChecked}
        onEditNote={handleEditNote}
        onDetail={setDetail}
        onDelete={handleDelete}
      />

      {meta && meta.pages > 1 && (
        <footer className="cash-pagination">
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

      {showCreate && (
        <CashEntryForm
          categories={categories}
          isSubmitting={isSubmitting}
          onSubmit={handleCreate}
          onCancel={() => setShowCreate(false)}
        />
      )}

      {detail && <CashEntryDetail entry={detail} onClose={() => setDetail(null)} />}
    </section>
  )
}
