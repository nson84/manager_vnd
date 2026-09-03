import { useCallback, useEffect, useState } from 'react'

import { permissionService } from '../../permission'
import type { PermissionResponse } from '../../permission'

export function usePermissionCatalog() {
  const [permissions, setPermissions] = useState<PermissionResponse[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const fetchCatalog = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const response = await permissionService.getAll(1, 200, 'module,asc')
      setPermissions(response.data?.result ?? [])
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không tải được permission')
    } finally {
      setIsLoading(false)
    }
  }, [])

  useEffect(() => {
    void fetchCatalog()
  }, [fetchCatalog])

  return { permissions, isLoading, error, refetch: fetchCatalog }
}
