# Project: Manager System (Monorepo)

Full-stack HR Management System — Spring Boot REST API + React SPA.

## Structure

```
Manager/
├── BE/     # Backend  — Spring Boot 4, JPA, MySQL, JWT
└── FE/     # Frontend — React 18, TypeScript, Vite
```

## Read Before Coding

### Always read first
- `docs/PROJECT-STATUS.md` — overall progress (BE + FE)
- Sub-project entry point based on task:
  - **Backend work** → `BE/AGENTS.md` + `BE/docs/PROJECT-RULES.md`
  - **Frontend work** → `FE/AGENTS.md` + `FE/docs/PROJECT-RULES.md`

### Cross-stack tasks (read both)
- `BE/docs/API_SPEC.md` + `FE/docs/API_SPEC.md` — API contract
- `BE/docs/DATABASE.md` + `FE/docs/DATA_MODEL.md` — data types must match

### Module context
- BE: `BE/src/.../feature/{name}/CONTEXT.md`
- FE: `FE/src/features/{name}/CONTEXT.md`

## Dev URLs

| Service | URL |
|---------|-----|
| Backend | http://localhost:8080 |
| Frontend | http://localhost:5173 |
| API proxy | FE `/api` → BE `:8080` |

## Slash Commands

| Command | Scope |
|---------|-------|
| `/start` | Read monorepo context |
| `/new-feature-be` | New BE feature module |
| `/new-feature-fe` | New FE feature module |
| `/write-tests-be` | BE tests |
| `/write-tests-fe` | FE tests |
| `/review-pr` | Review changes (BE + FE) |
| `/update-status` | Update PROJECT-STATUS docs |

Detailed workflows:
- BE: `BE/docs/Quy Trình Vibe Coding Với AI.md`
- FE: `FE/docs/Quy Trình Vibe Coding Với AI.md`
