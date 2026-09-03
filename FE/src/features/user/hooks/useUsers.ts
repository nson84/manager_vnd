import { useCallback, useEffect, useState } from 'react'

import type { PaginatedResult } from '../../../types/api.types'
import { userService } from '../services/userService'
import type { ActiveFilter, UserResponse } from '../types/user.types'

export function useUsers(pageSize = 10) {
  const [page, setPage] = useState(1)
  const [activeFilter, setActiveFilter] = useState<ActiveFilter>('')
  const [data, setData] = useState<PaginatedResult<UserResponse> | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const fetchUsers = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const active = activeFilter === '' ? undefined : activeFilter === 'true'
      const response = await userService.getAll(page, pageSize, 'id,asc', active)
      setData(response.data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không tải được danh sách user')
    } finally {
      setIsLoading(false)
    }
  }, [page, pageSize, activeFilter])

  useEffect(() => {
    void fetchUsers()
  }, [fetchUsers])

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
    refetch: fetchUsers,
  }
}
