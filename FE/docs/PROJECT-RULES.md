# Project Rules — React 18 + TypeScript + Vite

> Coding conventions and best practices. Both AI and developer must follow.
> This is the single source of truth — all AI tools (Cursor, Copilot, Gemini) read this file.

---

## 0. Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | TypeScript 5.6 |
| Framework | React 18 |
| Build | Vite 6 |
| Routing | React Router (planned) |
| HTTP | fetch via shared `apiClient` |
| Test | Vitest + React Testing Library (planned) |
| Lint | ESLint |
| BE API | Spring Boot REST at `/api/v1` (proxied in dev) |

---

## 1. Folder Structure

```
src/
├── app/                         # App shell
│   ├── App.tsx                  # Root component
│   └── routes.tsx               # Route definitions
│
├── features/                    # Feature modules (business domains)
│   └── {feature}/
│       ├── components/          # Feature UI components
│       ├── hooks/               # Feature hooks
│       ├── services/            # Feature API calls
│       ├── types/               # Feature types
│       ├── pages/               # Route pages
│       ├── __tests__/           # Feature tests
│       ├── CONTEXT.md           # Implementation context
│       └── index.ts             # Public exports
│
├── components/                  # Shared UI (Button, Input, Modal...)
│   └── ui/
│
├── hooks/                       # Shared hooks (useAuth, useDebounce...)
│
├── services/                    # Shared services
│   ├── apiClient.ts             # HTTP client with auth + error handling
│   └── authService.ts           # Login, logout, token management
│
├── types/                       # Shared types
│   └── api.types.ts             # ApiResponse<T>, PaginationMeta...
│
├── utils/                       # Pure utility functions
│
├── assets/                      # Static assets (images, icons)
│
├── main.tsx                     # Entry point
└── index.css                    # Global styles
```

### Folder Rules
- 1 feature = 1 folder under `features/` with components, hooks, services, types, pages
- Feature code MUST NOT import from another feature's internals — use public `index.ts` exports
- Shared UI goes in `components/`, shared logic in `hooks/` or `services/`
- Pages are thin wrappers — compose feature components, no business logic
- Test files live in `__tests__/` inside each feature folder

---

## 2. Naming Convention

### Files
| Type | Pattern | Example |
|------|---------|---------|
| Component | `PascalCase.tsx` | `UserList.tsx`, `LoginForm.tsx` |
| Hook | `use{Name}.ts` | `useUsers.ts`, `useAuth.ts` |
| Service | `{name}Service.ts` | `userService.ts`, `authService.ts` |
| Types | `{name}.types.ts` | `user.types.ts`, `api.types.ts` |
| Page | `{Name}Page.tsx` | `UsersPage.tsx`, `LoginPage.tsx` |
| Test | `{name}.test.ts(x)` | `userService.test.ts`, `UsersPage.test.tsx` |
| CSS | `{Component}.module.css` | `UserList.module.css` |

### Variables & Functions
- `camelCase` for variables, functions, hooks
- `PascalCase` for components, types, interfaces
- `UPPER_SNAKE_CASE` for constants
- Boolean prefix: `is/has/can` — `isLoading`, `hasError`, `canSubmit`
- Event handlers: `handle{Action}` — `handleSubmit`, `handleDelete`

---

## 3. Component Rules

```tsx
interface UserListProps {
  users: UserResponse[]
  isLoading: boolean
  onSelect: (id: number) => void
}

export function UserList({ users, isLoading, onSelect }: UserListProps) {
  if (isLoading) return <Spinner />
  if (users.length === 0) return <EmptyState message="No users found" />

  return (
    <ul>
      {users.map((user) => (
        <li key={user.id} onClick={() => onSelect(user.id)}>
          {user.name}
        </li>
      ))}
    </ul>
  )
}
```

### Rules
- Named exports (not default) for components
- Props interface defined above component
- Handle loading, error, and empty states explicitly
- No API calls inside components — use hooks
- Keep components focused — split if > 150 lines
- Prefer composition over prop drilling (max 2-3 levels)

---

## 4. Service Layer (API Calls)

```typescript
// services/apiClient.ts
const API_BASE = '/api/v1'

export async function apiClient<T>(
  path: string,
  options?: RequestInit,
): Promise<ApiResponse<T>> {
  const token = authService.getAccessToken()
  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }),
      ...options?.headers,
    },
  })

  if (!response.ok) {
    throw new ApiError(response.status, await response.json())
  }

  return response.json()
}
```

```typescript
// features/user/services/userService.ts
export const userService = {
  getAll: (page = 1, size = 10) =>
    apiClient<PaginatedResult<UserResponse>>(`/users?page=${page}&size=${size}`),

  getById: (id: number) =>
    apiClient<UserResponse>(`/users/${id}`),

  create: (data: CreateUserRequest) =>
    apiClient<UserResponse>('/users', { method: 'POST', body: JSON.stringify(data) }),
}
```

### Rules
- ALL HTTP calls go through `apiClient` — never raw `fetch` in components
- Feature services wrap `apiClient` with typed functions
- Request/response types match BE DTOs (see `docs/DATA_MODEL.md`)
- Handle auth token injection centrally in `apiClient`
- No hardcoded URLs — use `/api/v1` prefix (Vite proxy handles dev routing)

