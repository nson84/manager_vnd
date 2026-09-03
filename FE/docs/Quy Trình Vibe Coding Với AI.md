# Quy Trình Vibe Coding Với AI — Frontend

> Hướng dẫn từ A → Z cho FE của dự án Manager System.
> Áp dụng cho Cursor hoặc bất kỳ AI coding tool nào.

---

## Cấu Trúc Đã Setup

```
FE/
├── AGENTS.md                          # Entry point — AI đọc file này đầu tiên
│
├── .cursor/commands/                  # Các lệnh tắt cho Cursor
│   ├── start.md                       # Mở đầu session
│   ├── new-feature.md                 # Tạo feature mới
│   ├── write-tests.md                 # Viết test cho code có sẵn
│   ├── write-context.md              # Viết CONTEXT.md cho module
│   ├── review-pr.md                   # Review code
│   └── update-status.md              # Cập nhật tiến độ cuối session
│
├── docs/
│   ├── PROJECT-RULES.md              # Convention, naming, patterns
│   ├── PROJECT-STATUS.md             # Tiến độ hiện tại
│   ├── ARCHITECTURE.md               # Kiến trúc FE
│   ├── DATA_MODEL.md                 # TypeScript types (↔ BE entities)
│   ├── API_SPEC.md                    # FE service → BE endpoint mapping
│   └── decisions/                     # Architecture Decision Records
│       ├── 001-auth-token-storage.md
│       └── 002-state-management.md
│
└── src/                               # Source code
    ├── app/                           # App shell, routes
    ├── features/                      # Feature modules
    ├── components/                    # Shared UI
    ├── hooks/                         # Shared hooks
    ├── services/                      # apiClient, authService
    ├── types/                         # Shared types
    └── utils/
```

---

## Nguyên Tắc Cốt Lõi

```
1. Đầu session đọc docs, cuối session update docs
2. Hiểu trước, code sau — đọc CONTEXT.md trước khi sửa feature
3. ADR trước, implement sau — ghi quyết định trước khi code
4. Types khớp BE — luôn đối chiếu ../BE/docs/API_SPEC.md
5. Test đi kèm code — không có test = chưa xong
```

---

## Flow Tổng Quan

```
 /start                                              /update-status
    │                                                       │
    ▼                                                       ▼
┌────────┐    ┌──────────┐    ┌────────┐    ┌─────────┐    ┌────────┐
│  Đọc   │───▶│  Xác nhận │───▶│  Làm   │───▶│ Review  │───▶│  Đóng  │
│ context │    │   task    │    │  việc  │    │  + Test │    │session │
└────────┘    └──────────┘    └────────┘    └─────────┘    └────────┘
```

---

## Commands (Cursor)

| Lệnh | Khi nào | Làm gì |
|-------|---------|--------|
| `/start` | Đầu mỗi session | Đọc context, tóm tắt tiến độ |
| `/new-feature` | Tạo feature UI mới | Scaffold + implement + test + docs |
| `/write-tests` | Code có sẵn chưa có test | Viết service + component tests |
| `/write-context` | Feature mới hoặc logic quan trọng | Tạo CONTEXT.md snapshot |
| `/review-pr` | Trước khi commit | Check code theo checklist |
| `/update-status` | Cuối mỗi session | Cập nhật PROJECT-STATUS.md |

---

## Workflow A — Tạo Feature UI Mới

```
Bạn:  /new-feature — tạo User management UI
```

AI sẽ:
1. Đọc docs (PROJECT-RULES, DATA_MODEL, API_SPEC, BE/API_SPEC)
2. Scaffold `src/features/user/` (types → service → hooks → components → page)
3. Register route in `app/routes.tsx`
4. Viết test (service + component)
5. Update docs (API_SPEC, DATA_MODEL, CONTEXT.md, PROJECT-STATUS)

---

## Workflow B — Fix Bug UI

```
Bạn:  "Đọc CONTEXT.md của auth feature, fix lỗi:
       sau logout vẫn redirect về dashboard"
```

---

## Workflow C — Đồng bộ với BE

Khi BE thêm endpoint mới:
1. Đọc `../BE/docs/API_SPEC.md` → lấy request/response format
2. Update `docs/DATA_MODEL.md` → thêm types
3. Update `docs/API_SPEC.md` → thêm service function
4. Implement feature service + UI

---

## Bảng So Sánh BE ↔ FE

| BE | FE | Mô tả |
|----|-----|-------|
| `AGENTS.md` | `AGENTS.md` | Entry point |
| `docs/DATABASE.md` | `docs/DATA_MODEL.md` | Schema / Types |
| `docs/API_SPEC.md` | `docs/API_SPEC.md` | Endpoints (BE spec ↔ FE client) |
| `feature/{name}/` | `src/features/{name}/` | Feature module |
| `CONTEXT.md` | `CONTEXT.md` | Module context |
| `.cursor/commands/` | `.cursor/commands/` | Slash commands |

---

## Khi AI làm sai

| Tình huống | Cách xử lý |
|-----------|-----------|
| Types không khớp BE | `Đối chiếu ../BE/docs/API_SPEC.md và sửa types` |
| API call trong component | `Chuyển vào services/, component chỉ dùng hooks` |
| Thiếu loading/error state | `Thêm isLoading, error handling theo PROJECT-RULES` |
| Bỏ qua test | `Chưa xong. Viết test theo /write-tests` |
