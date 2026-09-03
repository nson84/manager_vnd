import type { PermissionResponse } from '../../permission'

export interface RoleResponse {
  id: number
  name: string
  description?: string | null
  permissions: PermissionResponse[]
  createdAt: string
  updatedAt?: string | null
}

export interface CreateRoleRequest {
  name: string
  description?: string
  permissionIds: number[]
}

export interface UpdateRoleRequest {
  id: number
  name?: string
  description?: string
  permissionIds?: number[]
}

export interface RoleFormValues {
  name: string
  description: string
  permissionIds: number[]
}

export const SYSTEM_ROLE_NAMES = ['ADMIN', 'USER'] as const

export function isSystemRole(name: string): boolean {
  return SYSTEM_ROLE_NAMES.includes(name.toUpperCase() as (typeof SYSTEM_ROLE_NAMES)[number])
}
