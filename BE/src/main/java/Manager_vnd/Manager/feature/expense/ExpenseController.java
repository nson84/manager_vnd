package Manager_vnd.Manager.feature.expense;

import java.net.URI;
import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Manager_vnd.Manager.dto.ApiResponse;
import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.feature.expense.dto.CreateExpenseRequest;
import Manager_vnd.Manager.feature.expense.dto.ExpenseResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResult<ExpenseResponse>>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "expenseDate,desc") String sort,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) ExpenseStatus status,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy danh sách phiếu chi thành công",
                expenseService.getAll(page, size, sort, categoryId, status, fromDate, toDate)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getById(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy phiếu chi thành công",
                expenseService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> create(
            @RequestBody @Valid CreateExpenseRequest request) {
        ExpenseResponse created = expenseService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/expenses/" + created.id()))
                .body(ApiResponse.created("Tạo phiếu chi thành công", created));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ExpenseResponse>> cancel(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Hủy phiếu chi thành công",
                expenseService.cancel(id)));
    }
}
