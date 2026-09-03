# Permission — Frontend Context
> Written: 2026-09-03 | Author: @team

## Business Context
Admin CRUD for API permissions. Used when assigning permissions to a Role.

## Technical Decisions
- **Reuse Company CSS**: same list+form chrome; no new visual system.
- **Hard delete** in UI: matches BE, confirm dialog only.

## Considered and Rejected
- **Permission filter on every request in FE**: rejected — BE still authorizes by role.

## Dependencies
- Depends on: `apiClient`, auth session
- Depended by: `role` (catalog for checkboxes)
- BE endpoints used: `/permissions` CRUD

## Known Limitations
- ⚠️ Vitest not installed — no page/service tests.
- ⚠️ Tab mount in `App.tsx` until React Router exists.
- ⚠️ No module filter; paginated list only.

## Refactor Log

### 2026-09-03 | @team
- Initial list + form.
