export interface AuthUser {
  id: number
  name: string
  email: string
  age?: number | null
  gender?: string | null
  address?: string | null
  avatar?: string | null
  active: boolean
  company?: { id: number; name: string } | null
  roles: { id: number; name: string }[]
}

export interface LoginRequest {
  email: string
  password: string
}

export interface TokenResponse {
  accessToken: string
  refreshToken: string
}
