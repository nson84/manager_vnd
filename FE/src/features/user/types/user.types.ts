export type Gender = 'MALE' | 'FEMALE' | 'OTHER'

export interface CompanySummary {
  id: number
  name: string
}

export interface RoleSummary {
  id: number
  name: string
}

export interface UserResponse {
  id: number
  name: string
  email: string
  age?: number
  gender?: Gender
  address?: string
  avatar?: string | null
  active: boolean
  company?: CompanySummary | null
  roles: RoleSummary[]
  createdAt: string
  updatedAt?: string | null
}

export interface CreateUserRequest {
  name: string
  email: string
  password: string
  age?: number
  gender?: Gender
  address?: string
  companyId?: number
  roleIds?: number[]
}

export interface UpdateUserRequest {
  id: number
  name?: string
  age?: number
  gender?: Gender
  address?: string
  avatar?: string
  companyId?: number
  roleIds?: number[]
}

export interface UserFormValues {
  name: string
  email: string
  password: string
  age: string
  gender: Gender | ''
  address: string
  companyId: string
  roleIds: string
}

export type ActiveFilter = '' | 'true' | 'false'
