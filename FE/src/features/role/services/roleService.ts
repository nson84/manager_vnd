import { apiClient } from '../../../services/apiClient'
import type { PaginatedResult } from '../../../types/api.types'
import type { CreateRoleRequest, RoleResponse, UpdateRoleRequest } from '../types/role.types'

export const roleService = {
  getAll: (page = 1, size = 10, sort = 'id,asc') => {
    const params = new URLSearchParams({
      page: String(page),
      size: String(size),
      sort,
    })
    return apiClient<PaginatedResult<RoleResponse>>(`/roles?${params}`)
  },

  getById: (id: number) => apiClient<RoleResponse>(`/roles/${id}`),

  create: (data: CreateRoleRequest) =>
    apiClient<RoleResponse>('/roles', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  update: (data: UpdateRoleRequest) =>
    apiClient<RoleResponse>('/roles', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  delete: (id: number) => apiClient<null>(`/roles/${id}`, { method: 'DELETE' }),
}
