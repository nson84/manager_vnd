import { useCallback, useEffect, useState } from 'react'

import type { PaginatedResult } from '../../../types/api.types'
import { permissionService } from '../services/permissionService'
import type { PermissionResponse } from '../types/permission.types'

export function usePermissions(pageSize = 10) {
  const [page, setPage] = useState(1)
  const [data, setData] = useState<PaginatedResult<PermissionResponse> | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const fetchPermissions = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const response = await permissionService.getAll(page, pageSize, 'id,asc')
      setData(response.data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không tải được danh sách permission')
    } finally {
      setIsLoading(false)
    }
  }, [page, pageSize])

  useEffect(() => {
    void fetchPermissions()
  }, [fetchPermissions])

  return { data, isLoading, error, page, setPage, refetch: fetchPermissions }
}
