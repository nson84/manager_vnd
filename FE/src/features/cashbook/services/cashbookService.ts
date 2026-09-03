import { apiClient, apiClientBlob } from '../../../services/apiClient'
import type { PaginatedResult } from '../../../types/api.types'
import type {
  CashEntryResponse,
  CashStatsResponse,
  CashbookFilters,
  CategorySummary,
  CreateManualCashEntryRequest,
} from '../types/cashbook.types'

function buildQuery(filters: CashbookFilters, page?: number, size?: number, sort?: string): string {
  const params = new URLSearchParams()
  if (page != null) params.set('page', String(page))
  if (size != null) params.set('size', String(size))
  if (sort) params.set('sort', sort)
  if (filters.fromDate) params.set('fromDate', filters.fromDate)
  if (filters.toDate) params.set('toDate', filters.toDate)
  if (filters.direction) params.set('direction', filters.direction)
  if (filters.categoryId) params.set('categoryId', filters.categoryId)
  if (filters.refType) params.set('refType', filters.refType)
  if (filters.checked) params.set('checked', filters.checked)
  if (filters.q.trim()) params.set('q', filters.q.trim())
  return params.toString()
}

export const cashbookService = {
  getAll: (filters: CashbookFilters, page = 1, size = 10) =>
    apiClient<PaginatedResult<CashEntryResponse>>(
      `/cashbook?${buildQuery(filters, page, size, 'entryDate,desc')}`,
    ),

  getStats: (filters: CashbookFilters) =>
    apiClient<CashStatsResponse>(`/cashbook/stats?${buildQuery(filters)}`),

  getById: (id: number) => apiClient<CashEntryResponse>(`/cashbook/${id}`),

  listCategories: () => apiClient<CategorySummary[]>('/cashbook/categories'),

  createManual: (data: CreateManualCashEntryRequest) =>
    apiClient<CashEntryResponse>('/cashbook', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  updateNote: (id: number, note: string | null) =>
    apiClient<CashEntryResponse>(`/cashbook/${id}/note`, {
      method: 'PATCH',
      body: JSON.stringify({ note }),
    }),

  updateChecked: (id: number, checked: boolean) =>
    apiClient<CashEntryResponse>(`/cashbook/${id}/checked`, {
      method: 'PATCH',
      body: JSON.stringify({ checked }),
    }),

  delete: (id: number) => apiClient<null>(`/cashbook/${id}`, { method: 'DELETE' }),

  exportPdf: async (filters: CashbookFilters) => {
    const blob = await apiClientBlob(`/cashbook/export/pdf?${buildQuery(filters)}`)
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `so-quy-${filters.fromDate}_${filters.toDate}.pdf`
    link.click()
    URL.revokeObjectURL(url)
  },
}
