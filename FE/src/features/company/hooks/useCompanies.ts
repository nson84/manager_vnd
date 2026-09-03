import { useCallback, useEffect, useState } from 'react'

import type { PaginatedResult } from '../../../types/api.types'
import { companyService } from '../services/companyService'
import type { ActiveFilter, CompanyResponse } from '../types/company.types'

export function useCompanies(pageSize = 10) {
  const [page, setPage] = useState(1)
  const [activeFilter, setActiveFilter] = useState<ActiveFilter>('')
  const [data, setData] = useState<PaginatedResult<CompanyResponse> | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const fetchCompanies = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const active =
        activeFilter === '' ? undefined : activeFilter === 'true'
      const response = await companyService.getAll(page, pageSize, 'id,asc', active)
      setData(response.data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không tải được danh sách công ty')
    } finally {
      setIsLoading(false)
    }
  }, [page, pageSize, activeFilter])

  useEffect(() => {
    void fetchCompanies()
  }, [fetchCompanies])

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
    refetch: fetchCompanies,
  }
}
