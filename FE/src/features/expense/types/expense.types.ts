export type ExpenseStatus = 'POSTED' | 'CANCELLED'

export interface ExpenseResponse {
  id: number
  categoryId: number
  categoryCode: string
  categoryName: string
  amount: number
  expenseDate: string
  note?: string | null
  status: ExpenseStatus
  createdById: number
  createdAt: string
  updatedAt?: string | null
}

export interface CreateExpenseRequest {
  categoryId: number
  amount: number
  expenseDate: string
  note?: string
}

export interface ExpenseFormValues {
  categoryId: string
  amount: string
  expenseDate: string
  note: string
}

export const EXPENSE_STATUS_LABELS: Record<ExpenseStatus, string> = {
  POSTED: 'Đã ghi',
  CANCELLED: 'Đã hủy',
}

export function formatVnd(amount: number): string {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(amount)
}
