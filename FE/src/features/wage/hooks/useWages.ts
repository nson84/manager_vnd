import { useCallback, useEffect, useState } from 'react'

import type { PaginatedResult } from '../../../types/api.types'
import { wageService } from '../services/wageService'
import type { WageEntryResponse } from '../types/wage.types'

export function useWages(pageSize = 10) {
  const [page, setPage] = useState(1)
  const [workerIdFilter, setWorkerIdFilter] = useState('')
  const [appliedWorkerId, setAppliedWorkerId] = useState('')
  const [unpaidOnly, setUnpaidOnly] = useState(false)
  const [data, setData] = useState<PaginatedResult<WageEntryResponse> | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const fetchWages = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const workerId = appliedWorkerId.trim() ? Number(appliedWorkerId) : undefined
      const response = await wageService.getAll({
        page,
        size: pageSize,
        workerId: Number.isFinite(workerId) ? workerId : undefined,
        unpaidOnly: unpaidOnly || undefined,
      })
      setData(response.data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không tải được danh sách ghi công')
    } finally {
      setIsLoading(false)
    }
  }, [page, pageSize, appliedWorkerId, unpaidOnly])

  useEffect(() => {
    void fetchWages()
  }, [fetchWages])

  return {
    data,
    isLoading,
    error,
    page,
    setPage,
    workerIdFilter,
    setWorkerIdFilter,
    unpaidOnly,
    setUnpaidOnly: (value: boolean) => {
      setPage(1)
      setUnpaidOnly(value)
    },
    applyFilters: () => {
      setPage(1)
      setAppliedWorkerId(workerIdFilter)
    },
    refetch: fetchWages,
  }
}
