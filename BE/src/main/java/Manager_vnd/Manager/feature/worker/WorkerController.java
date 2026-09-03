package Manager_vnd.Manager.feature.worker;

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
import Manager_vnd.Manager.feature.worker.dto.CreateWorkerRequest;
import Manager_vnd.Manager.feature.worker.dto.UpdateWorkerRequest;
import Manager_vnd.Manager.feature.worker.dto.WorkerResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/workers")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResult<WorkerResponse>>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String q) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy danh sách thợ thành công",
                workerService.getAll(page, size, sort, active, q)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkerResponse>> getById(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy thông tin thợ thành công",
                workerService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WorkerResponse>> create(@RequestBody @Valid CreateWorkerRequest request) {
        WorkerResponse created = workerService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/workers/" + created.id()))
                .body(ApiResponse.created("Tạo thợ thành công", created));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<WorkerResponse>> update(@RequestBody @Valid UpdateWorkerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cập nhật thợ thành công",
                workerService.update(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkerResponse>> disable(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Vô hiệu hóa thợ thành công",
                workerService.disable(id)));
    }

    @PostMapping("/{id}/enable")
    public ResponseEntity<ApiResponse<WorkerResponse>> enable(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Kích hoạt thợ thành công",
                workerService.enable(id)));
    }
}
