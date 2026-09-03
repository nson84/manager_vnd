# Project Status

> Last updated: 2026-09-02 | By: @team | Session: #0
>
> AI: update this file at the end of every session when asked.
> Follow this exact format. Keep it concise — under 80 lines.

---

## Completed
- ✅ Project skeleton (React 18 + TypeScript + Vite)
- ✅ Monorepo structure (BE + FE folders)
- ✅ Vite proxy `/api` → BE (localhost:8080)
- ✅ Documentation setup (AGENTS.md, PROJECT-RULES, ARCHITECTURE, DATA_MODEL, API_SPEC)
- ✅ ADR-001: Auth token storage decided (memory + HttpOnly cookie)
- ✅ ADR-002: State management decided (hooks + context, no external lib yet)
- ✅ AI workflow setup (.cursor/commands/)
- ✅ Source folder structure scaffolded

## In Progress
_Nothing yet — starting Phase 0._

## Deferred Issues
_None._

## Warnings
- ⚠️ React Router chưa cài — cần install trước Phase 1
- ⚠️ Vitest + Testing Library chưa cài — cần install trước khi viết test

## Next Tasks
1. **[P0]** Install React Router + setup `app/routes.tsx`
2. **[P0]** Create shared `apiClient` + `ApiResponse` types
3. **[P0]** Create auth context + auth service (login/logout/me)
4. **[P0]** Install Vitest + React Testing Library
5. **[P1]** Login page + form validation
6. **[P1]** Protected route wrapper component
7. **[P1]** App layout (header, sidebar, main content)

## Milestones

### Phase 0 — Foundation
- [ ] React Router setup
- [ ] Shared apiClient + ApiResponse types
- [ ] Auth context + authService
- [ ] Vitest + Testing Library setup
- [ ] App layout shell

### Phase 1 — Authentication UI
- [ ] Login page + form validation + test
- [ ] Register page + test
- [ ] Protected route wrapper
- [ ] Auth flow integration test (login → redirect → me)

### Phase 2 — User Management
- [ ] User list page (pagination) + test
- [ ] User detail page + test
- [ ] User create/edit form + test
- [ ] CONTEXT.md for user feature

### Phase 3 — Company Management
- [ ] Company CRUD pages + test
- [ ] CONTEXT.md for company feature

### Phase 4 — Role & Permission
- [ ] Role list + assign permissions UI
- [ ] Permission list page
- [ ] CONTEXT.md for role/permission features

### Phase 5 — File Upload
- [ ] File upload component (avatar/logo)
- [ ] Preview before save
- [ ] Integration with user/company forms

### Phase 6 — Polish
- [ ] Error boundary
- [ ] Toast notifications
- [ ] Loading skeletons
- [ ] Responsive mobile layout
- [ ] Full review (/review-pr) + final docs update
