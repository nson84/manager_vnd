import { useCallback, useEffect, useState } from 'react'

import type { PaginatedResult } from '../../../types/api.types'
import { payslipService } from '../services/payslipService'
import type { PayslipResponse } from '../types/payslip.types'

export function usePayslips(pageSize = 10) {
  const [page, setPage] = useState(1)
  const [data, setData] = useState<PaginatedResult<PayslipResponse> | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const fetchPayslips = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const response = await payslipService.getAll(page, pageSize)
      setData(response.data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không tải được phiếu lương')
    } finally {
      setIsLoading(false)
    }
  }, [page, pageSize])

  useEffect(() => {
    void fetchPayslips()
  }, [fetchPayslips])

  return {
    data,
    isLoading,
    error,
    page,
    setPage,
    refetch: fetchPayslips,
  }
}
