Create a new frontend feature module. Follow these steps in exact order.

## Step 1: Understand Context
- Read `AGENTS.md` → follow links to PROJECT-RULES.md
- Read `docs/ARCHITECTURE.md` → understand where this feature fits
- Read `docs/DATA_MODEL.md` → check if types already exist
- Read `docs/API_SPEC.md` + `../BE/docs/API_SPEC.md` → check endpoints to integrate
- Ask me if anything is unclear before writing code

## Step 2: Create Feature Structure
```
src/features/{feature_name}/
├── components/              # Feature-specific UI
│   └── {Feature}List.tsx
├── hooks/                   # Feature hooks (use{Feature}, use{Feature}Form)
│   └── use{Feature}.ts
├── services/                # API calls for this feature
│   └── {feature}Service.ts
├── types/                   # Feature types
│   └── {feature}.types.ts
├── pages/                   # Route page (thin wrapper)
│   └── {Feature}Page.tsx
└── index.ts                 # Public exports
```

Test files (mirror under `src/features/{feature_name}/`):
```
__tests__/
├── {feature}Service.test.ts    # Unit test — API/service logic
└── {Feature}Page.test.tsx      # Component test — user flows
```

## Step 3: Implement in This Order
1. **Types** — request/response interfaces matching BE DTOs
2. **Service** — API functions using shared `apiClient`
3. **Hooks** — data fetching, form state, mutations
4. **Components** — presentational + container split if needed
5. **Page** — compose components, wire to router
6. **Route** — register in `src/app/routes.tsx`

## Step 4: Write Tests

### Service Tests — `{feature}Service.test.ts`
Mock `fetch` or `apiClient`. Test:
- Success response parsing
- Error handling (401, 404, validation errors)
- Correct URL and HTTP method

### Component Tests — `{Feature}Page.test.tsx`
Use React Testing Library. Test:
- Renders loading / empty / error / success states
- Form validation messages
- User actions trigger correct callbacks

## Step 5: Follow These Rules
- No business logic in page components — use hooks/services
- All API calls go through `services/` layer, not directly in components
- Use shared `ApiResponse<T>` type from `src/types/api.types.ts`
- Handle loading, error, and empty states explicitly
- No `any` — strict TypeScript types

## Step 6: Update Documentation
- Update `docs/API_SPEC.md` with new FE client functions
- Update `docs/DATA_MODEL.md` if new types added
- Create `CONTEXT.md` inside the feature folder (use /write-context command)
- Update `docs/PROJECT-STATUS.md` (use /update-status command)

## Step 7: Verify
- `npm run build` passes with no errors
- All tests pass
- No file exceeds 300 lines
- No function exceeds 50 lines
- Check commit checklist in PROJECT-RULES.md section 14
