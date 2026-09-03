# Architecture — Monorepo

> High-level system overview. Sub-project details in BE/docs and FE/docs.

---

## System Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    Browser (User)                        │
└────────────────────────┬────────────────────────────────┘
                         │ HTTPS
                         ▼
┌─────────────────────────────────────────────────────────┐
│  FE/  React SPA (localhost:5173)                         │
│  ├── Pages & Components                                  │
│  ├── AuthContext (access token in memory)                │
│  └── apiClient → /api/v1/*                               │
└────────────────────────┬────────────────────────────────┘
                         │ Vite proxy /api
                         ▼
┌─────────────────────────────────────────────────────────┐
│  BE/  Spring Boot (localhost:8080)                       │
│  ├── REST Controllers                                    │
│  ├── JWT Security (oauth2-resource-server)               │
│  ├── Service layer (Interface + Impl)                    │
│  └── JPA Repositories                                    │
└────────────────────────┬────────────────────────────────┘
                         │
                         ▼
                   ┌──────────┐
                   │  MySQL   │
                   └──────────┘
```

---

## Repository Layout

| Path | Purpose |
|------|---------|
| `BE/src/main/java/Manager_vnd/Manager/` | Java source |
| `BE/docs/` | BE architecture, API spec, database schema |
| `FE/src/features/` | Feature-based React modules |
| `FE/docs/` | FE architecture, data model, API integration |
| `docs/` | Monorepo-wide status and overview |
| `.cursor/commands/` | Cursor slash commands (monorepo) |

---

## Data Flow (CRUD Example)

```
User clicks "Save" on FE form
  → FE feature service (POST /api/v1/users)
  → BE UserController → UserService → UserRepository
  → MySQL
  → ApiResponse<UserResponse> back to FE
  → FE updates UI
```

Types must stay in sync:
- BE DTO records ↔ FE TypeScript interfaces
- See `BE/docs/DATABASE.md` and `FE/docs/DATA_MODEL.md`

---

## Auth Flow (Cross-stack)

See:
- BE: `BE/docs/decisions/001-refresh-token-strategy.md`
- FE: `FE/docs/decisions/001-auth-token-storage.md`

Access token: JSON body → FE memory
Refresh token: HttpOnly cookie → browser auto-sends

---

## Development Workflow

1. Implement BE endpoint first (+ test + update BE/docs/API_SPEC.md)
2. Add FE types (FE/docs/DATA_MODEL.md)
3. Add FE service + UI (FE/docs/API_SPEC.md)
4. Update monorepo `docs/PROJECT-STATUS.md`
