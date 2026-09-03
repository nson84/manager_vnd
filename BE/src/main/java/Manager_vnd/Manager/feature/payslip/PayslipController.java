package Manager_vnd.Manager.feature.payslip;

import java.net.URI;
import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
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
import Manager_vnd.Manager.feature.payslip.dto.CreatePayslipRequest;
import Manager_vnd.Manager.feature.payslip.dto.PayslipResponse;
import Manager_vnd.Manager.feature.payslip.dto.UpdatePayslipRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/payslips")
public class PayslipController {

    private final PayslipService payslipService;

    public PayslipController(PayslipService payslipService) {
        this.payslipService = payslipService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResult<PayslipResponse>>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort,
            @RequestParam(required = false) Long workerId,
            @RequestParam(required = false) PayslipStatus status,
            @RequestParam(required = false) LocalDate periodFrom,
            @RequestParam(required = false) LocalDate periodTo) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy danh sách phiếu lương thành công",
                payslipService.getAll(page, size, sort, workerId, status, periodFrom, periodTo)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PayslipResponse>> getById(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy phiếu lương thành công",
                payslipService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PayslipResponse>> create(
            @RequestBody @Valid CreatePayslipRequest request) {
        PayslipResponse created = payslipService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/payslips/" + created.id()))
                .body(ApiResponse.created("Tạo phiếu lương DRAFT thành công", created));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<PayslipResponse>> update(
            @RequestBody @Valid UpdatePayslipRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cập nhật phiếu lương thành công",
                payslipService.update(request)));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<PayslipResponse>> confirm(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Xác nhận phiếu lương thành công",
                payslipService.confirm(id)));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<PayslipResponse>> pay(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Thanh toán phiếu lương thành công",
                payslipService.pay(id)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<PayslipResponse>> cancel(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Hủy phiếu lương thành công",
                payslipService.cancel(id)));
    }
}
