# Project Status — Monorepo

> Last updated: 2026-09-03 | Session: #5

---

## Overview

| Part | Stack | Status | Detail |
|------|-------|--------|--------|
| **BE** | Spring Boot 4 + MySQL | Role/Permission CRUD done | `BE/docs/PROJECT-STATUS.md` |
| **FE** | React 18 + Vite | Role/Permission UI | `FE/docs/PROJECT-STATUS.md` |

---

## Completed (Monorepo)
- ✅ BE/FE skeleton, docs, proxy
- ✅ User / Company / Customer / Cashbook
- ✅ Worker, Wage, Debt, Expense, Payslip
- ✅ Auth JWT (login / refresh / RBAC roles)
- ✅ **Role + Permission CRUD** (BE + FE)

## In Progress
_Nothing._

## Warnings
- ⚠️ Permission catalog chưa filter HTTP (chỉ `hasRole` trên JWT)
- ⚠️ React Router / Vitest chưa cài

## Next Tasks
1. Permission request filter (apiPath + method)
2. React Router
3. User form: Role dropdown

## Milestones

- [x] Phase 0 — BE foundation
- [x] Phase 2 — Shop ledger entities APIs
- [x] Phase 1 — Auth
- [x] Phase 3 — Role + Permission CRUD
- [ ] Phase 3b — Fine-grained permission filter
