export type WageType = 'DAILY' | 'HOURLY' | 'PIECE'

export type ActiveFilter = '' | 'true' | 'false'

export interface WorkerResponse {
  id: number
  name: string
  phone?: string | null
  address?: string | null
  jobTitle?: string | null
  wageType: WageType
  defaultUnitRate: number
  hireDate?: string | null
  active: boolean
  note?: string | null
  currentAdvance: number
  createdAt: string
  updatedAt?: string | null
}

export interface CreateWorkerRequest {
  name: string
  phone?: string
  address?: string
  jobTitle?: string
  wageType: WageType
  defaultUnitRate: number
  hireDate?: string
  note?: string
}

export interface UpdateWorkerRequest {
  id: number
  name?: string
  phone?: string
  address?: string
  jobTitle?: string
  wageType?: WageType
  defaultUnitRate?: number
  hireDate?: string
  note?: string
}

export interface WorkerFormValues {
  name: string
  phone: string
  address: string
  jobTitle: string
  wageType: WageType
  defaultUnitRate: string
  hireDate: string
  note: string
}

export const WAGE_TYPE_LABELS: Record<WageType, string> = {
  DAILY: 'Theo ngày',
  HOURLY: 'Theo giờ',
  PIECE: 'Theo sản phẩm',
}

export function formatVnd(amount: number): string {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(amount)
}
