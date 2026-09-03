export type WageType = 'DAILY' | 'HOURLY' | 'PIECE'

export interface WageEntryResponse {
  id: number
  workerId: number
  workerName: string
  workDate: string
  wageType: WageType
  quantity: number
  unitRate: number
  amount: number
  note?: string | null
  payslipId?: number | null
  createdById: number
  createdAt: string
  updatedAt?: string | null
}

export interface CreateWageEntryRequest {
  workerId: number
  workDate: string
  wageType?: WageType
  quantity: number
  unitRate?: number
  note?: string
}

export interface UpdateWageEntryRequest {
  id: number
  workDate?: string
  wageType?: WageType
  quantity?: number
  unitRate?: number
  note?: string
}

export interface WageFormValues {
  workerId: string
  workDate: string
  wageType: WageType | ''
  quantity: string
  unitRate: string
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
