export type CashDirection = 'IN' | 'OUT'

export type CashRefType =
  | 'EXPENSE'
  | 'PAYSLIP'
  | 'WORKER_ADVANCE'
  | 'CUSTOMER_PAYMENT'
  | 'MANUAL'

export interface CategorySummary {
  id: number
  code: string
  name: string
}

export interface CashUserSummary {
  id: number
  name: string
}

export interface CashEntryResponse {
  id: number
  entryDate: string
  direction: CashDirection
  amount: number
  category: CategorySummary | null
  description?: string | null
  note?: string | null
  checked: boolean
  checkedAt?: string | null
  checkedBy?: CashUserSummary | null
  refType: CashRefType
  refId?: number | null
  createdBy: CashUserSummary
  createdAt: string
  updatedAt?: string | null
}

export interface CreateManualCashEntryRequest {
  entryDate?: string
  direction: CashDirection
  amount: number
  categoryId: number
  description?: string
  note?: string
}

export interface CashStatsResponse {
  totalIn: number
  totalOut: number
  balance: number
  countIn: number
  countOut: number
  byCategory: Array<{
    categoryId: number
    categoryName: string
    direction: CashDirection
    total: number
  }>
}

export interface CashbookFilters {
  fromDate: string
  toDate: string
  direction: CashDirection | ''
  categoryId: string
  refType: CashRefType | ''
  checked: '' | 'true' | 'false'
  q: string
}

export function vietnamToday(): string {
  return new Intl.DateTimeFormat('en-CA', {
    timeZone: 'Asia/Ho_Chi_Minh',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(new Date())
}

export function vietnamFirstDayOfMonth(): string {
  const today = vietnamToday()
  return `${today.slice(0, 8)}01`
}

export function defaultCashbookFilters(): CashbookFilters {
  return {
    fromDate: vietnamFirstDayOfMonth(),
    toDate: vietnamToday(),
    direction: '',
    categoryId: '',
    refType: '',
    checked: '',
    q: '',
  }
}

export function formatVnd(amount: number): string {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(amount)
}

export function formatVnDateTime(iso?: string | null): string {
  if (!iso) return '—'
  return new Intl.DateTimeFormat('vi-VN', {
    timeZone: 'Asia/Ho_Chi_Minh',
    dateStyle: 'short',
    timeStyle: 'short',
  }).format(new Date(iso))
}
