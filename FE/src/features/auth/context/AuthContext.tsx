import { createContext, useContext, useEffect, useState, type ReactNode } from 'react'

import { setTokenGetter, setTokenPersister } from '../../../services/apiClient'
import { ApiError } from '../../../types/api.types'
import { authService } from '../services/authService'
import type { AuthUser } from '../types/auth.types'

const ACCESS_TOKEN_KEY = 'accessToken'

let accessToken: string | null = sessionStorage.getItem(ACCESS_TOKEN_KEY)

function persistToken(token: string | null) {
  accessToken = token
  if (token) {
    sessionStorage.setItem(ACCESS_TOKEN_KEY, token)
  } else {
    sessionStorage.removeItem(ACCESS_TOKEN_KEY)
  }
}

setTokenGetter(() => accessToken)
setTokenPersister(persistToken)

interface AuthContextValue {
  user: AuthUser | null
  isReady: boolean
  login: (email: string, password: string) => Promise<void>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null)
  const [isReady, setIsReady] = useState(false)

  useEffect(() => {
    let cancelled = false
    if (!accessToken) {
      setIsReady(true)
      return
    }
    authService
      .getMe()
      .then((res) => {
        if (!cancelled) setUser(res.data)
      })
      .catch(() => {
        persistToken(null)
        if (!cancelled) setUser(null)
      })
      .finally(() => {
        if (!cancelled) setIsReady(true)
      })
    return () => {
      cancelled = true
    }
  }, [])

  const login = async (email: string, password: string) => {
    const tokenRes = await authService.login({ email, password })
    persistToken(tokenRes.data.accessToken)
    const me = await authService.getMe()
    setUser(me.data)
  }

  const logout = async () => {
    try {
      await authService.logout()
    } catch (err) {
      if (!(err instanceof ApiError)) throw err
    } finally {
      persistToken(null)
      setUser(null)
    }
  }

  return (
    <AuthContext.Provider value={{ user, isReady, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return ctx
}
