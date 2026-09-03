# Quy Trình Vibe Coding Với AI

> Hướng dẫn từ A → Z cho dự án Manager System (BE).
> Áp dụng cho Cursor hoặc bất kỳ AI coding tool nào.

---

## Cấu Trúc Đã Setup

```
BE/
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
│   ├── ARCHITECTURE.md               # Kiến trúc hệ thống
│   ├── DATABASE.md                    # Schema, relationships
│   ├── API_SPEC.md                    # Endpoints specification
│   └── decisions/                     # Architecture Decision Records
│       ├── 001-refresh-token-strategy.md
│       └── 002-file-upload-strategy.md
│
└── src/                               # Source code
```

---

## Nguyên Tắc Cốt Lõi

```
1. Đầu session đọc docs, cuối session update docs
2. Hiểu trước, code sau — đọc CONTEXT.md trước khi sửa module
3. ADR trước, implement sau — ghi quyết định trước khi code
4. Không merge code không hiểu — AI viết, người review
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

## Quy Trình Chi Tiết

### ═══════════════════════════════════════
### PHASE 1: MỞ SESSION
### ═══════════════════════════════════════

```
Bạn:  /start
```

AI sẽ tự động:
1. Đọc `AGENTS.md` → biết project là gì
2. Đọc `PROJECT-STATUS.md` → biết đang ở đâu
3. Tóm tắt lại cho bạn: đã xong gì, đang dở gì, cảnh báo gì, task tiếp theo
4. Chờ bạn xác nhận

```
AI:   "Session trước đã xong User CRUD + test.
       Đang dở: Company CRUD — chưa viết test.
       Cảnh báo: CI flaky ở test concurrent payment.
       Đề xuất: hoàn thành Company test, sau đó làm Role CRUD."

Bạn:  "Ok, làm Company test trước"
      hoặc
      "Không, hôm nay fix bug auth trước"
```

**Tại sao phải làm bước này?**
AI không có memory giữa các session. Bỏ bước này = AI code mù, không biết convention, không biết context → tốn token sửa sai gấp 10 lần.

---

### ═══════════════════════════════════════
### PHASE 2: LÀM VIỆC
### ═══════════════════════════════════════

Tùy loại task, chọn đúng workflow:

---

#### WORKFLOW A — Tạo Feature Mới

Khi nào: thêm module CRUD mới (Company, Role, Permission...)

```
Bạn:  /new-feature — tạo Company CRUD
```

AI sẽ thực hiện theo thứ tự:

```
Step 1: Đọc docs
        ├── PROJECT-RULES.md  → convention
        ├── DATABASE.md       → schema của Company
        └── API_SPEC.md       → endpoints cần implement

Step 2: Scaffold package
        feature/company/
        ├── Company.java
        ├── CompanyController.java
        ├── CompanyService.java
        ├── CompanyServiceImpl.java
        ├── CompanyRepository.java
        └── dto/
            ├── CreateCompanyRequest.java
            ├── UpdateCompanyRequest.java
            └── CompanyResponse.java

Step 3: Implement (theo thứ tự)
        Entity → Repository → DTOs → Service Interface
        → ServiceImpl → Controller

Step 4: Viết test
        ├── CompanyServiceImplTest.java    (unit test)
        └── CompanyControllerTest.java     (integration test)

Step 5: Update docs
        ├── API_SPEC.md     (nếu endpoint mới)
        ├── DATABASE.md     (nếu schema mới)
        └── CONTEXT.md      (viết cho module mới)
```

**Sau mỗi step, bạn nên review trước khi AI làm step tiếp.**
Đừng để AI chạy hết 5 step rồi mới review — nếu step 1 sai, tất cả sau đó đều sai.

---

#### WORKFLOW B — Fix Bug

Khi nào: có lỗi cần sửa trong code đã có

```
Bạn:  "Đọc CONTEXT.md của auth module, sau đó fix lỗi:
       refresh token không bị revoke khi delete user"
```

AI sẽ:

```
Step 1: Đọc CONTEXT.md
        → hiểu design decisions, known limitations, trade-offs
        → tránh "fix" thứ là intentional

