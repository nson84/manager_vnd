import { useEffect, useState } from 'react'

import { ApiError } from '../../../types/api.types'
import { companyService } from '../services/companyService'
import type { PublicCompany } from '../types/company.types'
import '../../auth/components/login.css'

interface CompanyPickerPageProps {
  onSelect: (company: PublicCompany) => void
}

export function CompanyPickerPage({ onSelect }: CompanyPickerPageProps) {
  const [companies, setCompanies] = useState<PublicCompany[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false
    companyService
      .listPublic()
      .then((res) => {
        if (!cancelled) setCompanies(res.data ?? [])
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err instanceof ApiError ? err.message : 'Không tải được danh sách cửa hàng')
        }
      })
      .finally(() => {
        if (!cancelled) setIsLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [])

  return (
    <main className="login-page">
      <section className="login-panel shop-picker">
        <p className="login-brand">Manager</p>
        <h1>Chọn cửa hàng</h1>
        <p className="login-lead">Chọn công ty rồi đăng nhập vào hệ thống của cửa hàng đó.</p>
        {error && <p className="login-error">{error}</p>}
        {isLoading && <p className="login-lead">Đang tải cửa hàng...</p>}
        {!isLoading && companies.length === 0 && !error && (
          <p className="login-lead">Chưa có cửa hàng nào.</p>
        )}
        <ul className="shop-picker-list">
          {companies.map((company) => (
            <li key={company.id}>
              <button type="button" onClick={() => onSelect(company)}>
                <strong>{company.name}</strong>
                {company.description && <span>{company.description}</span>}
              </button>
            </li>
          ))}
        </ul>
      </section>
    </main>
  )
}
