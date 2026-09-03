import { ApiError, type ApiResponse } from '../types/api.types'

const API_BASE = import.meta.env.VITE_API_BASE ?? '/api/v1'

type TokenGetter = () => string | null
type TokenSetter = (token: string | null) => void

let getAccessToken: TokenGetter = () => null
let persistAccessToken: TokenSetter = () => {}
let refreshInFlight: Promise<string | null> | null = null

export function setTokenGetter(getter: TokenGetter) {
  getAccessToken = getter
}

export function setTokenPersister(setter: TokenSetter) {
  persistAccessToken = setter
}

function emptyResponse<T>(response: Response): ApiResponse<T> {
  return {
    statusCode: response.status,
    data: null as T,
    message: response.statusText || 'Empty response from server',
    timestamp: new Date().toISOString(),
  }
}

async function parseResponseBody<T>(
  response: Response,
): Promise<{ body: ApiResponse<T>; isEmpty: boolean }> {
  const text = await response.text()
  if (!text.trim()) {
    return { body: emptyResponse(response), isEmpty: true }
  }

  try {
    return { body: JSON.parse(text) as ApiResponse<T>, isEmpty: false }
  } catch {
    throw new ApiError(response.status, {
      statusCode: response.status,
      data: null,
      message: 'Invalid JSON response from server',
      timestamp: new Date().toISOString(),
    })
  }
}

function isAuthPublicPath(path: string) {
  return path.startsWith('/auth/login') || path.startsWith('/auth/refresh')
}

async function refreshAccessToken(): Promise<string | null> {
  if (refreshInFlight) {
    return refreshInFlight
  }
  refreshInFlight = (async () => {
    const response = await fetch(`${API_BASE}/auth/refresh`, {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: '{}',
    })
    if (!response.ok) {
      persistAccessToken(null)
      return null
    }
    const { body } = await parseResponseBody<{ accessToken: string }>(response)
    const token = body.data?.accessToken ?? null
    persistAccessToken(token)
    return token
  })().finally(() => {
    refreshInFlight = null
  })
  return refreshInFlight
}

export async function apiClient<T>(
  path: string,
  options?: RequestInit,
): Promise<ApiResponse<T>> {
  const send = async (token: string | null) => {
    const response = await fetch(`${API_BASE}${path}`, {
      ...options,
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
        ...(token && { Authorization: `Bearer ${token}` }),
        ...options?.headers,
      },
    })
    const parsed = await parseResponseBody<T>(response)
    return { response, ...parsed }
  }

  let token = getAccessToken()
  let { response, body, isEmpty } = await send(token)

  if (response.status === 401 && !isAuthPublicPath(path)) {
    const refreshed = await refreshAccessToken()
    if (refreshed) {
      const retry = await send(refreshed)
      response = retry.response
      body = retry.body
      isEmpty = retry.isEmpty
    }
  }

  if (!response.ok) {
    const message =
      isEmpty && response.status >= 500
        ? 'Không kết nối được backend — hãy chạy BE tại http://localhost:8080'
        : body.message || `Request failed (${response.status})`
    throw new ApiError(response.status, { ...body, message })
  }

  return body
}

export async function apiClientBlob(path: string): Promise<Blob> {
  const token = getAccessToken()
  const response = await fetch(`${API_BASE}${path}`, {
    credentials: 'include',
    headers: {
      ...(token && { Authorization: `Bearer ${token}` }),
    },
  })

  if (!response.ok) {
    const text = await response.text()
    let message = `Request failed (${response.status})`
    if (text.trim()) {
      try {
        const parsed = JSON.parse(text) as ApiResponse<unknown>
        message = parsed.message || message
      } catch {
        /* ignore */
      }
    } else if (response.status >= 500) {
      message = 'Không kết nối được backend — hãy chạy BE tại http://localhost:8080'
    }
    throw new ApiError(response.status, {
      statusCode: response.status,
      data: null,
      message,
      timestamp: new Date().toISOString(),
    })
  }

  return response.blob()
}
