export interface ApiResponse<T> {
  statusCode: number
  data: T
  message: string
  timestamp: string
}

export interface PaginationMeta {
  page: number
  pageSize: number
  pages: number
  total: number
}

export interface PaginatedResult<T> {
  meta: PaginationMeta
  result: T[]
}

export class ApiError extends Error {
  constructor(
    public status: number,
    public body: ApiResponse<unknown>,
  ) {
    super(body.message)
    this.name = 'ApiError'
  }
}
