import { apiClient } from '../../../services/apiClient'
import type { PaginatedResult } from '../../../types/api.types'
import type { CreatePayslipRequest, PayslipResponse } from '../types/payslip.types'

export const payslipService = {
  getAll: (page = 1, size = 10, sort = 'id,desc') => {
    const params = new URLSearchParams({
      page: String(page),
      size: String(size),
      sort,
    })
    return apiClient<PaginatedResult<PayslipResponse>>(`/payslips?${params}`)
  },

  getById: (id: number) => apiClient<PayslipResponse>(`/payslips/${id}`),

  create: (data: CreatePayslipRequest) =>
    apiClient<PayslipResponse>('/payslips', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  confirm: (id: number) =>
    apiClient<PayslipResponse>(`/payslips/${id}/confirm`, { method: 'POST' }),

  pay: (id: number) => apiClient<PayslipResponse>(`/payslips/${id}/pay`, { method: 'POST' }),

  cancel: (id: number) =>
    apiClient<PayslipResponse>(`/payslips/${id}/cancel`, { method: 'POST' }),
}
