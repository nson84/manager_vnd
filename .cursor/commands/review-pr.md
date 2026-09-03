Review code changes in this monorepo (BE and/or FE).

## Backend (if changed)
Follow checklist in `BE/.cursor/commands/review-pr.md`.

## Frontend (if changed)
Follow checklist in `FE/.cursor/commands/review-pr.md`.

## Cross-stack (if API/types changed)
- [ ] BE DTO fields match FE TypeScript types in `FE/docs/DATA_MODEL.md`
- [ ] FE service URLs match BE endpoints in `BE/docs/API_SPEC.md`
- [ ] Auth flow consistent (BE ADR-001 ↔ FE ADR-001)

## Summary
Provide blockers, suggestions, and good parts for each changed part (BE / FE).
