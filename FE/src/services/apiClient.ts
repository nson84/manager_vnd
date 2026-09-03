import { ApiError, type ApiResponse } from '../types/api.types'

const API_BASE = import.meta.env.VITE_API_BASE ?? '/api/v1'

type TokenGetter = () => string | null

let getAccessToken: TokenGetter = () => null

export function setTokenGetter(getter: TokenGetter) {
  getAccessToken = getter
}

export async function apiClient<T>(
  path: string,
  options?: RequestInit,
): Promise<ApiResponse<T>> {
  const token = getAccessToken()
  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }),
      ...options?.headers,
    },
  })

  const body = (await response.json()) as ApiResponse<T>

  if (!response.ok) {
    throw new ApiError(response.status, body)
  }

  return body
}
