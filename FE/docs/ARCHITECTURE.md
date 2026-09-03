# Architecture

> Frontend system design overview. Update only during architecture review sessions.

---

## High-Level Architecture

```
                         ┌─────────────────┐
                         │   Browser (SPA)  │
                         └────────┬─────────┘
                                  │
                         ┌────────▼─────────┐
                         │   React App       │
                         │   (Vite dev/build)│
                         └────────┬─────────┘
                                  │
              ┌───────────────────┼───────────────────┐
              ▼                   ▼                    ▼
     ┌────────────────┐  ┌────────────────┐  ┌────────────────┐
     │   Pages        │  │ Auth Context   │  │ Error Boundary │
     │   (Routes)     │  │ (Token state)  │  │ (Global catch) │
     └───────┬────────┘  └────────────────┘  └────────────────┘
             │
             ▼
     ┌────────────────┐
     │   Features     │
     │ (components +  │
     │  hooks + svc)  │
     └───────┬────────┘
             │
             ▼
     ┌────────────────┐
     │  apiClient     │
     │  (fetch + auth)│
     └───────┬────────┘
             │ /api/v1/*
             ▼
     ┌────────────────┐
     │  BE (Spring)   │
     │  localhost:8080│
     └────────────────┘
```

---

## Request Flow

### Standard Data Fetch
```
User action (click, page load)
  → Page component renders
  → Custom hook (useUsers) called
  → Hook calls feature service (userService.getAll)
  → Service calls apiClient('/users')
  → apiClient adds Authorization header
  → fetch → Vite proxy → BE
  → BE returns ApiResponse<T>
  → Service returns typed data
  → Hook updates state { data, isLoading, error }
  → Component re-renders with data
```

### Authentication Flow
```
1. Login:
   LoginPage → authService.login(email, password)
   → POST /api/v1/auth/login
   → BE returns accessToken + sets refresh_token cookie
   → AuthContext stores accessToken in memory
   → Redirect to dashboard

2. Authenticated Request:
   apiClient adds Authorization: Bearer {accessToken}
   → BE validates JWT → returns data

3. Token Refresh (on 401):
   apiClient catches 401
   → POST /api/v1/auth/refresh (cookie sent automatically)
   → BE returns new accessToken
   → Retry original request
   → If refresh fails → logout → redirect /login

4. Logout:
   authService.logout()
   → POST /api/v1/auth/logout
   → BE clears cookie + revokes token
   → AuthContext clears state
   → Redirect to /login
```

---

## Feature Module Structure

Each business feature is self-contained:

```
features/
├── auth/                    # Login, register, logout
├── user/                    # User CRUD UI
├── company/                 # Company CRUD UI
├── role/                    # Role management UI
└── permission/              # Permission management UI
```

### Feature Dependencies

```
auth        ← (no feature dependencies, uses shared apiClient)
     ↓
  user      ← auth (must be logged in)
  company   ← auth
  role      ← auth, permission (for assign UI)
  permission← auth
```

Dependency rules:
- `auth` is independent — only depends on shared services
- All other features depend on auth (protected routes)
- Features MUST NOT import each other's internals
- Shared types in `src/types/`, shared UI in `src/components/`

---

## Cross-Cutting Concerns

### Authentication
- Access token in React context (memory only)
- Refresh token in HttpOnly cookie (browser-managed)
- See ADR-001 in `docs/decisions/`

### State Management
- Server state: custom hooks + services (no Redux/Zustand yet)
- UI state: local component state or feature hooks
- Global state: AuthContext only (for now)
- See ADR-002 in `docs/decisions/`

### Error Handling
- Service layer throws `ApiError`
- Hooks expose `error` string
- Components show user-friendly messages
- Global ErrorBoundary catches unhandled React errors

### Routing
- React Router v6
- Public routes: `/login`, `/register`
- Protected routes: everything else (redirect to `/login` if unauthenticated)
- Lazy-loaded feature pages for code splitting

---

## Folder Mapping to BE

| FE Feature | BE Feature | BE Endpoints |
|-----------|-----------|-------------|
| auth | feature/auth | /auth/login, /auth/register, /auth/me, /auth/logout, /auth/refresh |
| user | feature/user | /users CRUD |
| company | feature/company | /companies CRUD |
| role | feature/role | /roles CRUD |
| permission | feature/permission | /permissions CRUD |
| file | feature/file | /files upload |

---

## Scalability Notes

### Current Design (MVP)
- Single SPA bundle (Vite)
- No SSR — client-side rendering only
- Feature-based code splitting via lazy routes
- Suitable for: admin dashboard, < 50 pages

### Future Considerations
- State management library (Zustand/TanStack Query) if hooks become complex
- i18n if multi-language needed
- PWA if offline support needed
- Component library (shadcn/ui) for consistent design system
