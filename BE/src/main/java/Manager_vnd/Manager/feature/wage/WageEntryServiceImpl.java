package Manager_vnd.Manager.feature.wage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Manager_vnd.Manager.config.ActorResolver;
import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.dto.PaginationMeta;
import Manager_vnd.Manager.exception.ConflictException;
import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.wage.dto.CreateWageEntryRequest;
import Manager_vnd.Manager.feature.wage.dto.UpdateWageEntryRequest;
import Manager_vnd.Manager.feature.wage.dto.WageEntryResponse;
import Manager_vnd.Manager.feature.worker.WageType;
import Manager_vnd.Manager.feature.worker.Worker;
import Manager_vnd.Manager.feature.worker.WorkerRepository;
import jakarta.persistence.criteria.Predicate;

@Service
public class WageEntryServiceImpl implements WageEntryService {

    private final WageEntryRepository wageEntryRepository;
    private final WorkerRepository workerRepository;
    private final ActorResolver actorResolver;

    public WageEntryServiceImpl(
            WageEntryRepository wageEntryRepository,
            WorkerRepository workerRepository,
            ActorResolver actorResolver) {
        this.wageEntryRepository = wageEntryRepository;
        this.workerRepository = workerRepository;
        this.actorResolver = actorResolver;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<WageEntryResponse> getAll(
            int page, int size, String sort,
            Long workerId, LocalDate fromDate, LocalDate toDate, Boolean unpaidOnly) {
        Pageable pageable = toPageable(page, size, sort);
        Page<WageEntry> result = wageEntryRepository.findAll(
                buildSpec(workerId, fromDate, toDate, unpaidOnly), pageable);
        List<WageEntryResponse> items = result.getContent().stream()
                .map(WageEntryResponse::fromEntity)
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
    public WageEntryResponse getById(long id) {
        return WageEntryResponse.fromEntity(findEntry(id));
    }

    @Override
    @Transactional
    public WageEntryResponse create(CreateWageEntryRequest request) {
        Worker worker = workerRepository.findById(request.workerId())
                .orElseThrow(() -> new ResourceNotFoundException("Worker", "id", request.workerId()));

        WageType wageType = request.wageType() != null ? request.wageType() : worker.getWageType();
        BigDecimal unitRate = request.unitRate() != null ? request.unitRate() : worker.getDefaultUnitRate();
        BigDecimal amount = calcAmount(request.quantity(), unitRate);

        WageEntry entry = new WageEntry();
        entry.setWorker(worker);
        entry.setWorkDate(request.workDate());
        entry.setWageType(wageType);
        entry.setQuantity(request.quantity());
        entry.setUnitRate(unitRate);
        entry.setAmount(amount);
        entry.setNote(blankToNull(request.note()));
        entry.setCreatedBy(actorResolver.requireActor());
        return WageEntryResponse.fromEntity(wageEntryRepository.save(entry));
    }

    @Override
    @Transactional
    public WageEntryResponse update(UpdateWageEntryRequest request) {
        WageEntry entry = findEntry(request.id());
        assertEditable(entry);

        if (request.workDate() != null) {
            entry.setWorkDate(request.workDate());
        }
        if (request.wageType() != null) {
            entry.setWageType(request.wageType());
        }
        if (request.quantity() != null) {
            entry.setQuantity(request.quantity());
        }
        if (request.unitRate() != null) {
            entry.setUnitRate(request.unitRate());
        }
        if (request.note() != null) {
            entry.setNote(blankToNull(request.note()));
        }
        entry.setAmount(calcAmount(entry.getQuantity(), entry.getUnitRate()));
        return WageEntryResponse.fromEntity(wageEntryRepository.save(entry));
    }

    @Override
    @Transactional
    public void delete(long id) {
        WageEntry entry = findEntry(id);
        assertEditable(entry);
        wageEntryRepository.delete(entry);
    }

    private void assertEditable(WageEntry entry) {
        if (entry.getPayslip() != null) {
            throw new ConflictException("Không sửa/xóa công đã gắn phiếu lương");
        }
    }

    private BigDecimal calcAmount(BigDecimal quantity, BigDecimal unitRate) {
        return quantity.multiply(unitRate).setScale(2, RoundingMode.HALF_UP);
    }

    private Specification<WageEntry> buildSpec(
            Long workerId, LocalDate fromDate, LocalDate toDate, Boolean unpaidOnly) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (workerId != null) {
                predicates.add(cb.equal(root.get("worker").get("id"), workerId));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("workDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("workDate"), toDate));
            }
            if (Boolean.TRUE.equals(unpaidOnly)) {
                predicates.add(cb.isNull(root.get("payslip")));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private WageEntry findEntry(long id) {
        return wageEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WageEntry", "id", id));
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
            return Sort.by("workDate").descending();
        }
        String[] parts = sort.split(",");
        if (parts.length == 2) {
            return Sort.by(Sort.Direction.fromString(parts[1].trim()), parts[0].trim());
        }
        return Sort.by("workDate").descending();
    }
}
