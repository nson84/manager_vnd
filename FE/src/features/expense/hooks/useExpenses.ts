import { useCallback, useEffect, useState } from 'react'

import type { PaginatedResult } from '../../../types/api.types'
import { expenseService } from '../services/expenseService'
import type { ExpenseResponse } from '../types/expense.types'

export function useExpenses(pageSize = 10) {
  const [page, setPage] = useState(1)
  const [data, setData] = useState<PaginatedResult<ExpenseResponse> | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const fetchExpenses = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const response = await expenseService.getAll(page, pageSize)
      setData(response.data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không tải được phiếu chi')
    } finally {
      setIsLoading(false)
    }
  }, [page, pageSize])

  useEffect(() => {
    void fetchExpenses()
  }, [fetchExpenses])

  return {
    data,
    isLoading,
    error,
    page,
    setPage,
    refetch: fetchExpenses,
  }
}
