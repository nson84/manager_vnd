package Manager_vnd.Manager.feature.worker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.dto.PaginationMeta;
import Manager_vnd.Manager.exception.ConflictException;
import Manager_vnd.Manager.exception.InvalidRequestException;
import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.worker.dto.CreateWorkerRequest;
import Manager_vnd.Manager.feature.worker.dto.UpdateWorkerRequest;
import Manager_vnd.Manager.feature.worker.dto.WorkerResponse;
import jakarta.persistence.criteria.Predicate;

@Service
public class WorkerServiceImpl implements WorkerService {

    private final WorkerRepository workerRepository;

    public WorkerServiceImpl(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<WorkerResponse> getAll(int page, int size, String sort, Boolean active, String q) {
        Pageable pageable = toPageable(page, size, sort);
        Page<Worker> result = workerRepository.findAll(buildSpec(active, q), pageable);
        List<WorkerResponse> items = result.getContent().stream()
                .map(WorkerResponse::fromEntity)
                .toList();
        PaginationMeta meta = new PaginationMeta(
                result.getNumber() + 1,
                result.getSize(),
                result.getTotalPages(),
                result.getTotalElements());
        return new PaginatedResult<>(meta, items);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkerResponse getById(long id) {
        return WorkerResponse.fromEntity(findWorker(id));
    }

    @Override
    @Transactional
    public WorkerResponse create(CreateWorkerRequest request) {
        String phone = normalizePhone(request.phone());
        assertPhoneUnique(phone, null);

        Worker worker = new Worker();
        worker.setName(request.name().trim());
        worker.setPhone(phone);
        worker.setAddress(blankToNull(request.address()));
        worker.setJobTitle(blankToNull(request.jobTitle()));
        worker.setWageType(request.wageType());
        worker.setDefaultUnitRate(request.defaultUnitRate());
        worker.setHireDate(request.hireDate());
        worker.setNote(blankToNull(request.note()));
        worker.setActive(true);
        worker.setCurrentAdvance(BigDecimal.ZERO);
        return WorkerResponse.fromEntity(workerRepository.save(worker));
    }

    @Override
    @Transactional
    public WorkerResponse update(UpdateWorkerRequest request) {
        Worker worker = findWorker(request.id());
        if (request.name() != null) {
            String name = request.name().trim();
            if (name.isEmpty()) {
                throw new InvalidRequestException("Tên thợ không được để trống");
            }
            worker.setName(name);
        }
        if (request.phone() != null) {
            String phone = normalizePhone(request.phone());
            assertPhoneUnique(phone, worker.getId());
            worker.setPhone(phone);
        }
        if (request.address() != null) {
            worker.setAddress(blankToNull(request.address()));
        }
        if (request.jobTitle() != null) {
            worker.setJobTitle(blankToNull(request.jobTitle()));
        }
        if (request.wageType() != null) {
            worker.setWageType(request.wageType());
        }
        if (request.defaultUnitRate() != null) {
            worker.setDefaultUnitRate(request.defaultUnitRate());
        }
        if (request.hireDate() != null) {
            worker.setHireDate(request.hireDate());
        }
        if (request.note() != null) {
            worker.setNote(blankToNull(request.note()));
        }
        return WorkerResponse.fromEntity(workerRepository.save(worker));
    }

    @Override
    @Transactional
    public WorkerResponse disable(long id) {
        Worker worker = findWorker(id);
        if (!worker.isActive()) {
            throw new ConflictException("Thợ đã bị vô hiệu hóa");
        }
        worker.setActive(false);
        return WorkerResponse.fromEntity(workerRepository.save(worker));
    }

    @Override
    @Transactional
    public WorkerResponse enable(long id) {
        Worker worker = findWorker(id);
        if (worker.isActive()) {
            throw new ConflictException("Thợ đang hoạt động");
        }
        worker.setActive(true);
        return WorkerResponse.fromEntity(workerRepository.save(worker));
    }

    private Specification<Worker> buildSpec(Boolean active, String q) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }
            if (q != null && !q.isBlank()) {
                String pattern = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("phone"), "")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("jobTitle"), "")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("note"), "")), pattern)));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Worker findWorker(long id) {
        return workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", "id", id));
    }

    private void assertPhoneUnique(String phone, Long excludeId) {
        if (phone == null) {
            return;
        }
        boolean exists = excludeId == null
                ? workerRepository.existsByPhone(phone)
                : workerRepository.existsByPhoneAndIdNot(phone, excludeId);
        if (exists) {
            throw new ConflictException("Số điện thoại đã tồn tại");
        }
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        return phone.trim();
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private Pageable toPageable(int page, int size, String sort) {
        int zeroBased = Math.max(page - 1, 0);
        return PageRequest.of(zeroBased, size, parseSort(sort));
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by("id").ascending();
        }
        String[] parts = sort.split(",");
        if (parts.length == 2) {
            return Sort.by(Sort.Direction.fromString(parts[1].trim()), parts[0].trim());
        }
        return Sort.by("id").ascending();
    }
}
