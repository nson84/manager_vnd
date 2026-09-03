package Manager_vnd.Manager.feature.wage;

import java.net.URI;
import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Manager_vnd.Manager.dto.ApiResponse;
import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.feature.wage.dto.CreateWageEntryRequest;
import Manager_vnd.Manager.feature.wage.dto.UpdateWageEntryRequest;
import Manager_vnd.Manager.feature.wage.dto.WageEntryResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/wages")
public class WageEntryController {

    private final WageEntryService wageEntryService;

    public WageEntryController(WageEntryService wageEntryService) {
        this.wageEntryService = wageEntryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResult<WageEntryResponse>>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "workDate,desc") String sort,
            @RequestParam(required = false) Long workerId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) Boolean unpaidOnly) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy danh sách ghi công thành công",
                wageEntryService.getAll(page, size, sort, workerId, fromDate, toDate, unpaidOnly)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WageEntryResponse>> getById(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy ghi công thành công",
                wageEntryService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WageEntryResponse>> create(
            @RequestBody @Valid CreateWageEntryRequest request) {
        WageEntryResponse created = wageEntryService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/wages/" + created.id()))
                .body(ApiResponse.created("Ghi công thành công", created));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<WageEntryResponse>> update(
            @RequestBody @Valid UpdateWageEntryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cập nhật ghi công thành công",
                wageEntryService.update(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        wageEntryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
