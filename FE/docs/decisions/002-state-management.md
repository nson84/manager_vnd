# ADR-002: State Management — React Hooks + Context (No External Library)

## Status
Accepted

## Context

FE needs state management for:
- Auth state (access token, current user)
- Server data (users, companies, roles...)
- UI state (form inputs, modals, loading)

## Options Considered

### Option A: Redux Toolkit
- Industry standard, devtools, middleware
- **Rejected for now**: overkill for MVP, adds boilerplate and learning curve

### Option B: Zustand
- Lightweight, simple API
- **Deferred**: good option if hooks become complex, revisit in Phase 6

### Option C: TanStack Query (React Query)
- Excellent for server state (caching, refetch, pagination)
- **Deferred**: add when data fetching patterns stabilize

### Option D: Hooks + Context (CHOSEN)
- useState/useEffect hooks for data fetching
- AuthContext for global auth state
- Local state for forms and UI
- Zero additional dependencies

## Decision
**Option D** for MVP. Revisit Zustand or TanStack Query when:
- Multiple features share the same server data
- Cache invalidation becomes manual and error-prone
- Optimistic updates needed

## Implementation

```
Auth state     → AuthContext (global)
Server data    → custom hooks per feature (useUsers, useCompanies)
Form state     → useState in component or useForm hook
UI state       → useState (modal open, selected tab)
```

## Consequences

### Positive
- No extra dependencies
- Simple to understand and debug
- Easy to migrate to TanStack Query later (hooks are the abstraction layer)

### Negative
- No automatic cache/refetch — manual useEffect
- AuthContext re-renders all consumers on token change
- May need refactor when app grows beyond ~10 features

## Files Affected
- `src/features/auth/context/AuthContext.tsx`
- `src/features/*/hooks/use*.ts`
