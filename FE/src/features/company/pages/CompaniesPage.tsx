import { useState } from 'react'

import { ApiError } from '../../../types/api.types'
import { CompanyForm } from '../components/CompanyForm'
import { CompanyList } from '../components/CompanyList'
import '../components/company.css'
import { useCompanies } from '../hooks/useCompanies'
import { companyService } from '../services/companyService'
import type {
  ActiveFilter,
  CompanyFormValues,
  CompanyResponse,
  CreateCompanyRequest,
  UpdateCompanyRequest,
} from '../types/company.types'

type FormMode = 'create' | 'edit' | null

function errorMessage(err: unknown): string {
  if (err instanceof ApiError) return err.message
  if (err instanceof Error) return err.message
  return 'Đã xảy ra lỗi'
}

function toCreate(values: CompanyFormValues): CreateCompanyRequest {
  return {
    name: values.name.trim(),
    description: values.description.trim() || undefined,
    address: values.address.trim() || undefined,
    logo: values.logo.trim() || undefined,
  }
}

function toUpdate(id: number, values: CompanyFormValues): UpdateCompanyRequest {
  return {
    id,
    name: values.name.trim(),
    description: values.description.trim() || undefined,
    address: values.address.trim() || undefined,
    logo: values.logo.trim() || undefined,
  }
}

export function CompaniesPage() {
  const {
    data,
    isLoading,
    error,
    page,
    setPage,
    activeFilter,
    setActiveFilter,
    refetch,
  } = useCompanies()

  const [formMode, setFormMode] = useState<FormMode>(null)
  const [editing, setEditing] = useState<CompanyResponse | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)

  const handleSubmit = async (values: CompanyFormValues) => {
    setIsSubmitting(true)
    setActionError(null)
    try {
      if (formMode === 'create') {
        await companyService.create(toCreate(values))
      } else if (formMode === 'edit' && editing) {
        await companyService.update(toUpdate(editing.id, values))
      }
      setFormMode(null)
      setEditing(null)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleDisable = async (company: CompanyResponse) => {
    if (!window.confirm(`Vô hiệu hóa công ty "${company.name}"? (không xóa dữ liệu)`)) {
      return
    }
    setActionError(null)
    try {
      await companyService.disable(company.id)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    }
  }

  const handleEnable = async (company: CompanyResponse) => {
    setActionError(null)
    try {
      await companyService.enable(company.id)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    }
  }

  const companies = data?.result ?? []
  const meta = data?.meta

  return (
    <section className="company-page">
      <header className="company-header">
        <div>
          <h1>Quản lý công ty</h1>
          <p>CRUD — vô hiệu hóa thay vì xóa cứng</p>
        </div>
        <button
          type="button"
          className="primary"
          onClick={() => {
            setActionError(null)
            setEditing(null)
            setFormMode('create')
          }}
        >
          + Thêm công ty
        </button>
      </header>

      {(error || actionError) && <p className="company-error">{error || actionError}</p>}

      <div className="company-toolbar">
        <label>
          Trạng thái{' '}
          <select
            value={activeFilter}
            onChange={(e) => setActiveFilter(e.target.value as ActiveFilter)}
          >
            <option value="">Tất cả</option>
            <option value="true">Đang hoạt động</option>
            <option value="false">Đã vô hiệu hóa</option>
          </select>
        </label>
      </div>

      {formMode && (
        <CompanyForm
          mode={formMode}
          company={editing}
          isSubmitting={isSubmitting}
          onSubmit={handleSubmit}
          onCancel={() => {
            setFormMode(null)
            setEditing(null)
          }}
        />
      )}

      <CompanyList
        companies={companies}
        isLoading={isLoading}
        onEdit={(company) => {
          setActionError(null)
          setEditing(company)
          setFormMode('edit')
        }}
        onDisable={handleDisable}
        onEnable={handleEnable}
      />

      {meta && meta.pages > 1 && (
        <footer className="company-pagination">
          <button type="button" disabled={page <= 1 || isLoading} onClick={() => setPage(page - 1)}>
            Trước
          </button>
          <span>
            Trang {meta.page} / {meta.pages} ({meta.total} công ty)
          </span>
          <button
            type="button"
            disabled={page >= meta.pages || isLoading}
            onClick={() => setPage(page + 1)}
          >
            Sau
          </button>
        </footer>
      )}
    </section>
  )
}
