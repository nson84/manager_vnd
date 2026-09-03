package Manager_vnd.Manager.feature.customer;

import java.net.URI;

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
import Manager_vnd.Manager.feature.customer.dto.CreateCustomerRequest;
import Manager_vnd.Manager.feature.customer.dto.CustomerResponse;
import Manager_vnd.Manager.feature.customer.dto.UpdateCustomerRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResult<CustomerResponse>>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String q) {
        PaginatedResult<CustomerResponse> result = customerService.getAll(page, size, sort, active, q);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khách hàng thành công", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getById(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy thông tin khách hàng thành công",
                customerService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> create(
            @RequestBody @Valid CreateCustomerRequest request) {
        CustomerResponse created = customerService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/customers/" + created.id()))
                .body(ApiResponse.created("Tạo khách hàng thành công", created));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> update(
            @RequestBody @Valid UpdateCustomerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cập nhật khách hàng thành công",
                customerService.update(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> disable(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Vô hiệu hóa khách hàng thành công",
                customerService.disable(id)));
    }

    @PostMapping("/{id}/enable")
    public ResponseEntity<ApiResponse<CustomerResponse>> enable(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Kích hoạt khách hàng thành công",
                customerService.enable(id)));
    }
}
