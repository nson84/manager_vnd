# Role — Frontend Context
> Written: 2026-09-03 | Author: @team

## Business Context
Admin assigns a named role and a set of API permissions. Users pick roles on the User form (ids); this screen manages the catalog.

## Technical Decisions
- **Checkbox catalog from Permission API** (`GET /permissions` pageSize 200) so Role form does not duplicate types.
- **Hide delete for ADMIN/USER**: matches BE system-role guard.
- **PUT always sends full `permissionIds`**: BE replaces the list.

## Considered and Rejected
- **Comma-separated permission ids**: rejected — checkboxes match the many-to-many model.

## Dependencies
- Depends on: `permission` (public `permissionService` + types)
- Depended by: none yet (User form still types role ids by hand)
- BE endpoints used: `/roles` CRUD, `/permissions` list

## Known Limitations
- ⚠️ Catalog capped at 200 permissions.
- ⚠️ Vitest / React Router not installed.
- ⚠️ Changing a role's permissions does not refresh existing JWTs until re-login.

## Refactor Log

### 2026-09-03 | @team
- Initial list + form.
