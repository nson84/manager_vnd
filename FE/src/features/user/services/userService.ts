import { apiClient } from '../../../services/apiClient'
import type { PaginatedResult } from '../../../types/api.types'
import type {
  CreateUserRequest,
  UpdateUserRequest,
  UserResponse,
} from '../types/user.types'

export const userService = {
  getAll: (page = 1, size = 10, sort = 'id,asc', active?: boolean) => {
    const params = new URLSearchParams({
      page: String(page),
      size: String(size),
      sort,
    })
    if (active !== undefined) {
      params.set('active', String(active))
    }
    return apiClient<PaginatedResult<UserResponse>>(`/users?${params}`)
  },

  getById: (id: number) => apiClient<UserResponse>(`/users/${id}`),

  create: (data: CreateUserRequest) =>
    apiClient<UserResponse>('/users', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  update: (data: UpdateUserRequest) =>
    apiClient<UserResponse>('/users', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  disable: (id: number) =>
    apiClient<UserResponse>(`/users/${id}`, { method: 'DELETE' }),

  enable: (id: number) =>
    apiClient<UserResponse>(`/users/${id}/enable`, { method: 'POST' }),
}
