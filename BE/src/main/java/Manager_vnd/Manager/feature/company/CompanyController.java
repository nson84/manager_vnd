package Manager_vnd.Manager.feature.company;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import Manager_vnd.Manager.feature.company.dto.CompanyResponse;
import Manager_vnd.Manager.feature.company.dto.CreateCompanyRequest;
import Manager_vnd.Manager.feature.company.dto.PublicCompanyResponse;
import Manager_vnd.Manager.feature.company.dto.UpdateCompanyRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/companies")
@PreAuthorize("hasRole('ADMIN')")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/public")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<PublicCompanyResponse>>> listPublic() {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy danh sách cửa hàng thành công",
                companyService.listPublic()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResult<CompanyResponse>>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort,
            @RequestParam(required = false) Boolean active) {
        PaginatedResult<CompanyResponse> result = companyService.getAll(page, size, sort, active);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách công ty thành công", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getById(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy thông tin công ty thành công",
                companyService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> create(
            @RequestBody @Valid CreateCompanyRequest request) {
        CompanyResponse created = companyService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/companies/" + created.id()))
                .body(ApiResponse.created("Tạo công ty thành công", created));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> update(
            @RequestBody @Valid UpdateCompanyRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cập nhật công ty thành công",
                companyService.update(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> disable(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Vô hiệu hóa công ty thành công",
                companyService.disable(id)));
    }

    @PostMapping("/{id}/enable")
    public ResponseEntity<ApiResponse<CompanyResponse>> enable(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Kích hoạt công ty thành công",
                companyService.enable(id)));
    }
}
