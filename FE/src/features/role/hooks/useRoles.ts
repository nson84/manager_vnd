import { useCallback, useEffect, useState } from 'react'

import type { PaginatedResult } from '../../../types/api.types'
import { roleService } from '../services/roleService'
import type { RoleResponse } from '../types/role.types'

export function useRoles(pageSize = 10) {
  const [page, setPage] = useState(1)
  const [data, setData] = useState<PaginatedResult<RoleResponse> | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const fetchRoles = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const response = await roleService.getAll(page, pageSize, 'id,asc')
      setData(response.data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không tải được danh sách role')
    } finally {
      setIsLoading(false)
    }
  }, [page, pageSize])

  useEffect(() => {
    void fetchRoles()
  }, [fetchRoles])

  return { data, isLoading, error, page, setPage, refetch: fetchRoles }
}
