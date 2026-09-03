Create a new **Frontend** feature module in `FE/`.

Follow the full workflow in `FE/.cursor/commands/new-feature.md`:

1. Read `FE/AGENTS.md` and linked docs
2. Work inside `FE/src/features/{feature_name}/`
3. Follow `FE/docs/PROJECT-RULES.md` strictly
4. Cross-check types with `BE/docs/API_SPEC.md` and `FE/docs/DATA_MODEL.md`
5. Update `FE/docs/API_SPEC.md` when adding service functions
6. Update `FE/docs/PROJECT-STATUS.md` and root `docs/PROJECT-STATUS.md` when done

BE endpoints must exist (or be stubbed) before integrating API calls.
