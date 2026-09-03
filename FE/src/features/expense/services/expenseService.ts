import { apiClient } from '../../../services/apiClient'
import type { PaginatedResult } from '../../../types/api.types'
import type { CreateExpenseRequest, ExpenseResponse } from '../types/expense.types'

export const expenseService = {
  getAll: (page = 1, size = 10, sort = 'expenseDate,desc') => {
    const params = new URLSearchParams({
      page: String(page),
      size: String(size),
      sort,
    })
    return apiClient<PaginatedResult<ExpenseResponse>>(`/expenses?${params}`)
  },

  getById: (id: number) => apiClient<ExpenseResponse>(`/expenses/${id}`),

  create: (data: CreateExpenseRequest) =>
    apiClient<ExpenseResponse>('/expenses', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  cancel: (id: number) =>
    apiClient<ExpenseResponse>(`/expenses/${id}/cancel`, { method: 'POST' }),
}
