export type DebtEntryType = 'CHARGE' | 'PAYMENT' | 'ADJUST'
export type LedgerDirection = 'INCREASE' | 'DECREASE'
export type DebtRefType = string
export type PartyType = 'customer' | 'worker'

export interface DebtEntryResponse {
  id: number
  customerId?: number | null
  customerName?: string | null
  workerId?: number | null
  workerName?: string | null
  entryType: DebtEntryType
  direction: LedgerDirection
  amount: number
  entryDate: string
  note?: string | null
  refType?: DebtRefType | null
  refId?: number | null
  createdById: number
  createdAt: string
}

export interface CreateDebtEntryRequest {
  customerId?: number
  workerId?: number
  entryType: DebtEntryType
  direction?: LedgerDirection
  amount: number
  entryDate: string
  note?: string
}

export interface DebtFormValues {
  partyType: PartyType
  partyId: string
  entryType: DebtEntryType
  direction: LedgerDirection | ''
  amount: string
  entryDate: string
  note: string
}

export const ENTRY_TYPE_LABELS: Record<DebtEntryType, string> = {
  CHARGE: 'Ghi nợ',
  PAYMENT: 'Thanh toán',
  ADJUST: 'Điều chỉnh',
}

export const DIRECTION_LABELS: Record<LedgerDirection, string> = {
  INCREASE: 'Tăng',
  DECREASE: 'Giảm',
}

export function formatVnd(amount: number): string {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(amount)
}
