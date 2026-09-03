package Manager_vnd.Manager.feature.cashbook;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Manager_vnd.Manager.dto.ApiResponse;
import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.feature.cashbook.dto.CashEntryResponse;
import Manager_vnd.Manager.feature.cashbook.dto.CashStatsResponse;
import Manager_vnd.Manager.feature.cashbook.dto.CategorySummary;
import Manager_vnd.Manager.feature.cashbook.dto.CreateManualCashEntryRequest;
import Manager_vnd.Manager.feature.cashbook.dto.UpdateCashCheckedRequest;
import Manager_vnd.Manager.feature.cashbook.dto.UpdateCashNoteRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/cashbook")
public class CashbookController {

    private final CashbookService cashbookService;

    public CashbookController(CashbookService cashbookService) {
        this.cashbookService = cashbookService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResult<CashEntryResponse>>> getEntries(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "entryDate,desc") String sort,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) CashDirection direction,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) CashRefType refType,
            @RequestParam(required = false) Long refId,
            @RequestParam(required = false) Long createdBy,
            @RequestParam(required = false) Boolean checked,
            @RequestParam(required = false) BigDecimal amountMin,
            @RequestParam(required = false) BigDecimal amountMax,
            @RequestParam(required = false) String q) {
        CashEntryFilter filter = toFilter(
                fromDate, toDate, direction, categoryId, refType, refId, createdBy, checked, amountMin, amountMax, q);
        PaginatedResult<CashEntryResponse> result = cashbookService.getEntries(filter, page, size, sort);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách sổ quỹ thành công", result));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<CashStatsResponse>> getStats(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) CashDirection direction,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) CashRefType refType,
            @RequestParam(required = false) Long refId,
            @RequestParam(required = false) Long createdBy,
            @RequestParam(required = false) Boolean checked,
            @RequestParam(required = false) BigDecimal amountMin,
            @RequestParam(required = false) BigDecimal amountMax,
            @RequestParam(required = false) String q) {
        CashEntryFilter filter = toFilter(
                fromDate, toDate, direction, categoryId, refType, refId, createdBy, checked, amountMin, amountMax, q);
        return ResponseEntity.ok(ApiResponse.success("Lấy thống kê sổ quỹ thành công", cashbookService.getStats(filter)));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategorySummary>>> listCategories() {
        return ResponseEntity.ok(ApiResponse.success("Lấy danh mục thu/chi thành công", cashbookService.listCategories()));
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) CashDirection direction,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) CashRefType refType,
            @RequestParam(required = false) Long refId,
            @RequestParam(required = false) Long createdBy,
            @RequestParam(required = false) Boolean checked,
            @RequestParam(required = false) BigDecimal amountMin,
            @RequestParam(required = false) BigDecimal amountMax,
            @RequestParam(required = false) String q) {
        CashEntryFilter filter = toFilter(
                fromDate, toDate, direction, categoryId, refType, refId, createdBy, checked, amountMin, amountMax, q);
        byte[] pdf = cashbookService.exportPdf(filter);
        String filename = "so-quy.pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CashEntryResponse>> getById(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết sổ quỹ thành công", cashbookService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CashEntryResponse>> createManual(
            @RequestBody @Valid CreateManualCashEntryRequest request) {
        CashEntryResponse created = cashbookService.createManual(request);
        return ResponseEntity
                .created(URI.create("/api/v1/cashbook/" + created.id()))
                .body(ApiResponse.created("Tạo phiếu quỹ thành công", created));
    }

    @PatchMapping("/{id}/note")
    public ResponseEntity<ApiResponse<CashEntryResponse>> updateNote(
            @PathVariable long id,
            @RequestBody @Valid UpdateCashNoteRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cập nhật ghi chú thành công",
                cashbookService.updateNote(id, request)));
    }

    @PatchMapping("/{id}/checked")
    public ResponseEntity<ApiResponse<CashEntryResponse>> updateChecked(
            @PathVariable long id,
            @RequestBody @Valid UpdateCashCheckedRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cập nhật đối chiếu thành công",
                cashbookService.updateChecked(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        cashbookService.deleteManual(id);
        return ResponseEntity.noContent().build();
    }

    private CashEntryFilter toFilter(
            LocalDate fromDate,
            LocalDate toDate,
            CashDirection direction,
            Long categoryId,
            CashRefType refType,
            Long refId,
            Long createdBy,
            Boolean checked,
            BigDecimal amountMin,
            BigDecimal amountMax,
            String q) {
        return new CashEntryFilter(
                fromDate, toDate, direction, categoryId, refType, refId, createdBy, checked, amountMin, amountMax, q);
    }
}
