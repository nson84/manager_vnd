export interface CompanyResponse {
  id: number
  name: string
  description?: string | null
  address?: string | null
  logo?: string | null
  active: boolean
  createdAt: string
  updatedAt?: string | null
}

export interface CreateCompanyRequest {
  name: string
  description?: string
  address?: string
  logo?: string
}

export interface UpdateCompanyRequest {
  id: number
  name?: string
  description?: string
  address?: string
  logo?: string
}

export interface CompanyFormValues {
  name: string
  description: string
  address: string
  logo: string
}

export type ActiveFilter = '' | 'true' | 'false'
