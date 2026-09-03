import { useCallback, useEffect, useState } from 'react'

import type { PaginatedResult } from '../../../types/api.types'
import { debtService } from '../services/debtService'
import type { DebtEntryResponse } from '../types/debt.types'

export function useDebts(pageSize = 10) {
  const [page, setPage] = useState(1)
  const [data, setData] = useState<PaginatedResult<DebtEntryResponse> | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const fetchDebts = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const response = await debtService.getAll({ page, size: pageSize })
      setData(response.data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không tải được sổ công nợ')
    } finally {
      setIsLoading(false)
    }
  }, [page, pageSize])

  useEffect(() => {
    void fetchDebts()
  }, [fetchDebts])

  return {
    data,
    isLoading,
    error,
    page,
    setPage,
    refetch: fetchDebts,
  }
}
