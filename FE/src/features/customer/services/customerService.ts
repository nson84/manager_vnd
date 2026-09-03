import { apiClient } from '../../../services/apiClient'
import type { PaginatedResult } from '../../../types/api.types'
import type {
  CreateCustomerRequest,
  CustomerResponse,
  UpdateCustomerRequest,
} from '../types/customer.types'

export const customerService = {
  getAll: (page = 1, size = 10, sort = 'id,asc', active?: boolean, q?: string) => {
    const params = new URLSearchParams({
      page: String(page),
      size: String(size),
      sort,
    })
    if (active !== undefined) params.set('active', String(active))
    if (q?.trim()) params.set('q', q.trim())
    return apiClient<PaginatedResult<CustomerResponse>>(`/customers?${params}`)
  },

  getById: (id: number) => apiClient<CustomerResponse>(`/customers/${id}`),

  create: (data: CreateCustomerRequest) =>
    apiClient<CustomerResponse>('/customers', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  update: (data: UpdateCustomerRequest) =>
    apiClient<CustomerResponse>('/customers', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  disable: (id: number) =>
    apiClient<CustomerResponse>(`/customers/${id}`, { method: 'DELETE' }),

  enable: (id: number) =>
    apiClient<CustomerResponse>(`/customers/${id}/enable`, { method: 'POST' }),
}
