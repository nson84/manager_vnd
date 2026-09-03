import { apiClient } from '../../../services/apiClient'
import type { PaginatedResult } from '../../../types/api.types'
import type {
  CreateWageEntryRequest,
  UpdateWageEntryRequest,
  WageEntryResponse,
} from '../types/wage.types'

export interface WageListParams {
  page?: number
  size?: number
  sort?: string
  workerId?: number
  unpaidOnly?: boolean
}

export const wageService = {
  getAll: ({
    page = 1,
    size = 10,
    sort = 'workDate,desc',
    workerId,
    unpaidOnly,
  }: WageListParams = {}) => {
    const params = new URLSearchParams({
      page: String(page),
      size: String(size),
      sort,
    })
    if (workerId != null) params.set('workerId', String(workerId))
    if (unpaidOnly !== undefined) params.set('unpaidOnly', String(unpaidOnly))
    return apiClient<PaginatedResult<WageEntryResponse>>(`/wages?${params}`)
  },

  getById: (id: number) => apiClient<WageEntryResponse>(`/wages/${id}`),

  create: (data: CreateWageEntryRequest) =>
    apiClient<WageEntryResponse>('/wages', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  update: (data: UpdateWageEntryRequest) =>
    apiClient<WageEntryResponse>('/wages', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  delete: (id: number) => apiClient<null>(`/wages/${id}`, { method: 'DELETE' }),
}
