export interface CustomerResponse {
  id: number
  name: string
  phone?: string | null
  address?: string | null
  note?: string | null
  active: boolean
  currentDebt: number
  createdAt: string
  updatedAt?: string | null
}

export interface CreateCustomerRequest {
  name: string
  phone?: string
  address?: string
  note?: string
}

export interface UpdateCustomerRequest {
  id: number
  name?: string
  phone?: string
  address?: string
  note?: string
}

export interface CustomerFormValues {
  name: string
  phone: string
  address: string
  note: string
}

export type ActiveFilter = '' | 'true' | 'false'

export function formatVnd(amount: number): string {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(amount)
}
