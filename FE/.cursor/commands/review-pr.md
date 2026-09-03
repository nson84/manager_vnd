Review the frontend code changes in this PR. Check every item below — flag violations clearly.

## Architecture
- [ ] Pages are thin — compose components, no business logic
- [ ] API calls only in `services/` layer
- [ ] Feature code stays inside `src/features/{name}/`
- [ ] Shared UI in `src/components/`, shared hooks in `src/hooks/`
- [ ] No circular imports between features

## TypeScript
- [ ] No `any` — use proper types or `unknown` with guards
- [ ] Request/response types match BE DTOs (see DATA_MODEL.md)
- [ ] Props interfaces defined for every component
- [ ] API responses typed as `ApiResponse<T>`

## Components
- [ ] Single responsibility — one component, one job
- [ ] Loading, error, and empty states handled
- [ ] No inline styles for complex layout — use CSS modules or co-located CSS
- [ ] Accessible: labels on inputs, buttons have type, alt on images

## State & Data
- [ ] Server state in hooks/services, not duplicated in global state
- [ ] Form state local unless truly shared
- [ ] No sensitive data (tokens, passwords) in component state longer than needed

## API Integration
- [ ] Uses shared `apiClient` with auth header injection
- [ ] Handles 401 → redirect to login
- [ ] Error messages shown to user (not silent failures)
- [ ] No hardcoded API URLs — use env or `/api` proxy

## Code Quality
- [ ] File < 300 lines
- [ ] Function < 50 lines
- [ ] Named exports for components (avoid default export abuse)
- [ ] No console.log left in production code

## Convention
- [ ] Naming follows PROJECT-RULES.md
- [ ] Feature folder structure matches template
- [ ] Imports ordered: external → internal → relative

## Documentation
- [ ] If new feature: CONTEXT.md created inside feature folder
- [ ] If logic changed: CONTEXT.md updated (add Refactor Log entry)
- [ ] If new API integration: docs/API_SPEC.md updated
- [ ] If new types: docs/DATA_MODEL.md updated

## Summary
After checking, provide:
1. **Blockers** — must fix before merge
2. **Suggestions** — improve but not blocking
3. **Good parts** — what was done well
