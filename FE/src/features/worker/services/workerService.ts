import { apiClient } from '../../../services/apiClient'
import type { PaginatedResult } from '../../../types/api.types'
import type {
  CreateWorkerRequest,
  UpdateWorkerRequest,
  WorkerResponse,
} from '../types/worker.types'

export const workerService = {
  getAll: (page = 1, size = 10, sort = 'id,asc', active?: boolean, q?: string) => {
    const params = new URLSearchParams({
      page: String(page),
      size: String(size),
      sort,
    })
    if (active !== undefined) params.set('active', String(active))
    if (q?.trim()) params.set('q', q.trim())
    return apiClient<PaginatedResult<WorkerResponse>>(`/workers?${params}`)
  },

  getById: (id: number) => apiClient<WorkerResponse>(`/workers/${id}`),

  create: (data: CreateWorkerRequest) =>
    apiClient<WorkerResponse>('/workers', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  update: (data: UpdateWorkerRequest) =>
    apiClient<WorkerResponse>('/workers', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  disable: (id: number) =>
    apiClient<WorkerResponse>(`/workers/${id}`, { method: 'DELETE' }),

  enable: (id: number) =>
    apiClient<WorkerResponse>(`/workers/${id}/enable`, { method: 'POST' }),
}
