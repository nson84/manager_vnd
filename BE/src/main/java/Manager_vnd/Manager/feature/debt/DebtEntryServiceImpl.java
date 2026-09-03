package Manager_vnd.Manager.feature.debt;

import java.math.BigDecimal;
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
import Manager_vnd.Manager.exception.InvalidRequestException;
import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.cashbook.CashDirection;
import Manager_vnd.Manager.feature.cashbook.CashLedgerWriter;
import Manager_vnd.Manager.feature.cashbook.CashRefType;
import Manager_vnd.Manager.feature.customer.Customer;
import Manager_vnd.Manager.feature.customer.CustomerRepository;
import Manager_vnd.Manager.feature.debt.dto.CreateDebtEntryRequest;
import Manager_vnd.Manager.feature.debt.dto.DebtEntryResponse;
import Manager_vnd.Manager.feature.worker.Worker;
import Manager_vnd.Manager.feature.worker.WorkerRepository;
import jakarta.persistence.criteria.Predicate;

@Service
public class DebtEntryServiceImpl implements DebtEntryService {

    private final DebtEntryRepository debtEntryRepository;
    private final CustomerRepository customerRepository;
    private final WorkerRepository workerRepository;
    private final ActorResolver actorResolver;
    private final CashLedgerWriter cashLedgerWriter;

    public DebtEntryServiceImpl(
            DebtEntryRepository debtEntryRepository,
            CustomerRepository customerRepository,
            WorkerRepository workerRepository,
            ActorResolver actorResolver,
            CashLedgerWriter cashLedgerWriter) {
        this.debtEntryRepository = debtEntryRepository;
        this.customerRepository = customerRepository;
        this.workerRepository = workerRepository;
        this.actorResolver = actorResolver;
        this.cashLedgerWriter = cashLedgerWriter;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<DebtEntryResponse> getAll(
            int page, int size, String sort,
            Long customerId, Long workerId,
            LocalDate fromDate, LocalDate toDate,
            DebtEntryType entryType) {
        Pageable pageable = toPageable(page, size, sort);
        Page<DebtEntry> result = debtEntryRepository.findAll(
                buildSpec(customerId, workerId, fromDate, toDate, entryType), pageable);
        List<DebtEntryResponse> items = result.getContent().stream()
                .map(DebtEntryResponse::fromEntity)
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
    public DebtEntryResponse getById(long id) {
        return DebtEntryResponse.fromEntity(findEntry(id));
    }

    @Override
    @Transactional
    public DebtEntryResponse create(CreateDebtEntryRequest request) {
        return createInternal(
                request.customerId(),
                request.workerId(),
                request.entryType(),
                request.direction(),
                request.amount(),
                request.entryDate(),
                request.note(),
                DebtRefType.MANUAL,
                null,
                true);
    }

    @Override
    @Transactional
    public DebtEntryResponse createInternal(
            Long customerId,
            Long workerId,
            DebtEntryType entryType,
            LedgerDirection direction,
            BigDecimal amount,
            LocalDate entryDate,
            String note,
            DebtRefType refType,
            Long refId,
            boolean postCash) {
        boolean hasCustomer = customerId != null;
        boolean hasWorker = workerId != null;
        if (hasCustomer == hasWorker) {
            throw new InvalidRequestException("Phải chọn đúng một trong customerId hoặc workerId");
        }

        LedgerDirection resolvedDirection = resolveDirection(entryType, direction);
        Customer customer = null;
        Worker worker = null;
        if (hasCustomer) {
            customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
            applyCache(customer, null, resolvedDirection, amount);
            customerRepository.save(customer);
        } else {
            worker = workerRepository.findById(workerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Worker", "id", workerId));
            applyCache(null, worker, resolvedDirection, amount);
            workerRepository.save(worker);
        }

        DebtEntry entry = new DebtEntry();
        entry.setCustomer(customer);
        entry.setWorker(worker);
        entry.setEntryType(entryType);
        entry.setDirection(resolvedDirection);
        entry.setAmount(amount);
        entry.setEntryDate(entryDate);
        entry.setNote(blankToNull(note));
        entry.setRefType(refType);
        entry.setRefId(refId);
        entry.setCreatedBy(actorResolver.requireActor());
        DebtEntry saved = debtEntryRepository.save(entry);

        if (postCash) {
            maybePostCash(saved);
        }
        return DebtEntryResponse.fromEntity(saved);
    }

    private void maybePostCash(DebtEntry entry) {
        if (entry.getCustomer() != null && entry.getEntryType() == DebtEntryType.PAYMENT) {
            cashLedgerWriter.postByCategoryCode(
                    entry.getEntryDate(),
                    CashDirection.IN,
                    entry.getAmount(),
                    "CUSTOMER_REPAY",
                    "Thu no khach #" + entry.getId(),
                    CashRefType.CUSTOMER_PAYMENT,
                    entry.getId());
        } else if (entry.getWorker() != null && entry.getEntryType() == DebtEntryType.CHARGE) {
            cashLedgerWriter.postByCategoryCode(
                    entry.getEntryDate(),
                    CashDirection.OUT,
                    entry.getAmount(),
                    "WORKER_ADVANCE",
                    "Ung tho #" + entry.getId(),
                    CashRefType.WORKER_ADVANCE,
                    entry.getId());
        }
    }

    private LedgerDirection resolveDirection(DebtEntryType entryType, LedgerDirection direction) {
        return switch (entryType) {
            case CHARGE -> LedgerDirection.INCREASE;
            case PAYMENT -> LedgerDirection.DECREASE;
            case ADJUST -> {
                if (direction == null) {
                    throw new InvalidRequestException("ADJUST cần direction INCREASE hoặc DECREASE");
                }
                yield direction;
            }
        };
    }

    private void applyCache(Customer customer, Worker worker, LedgerDirection direction, BigDecimal amount) {
        BigDecimal delta = direction == LedgerDirection.INCREASE ? amount : amount.negate();
        if (customer != null) {
            customer.setCurrentDebt(customer.getCurrentDebt().add(delta));
        } else {
            worker.setCurrentAdvance(worker.getCurrentAdvance().add(delta));
        }
    }

    private Specification<DebtEntry> buildSpec(
            Long customerId, Long workerId,
            LocalDate fromDate, LocalDate toDate,
            DebtEntryType entryType) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (customerId != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), customerId));
            }
            if (workerId != null) {
                predicates.add(cb.equal(root.get("worker").get("id"), workerId));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("entryDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("entryDate"), toDate));
            }
            if (entryType != null) {
                predicates.add(cb.equal(root.get("entryType"), entryType));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private DebtEntry findEntry(long id) {
        return debtEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DebtEntry", "id", id));
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
            return Sort.by("entryDate").descending();
        }
        String[] parts = sort.split(",");
        if (parts.length == 2) {
            return Sort.by(Sort.Direction.fromString(parts[1].trim()), parts[0].trim());
        }
        return Sort.by("entryDate").descending();
    }
}
