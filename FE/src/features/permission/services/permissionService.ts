import { apiClient } from '../../../services/apiClient'
import type { PaginatedResult } from '../../../types/api.types'
import type {
  CreatePermissionRequest,
  PermissionResponse,
  UpdatePermissionRequest,
} from '../types/permission.types'

export const permissionService = {
  getAll: (page = 1, size = 10, sort = 'id,asc') => {
    const params = new URLSearchParams({
      page: String(page),
      size: String(size),
      sort,
    })
    return apiClient<PaginatedResult<PermissionResponse>>(`/permissions?${params}`)
  },

  getById: (id: number) => apiClient<PermissionResponse>(`/permissions/${id}`),

  create: (data: CreatePermissionRequest) =>
    apiClient<PermissionResponse>('/permissions', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  update: (data: UpdatePermissionRequest) =>
    apiClient<PermissionResponse>('/permissions', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  delete: (id: number) =>
    apiClient<null>(`/permissions/${id}`, { method: 'DELETE' }),
}
