export type PayslipStatus = 'DRAFT' | 'CONFIRMED' | 'PAID' | 'CANCELLED'

export interface PayslipResponse {
  id: number
  workerId: number
  workerName: string
  periodStart: string
  periodEnd: string
  grossAmount: number
  advanceDeducted: number
  otherDeduction: number
  netAmount: number
  status: PayslipStatus
  paidAt?: string | null
  note?: string | null
  createdById: number
  createdAt: string
  updatedAt?: string | null
}

export interface CreatePayslipRequest {
  workerId: number
  periodStart: string
  periodEnd: string
  advanceDeducted?: number
  otherDeduction?: number
  note?: string
}

export interface PayslipFormValues {
  workerId: string
  periodStart: string
  periodEnd: string
  advanceDeducted: string
  otherDeduction: string
  note: string
}

export const PAYSLIP_STATUS_LABELS: Record<PayslipStatus, string> = {
  DRAFT: 'Nháp',
  CONFIRMED: 'Đã xác nhận',
  PAID: 'Đã trả',
  CANCELLED: 'Đã hủy',
}

export function formatVnd(amount: number): string {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(amount)
}
