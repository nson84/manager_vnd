Write tests for a **Backend** feature in `BE/`.

Follow `BE/.cursor/commands/write-tests.md`:

1. Read feature source under `BE/src/`
2. Unit tests: `{Feature}ServiceImplTest.java`
3. Integration tests: `{Feature}ControllerTest.java` with `@ActiveProfiles("test")`
4. Run: `cd BE && ./mvnw test`
