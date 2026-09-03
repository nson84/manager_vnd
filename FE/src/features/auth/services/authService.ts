import { apiClient } from '../../../services/apiClient'
import type { AuthUser, LoginRequest, TokenResponse } from '../types/auth.types'

export const authService = {
  login: (data: LoginRequest) =>
    apiClient<TokenResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  getMe: () => apiClient<AuthUser>('/auth/me'),

  refresh: () =>
    apiClient<TokenResponse>('/auth/refresh', {
      method: 'POST',
      body: JSON.stringify({}),
    }),

  logout: () => apiClient<null>('/auth/logout', { method: 'POST' }),
}
