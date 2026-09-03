import { useCallback, useEffect, useState } from 'react'

import type { PaginatedResult } from '../../../types/api.types'
import { workerService } from '../services/workerService'
import type { ActiveFilter, WorkerResponse } from '../types/worker.types'

export function useWorkers(pageSize = 10) {
  const [page, setPage] = useState(1)
  const [activeFilter, setActiveFilter] = useState<ActiveFilter>('')
  const [q, setQ] = useState('')
  const [appliedQ, setAppliedQ] = useState('')
  const [data, setData] = useState<PaginatedResult<WorkerResponse> | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const fetchWorkers = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const active = activeFilter === '' ? undefined : activeFilter === 'true'
      const response = await workerService.getAll(page, pageSize, 'id,asc', active, appliedQ)
      setData(response.data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không tải được danh sách thợ')
    } finally {
      setIsLoading(false)
    }
  }, [page, pageSize, activeFilter, appliedQ])

  useEffect(() => {
    void fetchWorkers()
  }, [fetchWorkers])

  return {
    data,
    isLoading,
    error,
    page,
    setPage,
    activeFilter,
    setActiveFilter: (value: ActiveFilter) => {
      setPage(1)
      setActiveFilter(value)
    },
    q,
    setQ,
    applySearch: () => {
      setPage(1)
      setAppliedQ(q)
    },
    refetch: fetchWorkers,
  }
}
