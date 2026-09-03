import { apiClient } from '../../../services/apiClient'
import type { PaginatedResult } from '../../../types/api.types'
import type { CreateDebtEntryRequest, DebtEntryResponse } from '../types/debt.types'

export interface DebtListParams {
  page?: number
  size?: number
  sort?: string
  customerId?: number
  workerId?: number
}

export const debtService = {
  getAll: ({
    page = 1,
    size = 10,
    sort = 'entryDate,desc',
    customerId,
    workerId,
  }: DebtListParams = {}) => {
    const params = new URLSearchParams({
      page: String(page),
      size: String(size),
      sort,
    })
    if (customerId != null) params.set('customerId', String(customerId))
    if (workerId != null) params.set('workerId', String(workerId))
    return apiClient<PaginatedResult<DebtEntryResponse>>(`/debts?${params}`)
  },

  getById: (id: number) => apiClient<DebtEntryResponse>(`/debts/${id}`),

  create: (data: CreateDebtEntryRequest) =>
    apiClient<DebtEntryResponse>('/debts', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
}