---

## 5. ApiResponse Type

```typescript
// types/api.types.ts
export interface ApiResponse<T> {
  statusCode: number
  data: T
  message: string
  timestamp: string
}

export interface PaginatedResult<T> {
  meta: PaginationMeta
  result: T[]
}

export interface PaginationMeta {
  page: number
  pageSize: number
  pages: number
  total: number
}
```

Must match BE `ApiResponse<T>` wrapper exactly.

---

## 6. Hooks

```typescript
export function useUsers(page = 1, size = 10) {
  const [data, setData] = useState<PaginatedResult<UserResponse> | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    setIsLoading(true)
    userService.getAll(page, size)
      .then((res) => setData(res.data))
      .catch((err) => setError(err.message))
      .finally(() => setIsLoading(false))
  }, [page, size])

  return { data, isLoading, error }
}
```

### Rules
- Custom hooks start with `use`
- Encapsulate data fetching, form state, mutations
- Return `{ data, isLoading, error }` consistently
- Hooks call services, not `fetch` directly

---

## 7. Routing

```tsx
// app/routes.tsx
export const routes = [
  { path: '/login', element: <LoginPage />, public: true },
  { path: '/users', element: <UsersPage /> },
  { path: '/users/:id', element: <UserDetailPage /> },
]
```

### Rules
- Route definitions centralized in `app/routes.tsx`
- Protected routes check auth before rendering
- Public routes: `/login`, `/register`
- Use lazy loading for feature pages: `React.lazy(() => import(...))`

---

## 8. Authentication (FE side)

Per BE ADR-001 (see `../BE/docs/decisions/001-refresh-token-strategy.md`):

| Token | FE Storage | Notes |
|-------|-----------|-------|
| Access token | Memory (React state/context) | Short-lived (15 min) |
| Refresh token | HttpOnly cookie (auto) | Browser manages — JS cannot read |

### Rules
- NEVER store access token in localStorage
- NEVER store refresh token in localStorage
- On 401 response → attempt refresh → if fail, redirect to `/login`
- Clear auth state on logout, call BE `/auth/logout`

---

## 9. Error Handling

```typescript
export class ApiError extends Error {
  constructor(
    public status: number,
    public body: ApiResponse<unknown>,
  ) {
    super(body.message)
  }
}
```

### Rules
- Service layer throws `ApiError` on non-2xx responses
- Hooks catch errors and expose `error` string to components
- Components display user-friendly error messages
- Never show raw stack traces or internal errors to user
- Log errors to console only in development

---

## 10. Styling

### Rules
- Co-located CSS modules: `Component.module.css`
- Global styles only in `index.css` (reset, typography, CSS variables)
- Use CSS variables for theme colors, spacing, fonts
- No inline styles for layout (ok for one-off dynamic values)
- Mobile-first responsive design

---

## 11. Environment Variables

```env
# .env.development
VITE_API_BASE=/api/v1

# .env.production
VITE_API_BASE=https://api.example.com/api/v1
```

### Rules
- All env vars prefixed with `VITE_`
- Never commit `.env.local` with secrets
- Access via `import.meta.env.VITE_*`

---

## 12. Testing

```typescript
// features/user/__tests__/userService.test.ts
describe('userService', () => {
  it('getAll_returnsUsers_whenApiSucceeds', async () => {
    vi.spyOn(global, 'fetch').mockResolvedValueOnce({
      ok: true,
      json: async () => ({ statusCode: 200, data: { meta: {}, result: [] }, message: 'OK' }),
    } as Response)

    const result = await userService.getAll()
    expect(result.data.result).toEqual([])
  })
})
```

### Rules
- Test naming: `functionName_scenario_expectedResult`
- Service tests: mock `fetch`, test parsing and error handling
- Component tests: React Testing Library, test user-visible behavior
- No snapshot tests for entire pages — test specific behaviors
- Run: `npm test` (Vitest)

---

## 13. Code Size Limits

| Metric | Limit | Action if exceeded |
|--------|-------|-------------------|
| File | < 300 lines | Split into smaller files |
| Function | < 50 lines | Extract helper/hook |
| Component props | < 8 | Group into object or split component |
| Nesting (JSX) | < 4 levels | Extract sub-component |

---

## 14. Commit Checklist

- [ ] No `any` types — strict TypeScript
- [ ] API calls only in services, not components
- [ ] Loading/error/empty states handled
- [ ] Types match BE DTOs
- [ ] No hardcoded API URLs
- [ ] No sensitive data in localStorage
- [ ] Tests cover happy path + error cases
- [ ] File < 300 lines, function < 50 lines
- [ ] `CONTEXT.md` updated if important logic changed
- [ ] `PROJECT-STATUS.md` updated

---

## 15. Documentation Requirements

| When | Action |
|------|--------|
| End of every coding session | Update `docs/PROJECT-STATUS.md` |
| New feature with non-obvious logic | Create `CONTEXT.md` inside feature folder |
| Architecture decision | Create new file in `docs/decisions/` |
| New API integration | Update `docs/API_SPEC.md` |
| New types | Update `docs/DATA_MODEL.md` |
