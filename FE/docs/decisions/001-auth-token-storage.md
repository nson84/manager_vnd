# ADR-001: Auth Token Storage — Memory (Access) + HttpOnly Cookie (Refresh)

## Status
Accepted

## Context

FE must store auth tokens securely. BE ADR-001 defines dual-source refresh token
(cookie for SPA, body for mobile). FE is a browser SPA.

## Options Considered

### Option A: localStorage for both tokens
- Simple to implement
- **Rejected**: XSS can steal tokens from localStorage

### Option B: Memory + HttpOnly Cookie (CHOSEN)
- Access token in React context (memory) — lost on tab close, XSS cannot persist
- Refresh token in HttpOnly cookie — set by BE, JS cannot read
- Matches BE ADR-001 SPA strategy

### Option C: sessionStorage
- Survives page refresh but not tab close
- Still vulnerable to XSS — rejected

## Decision
**Option B** — Access token in memory, refresh token in HttpOnly cookie.

## Implementation

```typescript
// AuthContext holds accessToken in useState
// apiClient reads token from context for Authorization header
// On 401: apiClient calls POST /auth/refresh (cookie auto-sent)
// On refresh success: update context with new accessToken
// On refresh fail: clear context, redirect to /login
```

## Consequences

### Positive
- XSS cannot steal refresh token
- Access token not persisted — auto-logout on tab close
- Aligns with BE cookie strategy

### Negative
- User must re-login after closing browser tab
- Cannot share auth state across tabs (unless refresh cookie works cross-tab)

## Files Affected
- `src/features/auth/context/AuthContext.tsx`
- `src/features/auth/services/authService.ts`
- `src/services/apiClient.ts`