Step 2: Phân tích bug
        → trace từ symptom → root cause
        → xác nhận đây là bug thật, không phải trade-off đã biết

Step 3: Đề xuất fix
        → giải thích cách fix
        → chờ bạn confirm trước khi code

Step 4: Implement fix
        → scope nhỏ nhất có thể
        → không refactor thêm thứ không liên quan

Step 5: Viết test cover bug
        → test case cho đúng case bị lỗi
        → đảm bảo bug không quay lại

Step 6: Update docs
        → CONTEXT.md: thêm entry Refactor Log
        → PROJECT-STATUS.md: ghi lại bug đã fix
```

**Quan trọng: luôn bảo AI đọc CONTEXT.md trước khi sửa.** Không có bước này, AI có thể "fix" một thứ mà team đã cố tình thiết kế như vậy.

---

#### WORKFLOW C — Viết Test Cho Code Có Sẵn

Khi nào: code đã viết nhưng chưa có test

```
Bạn:  /write-tests — viết test cho company module
```

---

#### WORKFLOW D — Thêm Logic Vào Feature Có Sẵn

Khi nào: feature đã có, cần thêm chức năng (vd: thêm search, filter, export...)

```
Bạn:  "Đọc CONTEXT.md của user module.
       Thêm chức năng search user theo name và email"
```

---

#### WORKFLOW E — Quyết Định Kiến Trúc

Khi nào: cần chọn giữa nhiều cách tiếp cận (vd: cách lưu file, cách handle cache...)

**Luôn theo thứ tự: phân tích → quyết định → ghi ADR → rồi mới code.**

---

#### WORKFLOW F — Review Code

Khi nào: trước khi commit/merge

```
Bạn:  /review-pr
```

Output:
```
🔴 Blockers    — phải fix trước khi merge
🟡 Suggestions — nên fix nhưng không chặn
🟢 Good parts  — điểm tốt, giữ nguyên
```

---

### ═══════════════════════════════════════
### PHASE 3: ĐÓNG SESSION
### ═══════════════════════════════════════

```
Bạn:  /update-status
```

AI sẽ tự động cập nhật `PROJECT-STATUS.md` và bạn commit code + docs cùng lúc.

---

## Bảng Tham Chiếu Nhanh

### Commands (Cursor)

| Lệnh | Khi nào | Làm gì |
|-------|---------|--------|
| `/start` | Đầu mỗi session | Đọc context, tóm tắt tiến độ |
| `/new-feature` | Tạo module CRUD mới | Scaffold + implement + test + docs |
| `/write-tests` | Code có sẵn chưa có test | Viết unit test + integration test |
| `/write-context` | Module mới hoặc logic quan trọng | Tạo CONTEXT.md snapshot |
| `/review-pr` | Trước khi commit | Check code theo checklist |
| `/update-status` | Cuối mỗi session | Cập nhật PROJECT-STATUS.md |

### Câu lệnh thường dùng

| Bạn muốn | Nói với AI |
|-----------|-----------|
| Bắt đầu ngày mới | `/start` |
| Tạo feature mới | `/new-feature — tạo [tên] CRUD` |
| Fix bug | `Đọc CONTEXT.md của [module], fix lỗi [mô tả]` |
| Thêm logic | `Đọc CONTEXT.md của [module], thêm [chức năng]` |
| Viết test | `/write-tests — viết test cho [module]` |
| Quyết định kiến trúc | `Tôi cần [yêu cầu], nên dùng cách nào?` |
| Review | `/review-pr` |
| Kết thúc ngày | `/update-status` |

### Khi AI làm sai

| Tình huống | Cách xử lý |
|-----------|-----------|
| Code sai convention | `Đọc lại PROJECT-RULES.md section [X], sửa lại` |
| Sửa nhầm trade-off | `Đọc CONTEXT.md, phần Known Limitations. Đây là intentional` |
| Code quá phức tạp | `Đơn giản hóa. File không quá 300 dòng, method không quá 50` |
| Không hiểu yêu cầu | `Dừng lại, hỏi tôi trước khi code tiếp` |
| Bỏ qua test | `Chưa xong. Viết test theo /write-tests` |
| Quên update docs | `Chạy /update-status và update [file] trước khi commit` |
