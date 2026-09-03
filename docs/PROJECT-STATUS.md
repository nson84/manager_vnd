# Project Status — Monorepo

> Last updated: 2026-09-02 | Session: #1
>
> AI: update this file AND the relevant sub-project status at end of session.

---

## Overview

| Part | Stack | Status | Detail |
|------|-------|--------|--------|
| **BE** | Spring Boot 4 + MySQL | Phase 0 | `BE/docs/PROJECT-STATUS.md` |
| **FE** | React 18 + Vite | Phase 0 | `FE/docs/PROJECT-STATUS.md` |

---

## Completed (Monorepo)
- ✅ Split project into `BE/` + `FE/` folders
- ✅ BE: Spring Boot skeleton + AI workflow (docs, .cursor/commands)
- ✅ FE: React + Vite skeleton + AI workflow (docs, .cursor/commands)
- ✅ Root monorepo config (AGENTS.md, .cursor/commands)
- ✅ Vite proxy `/api` → BE
- ✅ BE DB plan: công nợ + công thợ + lương + sổ quỹ (`BE/docs/DATABASE.md`, ADR-003)

## In Progress
_Nothing — both sub-projects at Phase 0 foundation._

## Warnings
- ⚠️ BE: MySQL chưa cấu hình trong `application.properties`
- ⚠️ FE: React Router + Vitest chưa cài

## Next Tasks (Recommended Order)

### Backend first (FE depends on API)
1. **[P0] BE** Foundation — exceptions, ApiResponse, SecurityConfig, JwtConfig
2. **[P1] BE** Permission + Company CRUD
3. **[P1] BE** Role → User → Auth flow

### Frontend (after BE endpoints exist)
4. **[P0] FE** React Router + apiClient + AuthContext
5. **[P1] FE** Login/Register pages
6. **[P1] FE** User + Company management UI

## Milestones

- [ ] Phase 0 — BE + FE foundation
- [ ] Phase 1 — Auth (BE + FE)
- [ ] Phase 2 — User + Company CRUD (BE + FE)
- [ ] Phase 3 — Role + Permission + RBAC
- [ ] Phase 4 — File upload (avatar/logo)
- [ ] Phase 5 — Polish + full review
