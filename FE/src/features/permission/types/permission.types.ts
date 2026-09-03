export interface PermissionResponse {
  id: number
  name: string
  apiPath: string
  method: string
  module: string
  createdAt: string
  updatedAt?: string | null
}

export interface CreatePermissionRequest {
  name: string
  apiPath: string
  method: string
  module: string
}

export interface UpdatePermissionRequest {
  id: number
  name?: string
  apiPath?: string
  method?: string
  module?: string
}

export interface PermissionFormValues {
  name: string
  apiPath: string
  method: string
  module: string
}

export const HTTP_METHODS = ['GET', 'POST', 'PUT', 'PATCH', 'DELETE'] as const
