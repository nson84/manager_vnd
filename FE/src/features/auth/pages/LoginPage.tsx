import { useState } from 'react'

import { ApiError } from '../../../types/api.types'
import { useAuth } from '../context/AuthContext'
import '../components/login.css'

interface LoginPageProps {
  companyId: number
  companyName: string
  onBack: () => void
}

export function LoginPage({ companyId, companyName, onBack }: LoginPageProps) {
  const { login } = useAuth()
  const [email, setEmail] = useState('admin@local.dev')
  const [password, setPassword] = useState('password123')
  const [error, setError] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault()
    setIsSubmitting(true)
    setError(null)
    try {
      await login(email.trim(), password, companyId)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Đăng nhập thất bại')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <main className="login-page">
      <form className="login-panel" onSubmit={handleSubmit}>
        <p className="login-brand">Manager</p>
        <h1>Đăng nhập</h1>
        <p className="login-lead">{companyName}</p>
        {error && <p className="login-error">{error}</p>}
        <label>
          Email
          <input
            type="email"
            autoComplete="username"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </label>
        <label>
          Mật khẩu
          <input
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </label>
        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Đang vào...' : 'Đăng nhập'}
        </button>
        <button type="button" className="login-back" onClick={onBack}>
          Đổi cửa hàng
        </button>
      </form>
    </main>
  )
}
