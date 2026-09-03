import { useState } from 'react'

import { ApiError } from '../../../types/api.types'
import { CustomerForm } from '../components/CustomerForm'
import { CustomerList } from '../components/CustomerList'
import '../components/customer.css'
import { useCustomers } from '../hooks/useCustomers'
import { customerService } from '../services/customerService'
import type {
  ActiveFilter,
  CreateCustomerRequest,
  CustomerFormValues,
  CustomerResponse,
  UpdateCustomerRequest,
} from '../types/customer.types'

type FormMode = 'create' | 'edit' | null

function errorMessage(err: unknown): string {
  if (err instanceof ApiError) return err.message
  if (err instanceof Error) return err.message
  return 'Đã xảy ra lỗi'
}

function toCreate(values: CustomerFormValues): CreateCustomerRequest {
  return {
    name: values.name.trim(),
    phone: values.phone.trim() || undefined,
    address: values.address.trim() || undefined,
    note: values.note.trim() || undefined,
  }
}

function toUpdate(id: number, values: CustomerFormValues): UpdateCustomerRequest {
  return {
    id,
    name: values.name.trim(),
    phone: values.phone.trim(),
    address: values.address.trim(),
    note: values.note.trim(),
  }
}

export function CustomersPage() {
  const {
    data,
    isLoading,
    error,
    page,
    setPage,
    activeFilter,
    setActiveFilter,
    q,
    setQ,
    applySearch,
    refetch,
  } = useCustomers()

  const [formMode, setFormMode] = useState<FormMode>(null)
  const [editing, setEditing] = useState<CustomerResponse | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)

  const handleSubmit = async (values: CustomerFormValues) => {
    setIsSubmitting(true)
    setActionError(null)
    try {
      if (formMode === 'create') {
        await customerService.create(toCreate(values))
      } else if (formMode === 'edit' && editing) {
        await customerService.update(toUpdate(editing.id, values))
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

  const handleDisable = async (customer: CustomerResponse) => {
    if (!window.confirm(`Vô hiệu hóa khách "${customer.name}"? (không xóa dữ liệu)`)) return
    setActionError(null)
    try {
      await customerService.disable(customer.id)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    }
  }

  const handleEnable = async (customer: CustomerResponse) => {
    setActionError(null)
    try {
      await customerService.enable(customer.id)
      await refetch()
    } catch (err) {
      setActionError(errorMessage(err))
    }
  }

  const customers = data?.result ?? []
  const meta = data?.meta

  return (
    <section className="customer-page">
      <header className="customer-header">
        <div>
          <h1>Khách hàng</h1>
          <p>CRUD — vô hiệu hóa thay vì xóa · công nợ chỉ xem</p>
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
          + Thêm khách
        </button>
      </header>

      {(error || actionError) && <p className="customer-error">{error || actionError}</p>}

      <div className="customer-toolbar">
        <label>
          Trạng thái
          <select
            value={activeFilter}
            onChange={(e) => setActiveFilter(e.target.value as ActiveFilter)}
          >
            <option value="">Tất cả</option>
            <option value="true">Đang hoạt động</option>
            <option value="false">Đã vô hiệu hóa</option>
          </select>
        </label>
        <label>
          Tìm kiếm
          <input
            value={q}
            placeholder="Tên / SĐT / ghi chú"
            onChange={(e) => setQ(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter') applySearch()
            }}
          />
        </label>
        <button type="button" onClick={applySearch}>
          Tìm
        </button>
      </div>

      {formMode && (
        <CustomerForm
          mode={formMode}
          customer={editing}
          isSubmitting={isSubmitting}
          onSubmit={handleSubmit}
          onCancel={() => {
            setFormMode(null)
            setEditing(null)
          }}
        />
      )}

      <CustomerList
        customers={customers}
        isLoading={isLoading}
        onEdit={(customer) => {
          setActionError(null)
          setEditing(customer)
          setFormMode('edit')
        }}
        onDisable={handleDisable}
        onEnable={handleEnable}
      />

      {meta && meta.pages > 1 && (
        <footer className="customer-pagination">
          <button type="button" disabled={page <= 1 || isLoading} onClick={() => setPage(page - 1)}>
            Trước
          </button>
          <span>
            Trang {meta.page} / {meta.pages} ({meta.total} khách)
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
