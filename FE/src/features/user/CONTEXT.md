# User Feature — Frontend Context

> Module: `src/features/user/` | Phase 2 (partial)

## Scope

User CRUD only — no Auth, Company, or Role UI.

## Structure

```
features/user/
├── components/   UserList, UserForm, users.css
├── hooks/        useUsers
├── services/     userService
├── types/        user.types.ts
├── pages/        UsersPage.tsx
└── index.ts
```

## API

All calls via `userService` → `apiClient` → `/api/v1/users`.

| Function | BE endpoint |
|----------|-------------|
| `getAll` | GET `/users?page&size&sort` |
| `getById` | GET `/users/{id}` |
| `create` | POST `/users` |
| `update` | PUT `/users` |
| `delete` | DELETE `/users/{id}` |

## UI Notes

- `UsersPage` mounted directly in `App.tsx` (React Router chưa cài)
- `companyId` / `roleIds`: input số thủ công, chưa fetch dropdown từ Company/Role API
- Email disabled khi edit (BE không cho đổi email qua PUT)

## Tests

Deferred — Vitest chưa cài.

## Related

- BE: `BE/docs/API_SPEC.md` — Users section
- Types: `FE/docs/DATA_MODEL.md` — User section
