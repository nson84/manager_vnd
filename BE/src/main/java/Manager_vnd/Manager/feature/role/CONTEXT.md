# Role — Implementation Context
> Written: 2026-09-03 | Author: @team

## Business Context
A role is a named set of permissions assigned to login users. JWT `roles` claim is built from these names (`ADMIN`, `USER`, custom).

## Technical Decisions
- **CRUD ADMIN-only**: matcher + `@PreAuthorize`.
- **`permissionIds` replaces the whole list** on PUT (not additive) — matches API_SPEC.
- **System roles `ADMIN` / `USER`**: cannot delete or rename — seeder + `hasRole` would break.
- **Hard delete**: unlink `user_role` (User owns that join), clear `permission_role`, then delete.

## Considered and Rejected
- **Assign permissions directly on User**: rejected — schema is User → Role → Permission.
- **Soft disable roles**: rejected — API_SPEC is hard delete.

## Dependencies
- Depends on: `permission`, `user` (unlink on delete)
- Depended by: `user` (assign `roleIds`), `auth` (JWT role names)

## Known Limitations
- ⚠️ Fine-grained permission filter not wired — JWT `hasRole` is still the gate.
- ⚠️ MockMvc controller tests deferred.

## Refactor Log

### 2026-09-03 | @team
- Initial CRUD on top of existing entity + seeder.
