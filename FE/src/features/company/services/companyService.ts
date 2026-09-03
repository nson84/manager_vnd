import { apiClient } from '../../../services/apiClient'
import type { PaginatedResult } from '../../../types/api.types'
import type {
  CompanyResponse,
  CreateCompanyRequest,
  UpdateCompanyRequest,
} from '../types/company.types'

export const companyService = {
  getAll: (page = 1, size = 10, sort = 'id,asc', active?: boolean) => {
    const params = new URLSearchParams({
      page: String(page),
      size: String(size),
      sort,
    })
    if (active !== undefined) {
      params.set('active', String(active))
    }
    return apiClient<PaginatedResult<CompanyResponse>>(`/companies?${params}`)
  },

  getById: (id: number) => apiClient<CompanyResponse>(`/companies/${id}`),

  create: (data: CreateCompanyRequest) =>
    apiClient<CompanyResponse>('/companies', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  update: (data: UpdateCompanyRequest) =>
    apiClient<CompanyResponse>('/companies', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  disable: (id: number) =>
    apiClient<CompanyResponse>(`/companies/${id}`, { method: 'DELETE' }),

  enable: (id: number) =>
    apiClient<CompanyResponse>(`/companies/${id}/enable`, { method: 'POST' }),
}
