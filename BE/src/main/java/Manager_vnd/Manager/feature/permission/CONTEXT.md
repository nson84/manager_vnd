# Permission — Implementation Context
> Written: 2026-09-03 | Author: @team

## Business Context
Each permission is one API action (`method` + `apiPath`) grouped by `module`. Roles attach these rows; JWT still carries role names only.

## Technical Decisions
- **CRUD ADMIN-only**: `/api/v1/permissions/**` + `@PreAuthorize("hasRole('ADMIN')")` — catalog is not a shop-floor concern.
- **Unique `(apiPath, method)`**: matches DB index; conflict 409 instead of a second unique on `name`.
- **Hard delete**: clears `permission_role` via Role (join-table owner), then deletes the row — spec requires join cleanup, not soft disable.

## Considered and Rejected
- **Put permission codes in JWT**: rejected — token bloat and stale grants; roles stay in JWT, permissions stay in DB for a later request filter.
- **Soft disable**: rejected — API_SPEC is hard delete.

## Dependencies
- Depends on: `role` (unlink on delete)
- Depended by: `role` (assign `permissionIds`)

## Known Limitations
- ⚠️ No runtime filter yet — having a permission row does not block HTTP; Security still uses `hasRole`.
- ⚠️ MockMvc controller tests deferred (`application-test.yml`).
- ⚠️ No seeder — admin creates rows in UI.

## Refactor Log

### 2026-09-03 | @team
- Initial CRUD.
