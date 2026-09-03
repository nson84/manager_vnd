Write tests for the specified frontend feature module. Read the source code first, then generate tests.

## Step 1: Read Before Writing
- Read the feature's source: types, service, hooks, components, page
- Read `CONTEXT.md` of the module (if exists) to understand trade-offs
- Read `docs/API_SPEC.md` for expected API shapes
- Identify all UI states: loading, empty, error, success, validation

## Step 2: Service Tests — `{feature}Service.test.ts`

Location: `src/features/{feature_name}/__tests__/`

Mock `fetch` or the shared `apiClient`.

Required coverage per function:
- ✅ Success — valid response parsed correctly
- ✅ 401 — throws or returns auth error
- ✅ 404 — not found handled
- ✅ 400 — validation error with field messages
- ✅ Network error — graceful failure

### Service Test Rules
- Mock at HTTP layer, not internal helpers
- One behavior per test
- Naming: `functionName_scenario_expectedResult`

## Step 3: Component Tests — `{Feature}Page.test.tsx`

Use `@testing-library/react` + `@testing-library/user-event`.

Required coverage:
- ✅ Loading state — spinner/skeleton shown
- ✅ Empty state — message shown when no data
- ✅ Error state — error message displayed
- ✅ Success state — data rendered correctly
- ✅ Form validation — invalid input shows errors
- ✅ User action — submit calls service/hook

### Component Test Rules
- Test behavior, not implementation details
- Query by role/label/text, not by className or testId (unless necessary)
- Mock services/hooks, not child components
- Each test is independent

## Step 4: Hook Tests (if applicable)

For custom hooks with logic:
```typescript
import { renderHook, waitFor } from '@testing-library/react'
```

Test data fetching, mutations, and error states.

## Step 5: Verify
- All tests pass: `npm test`
- No test depends on execution order
- Tests don't call real BE API
- Coverage: every service function + main user flows
