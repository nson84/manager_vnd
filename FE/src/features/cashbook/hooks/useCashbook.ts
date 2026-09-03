import { useCallback, useEffect, useState } from 'react'

import type { PaginatedResult } from '../../../types/api.types'
import { cashbookService } from '../services/cashbookService'
import type {
  CashEntryResponse,
  CashStatsResponse,
  CashbookFilters,
  CategorySummary,
} from '../types/cashbook.types'
import { defaultCashbookFilters } from '../types/cashbook.types'

export function useCashbook(pageSize = 10) {
  const [filters, setFilters] = useState<CashbookFilters>(defaultCashbookFilters)
  const [applied, setApplied] = useState<CashbookFilters>(defaultCashbookFilters)
  const [page, setPage] = useState(1)
  const [data, setData] = useState<PaginatedResult<CashEntryResponse> | null>(null)
  const [stats, setStats] = useState<CashStatsResponse | null>(null)
  const [categories, setCategories] = useState<CategorySummary[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const fetchAll = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const [listRes, statsRes] = await Promise.all([
        cashbookService.getAll(applied, page, pageSize),
        cashbookService.getStats(applied),
      ])
      setData(listRes.data)
      setStats(statsRes.data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không tải được sổ quỹ')
    } finally {
      setIsLoading(false)
    }
  }, [applied, page, pageSize])

  useEffect(() => {
    void fetchAll()
  }, [fetchAll])

  useEffect(() => {
    cashbookService
      .listCategories()
      .then((res) => setCategories(res.data))
      .catch(() => setCategories([]))
  }, [])

  const applyFilters = () => {
    setPage(1)
    setApplied({ ...filters })
  }

  const resetFilters = () => {
    const next = defaultCashbookFilters()
    setFilters(next)
    setApplied(next)
    setPage(1)
  }

  return {
    filters,
    setFilters,
    applied,
    applyFilters,
    resetFilters,
    data,
    stats,
    categories,
    isLoading,
    error,
    page,
    setPage,
    refetch: fetchAll,
  }
}
