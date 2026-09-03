# Manager

Monorepo full-stack — Backend (Spring Boot) + Frontend (React + Vite).

## Cấu trúc

```
Manager/
├── AGENTS.md              # Entry point AI (monorepo)
├── .cursor/commands/      # Slash commands: /start, /new-feature-be, ...
├── docs/                  # Monorepo docs (status, architecture)
│
├── BE/                    # Backend — Spring Boot + JPA + MySQL
│   ├── AGENTS.md
│   ├── .cursor/
│   └── docs/
│
└── FE/                    # Frontend — React + TypeScript + Vite
    ├── AGENTS.md
    ├── .cursor/
    └── docs/
```

## Chạy Backend

```bash
cd BE
./mvnw spring-boot:run
```

Backend: http://localhost:8080

## Chạy Frontend

```bash
cd FE
npm install
npm run dev
```

Frontend: http://localhost:5173 — API proxy `/api` → BE.

## AI Workflow (Cursor)

Mở folder `Manager` trong Cursor, dùng slash commands:

| Lệnh | Mô tả |
|------|--------|
| `/start` | Đọc context BE + FE, tóm tắt tiến độ |
| `/new-feature-be` | Tạo feature backend mới |
| `/new-feature-fe` | Tạo feature frontend mới |
| `/write-tests-be` | Viết test backend |
| `/write-tests-fe` | Viết test frontend |
| `/review-pr` | Review code BE/FE |
| `/update-status` | Cập nhật tiến độ |

Chi tiết:
- Monorepo: `AGENTS.md`, `docs/PROJECT-STATUS.md`
- BE: `BE/docs/Quy Trình Vibe Coding Với AI.md`
- FE: `FE/docs/Quy Trình Vibe Coding Với AI.md`
