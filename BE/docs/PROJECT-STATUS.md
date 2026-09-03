# Project Status

> Last updated: 2026-09-03 | By: @team | Session: #5
>
> AI: update this file at the end of every session when asked.
> Follow this exact format. Keep it concise — under 80 lines.

---

## Completed
- ✅ Project skeleton + monorepo + docs + ADR-001/002/003 + JPA entities
- ✅ Foundation: ApiResponse, GlobalExceptionHandler, JWT SecurityConfig
- ✅ User / Company / Customer / Cashbook
- ✅ Worker / Wage / Debt / Expense / Payslip (ADR ledger + unit tests)
- ✅ ActorResolver + CashLedgerWriter
- ✅ **Permission + Role CRUD** (ADMIN, system roles ADMIN/USER protected)

## In Progress
_Nothing._

## Deferred Issues
- Integration MockMvc tests — cần application-test.yml
- Fine-grained permission filter (apiPath + method) — JWT still uses `hasRole`

## Warnings
- ⚠️ Permission rows do not yet gate HTTP; only Role names in JWT

## Next Tasks
1. **[P1]** Permission request filter (match method + apiPath)
2. **[P2]** FE polish / React Router

## Milestones

### Phase 0 — Foundation
- [x] Exceptions + ApiResponse + SecurityConfig JWT

### Phase 1 — Independent Entities
- [x] Company / Customer / Worker CRUD (soft disable)
- [x] Permission CRUD
- [x] Role CRUD

### Shop ledger
- [x] Cashbook
- [x] Debt / Wage / Expense / Payslip APIs
