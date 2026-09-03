package Manager_vnd.Manager.feature.debt;

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
import Manager_vnd.Manager.feature.debt.dto.CreateDebtEntryRequest;
import Manager_vnd.Manager.feature.debt.dto.DebtEntryResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/debts")
public class DebtEntryController {

    private final DebtEntryService debtEntryService;

    public DebtEntryController(DebtEntryService debtEntryService) {
        this.debtEntryService = debtEntryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResult<DebtEntryResponse>>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "entryDate,desc") String sort,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long workerId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) DebtEntryType entryType) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy sổ công nợ thành công",
                debtEntryService.getAll(page, size, sort, customerId, workerId, fromDate, toDate, entryType)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DebtEntryResponse>> getById(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy bút toán công nợ thành công",
                debtEntryService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DebtEntryResponse>> create(
            @RequestBody @Valid CreateDebtEntryRequest request) {
        DebtEntryResponse created = debtEntryService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/debts/" + created.id()))
                .body(ApiResponse.created("Ghi công nợ thành công", created));
    }
}
