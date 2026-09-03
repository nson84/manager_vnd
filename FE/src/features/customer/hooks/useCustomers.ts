import { useCallback, useEffect, useState } from 'react'

import type { PaginatedResult } from '../../../types/api.types'
import { customerService } from '../services/customerService'
import type { ActiveFilter, CustomerResponse } from '../types/customer.types'

export function useCustomers(pageSize = 10) {
  const [page, setPage] = useState(1)
  const [activeFilter, setActiveFilter] = useState<ActiveFilter>('')
  const [q, setQ] = useState('')
  const [appliedQ, setAppliedQ] = useState('')
  const [data, setData] = useState<PaginatedResult<CustomerResponse> | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const fetchCustomers = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const active = activeFilter === '' ? undefined : activeFilter === 'true'
      const response = await customerService.getAll(page, pageSize, 'id,asc', active, appliedQ)
      setData(response.data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không tải được danh sách khách hàng')
    } finally {
      setIsLoading(false)
    }
  }, [page, pageSize, activeFilter, appliedQ])

  useEffect(() => {
    void fetchCustomers()
  }, [fetchCustomers])

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
    refetch: fetchCustomers,
  }
}
