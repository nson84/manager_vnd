package Manager_vnd.Manager.feature.cashbook;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Manager_vnd.Manager.config.ActorResolver;
import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.dto.PaginationMeta;
import Manager_vnd.Manager.exception.ConflictException;
import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.cashbook.dto.CashEntryResponse;
import Manager_vnd.Manager.feature.cashbook.dto.CashStatsResponse;
import Manager_vnd.Manager.feature.cashbook.dto.CategorySummary;
import Manager_vnd.Manager.feature.cashbook.dto.CreateManualCashEntryRequest;
import Manager_vnd.Manager.feature.cashbook.dto.UpdateCashCheckedRequest;
import Manager_vnd.Manager.feature.cashbook.dto.UpdateCashNoteRequest;
import Manager_vnd.Manager.feature.expense.ExpenseCategory;
import Manager_vnd.Manager.feature.expense.ExpenseCategoryRepository;
import Manager_vnd.Manager.feature.user.User;
import Manager_vnd.Manager.util.VietnamTime;

@Service
public class CashbookServiceImpl implements CashbookService {

    private final CashEntryRepository cashEntryRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ActorResolver actorResolver;
    private final VietnamTime vietnamTime;
    private final CashbookPdfExporter pdfExporter;

    public CashbookServiceImpl(
            CashEntryRepository cashEntryRepository,
            ExpenseCategoryRepository expenseCategoryRepository,
            ActorResolver actorResolver,
            VietnamTime vietnamTime,
            CashbookPdfExporter pdfExporter) {
        this.cashEntryRepository = cashEntryRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.actorResolver = actorResolver;
        this.vietnamTime = vietnamTime;
        this.pdfExporter = pdfExporter;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<CashEntryResponse> getEntries(CashEntryFilter filter, int page, int size, String sort) {
        CashEntryFilter applied = applyDefaultDates(filter);
        Pageable pageable = toPageable(page, size, sort);
        Page<CashEntry> result = cashEntryRepository.findAll(CashEntrySpecs.withFilters(applied), pageable);
        List<CashEntryResponse> items = result.getContent().stream()
                .map(CashEntryResponse::fromEntity)
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
    public CashStatsResponse getStats(CashEntryFilter filter) {
        CashEntryFilter applied = applyDefaultDates(filter);
        BigDecimal totalIn = BigDecimal.ZERO;
        BigDecimal totalOut = BigDecimal.ZERO;
        long countIn = 0;
        long countOut = 0;

        List<Object[]> byDirection = cashEntryRepository.aggregateByDirection(
                applied.fromDate(), applied.toDate(), applied.direction(), applied.categoryId(),
                applied.refType(), applied.refId(), applied.createdBy(), applied.checked(),
                applied.amountMin(), applied.amountMax(), blankToNull(applied.q()));

        for (Object[] row : byDirection) {
            CashDirection direction = (CashDirection) row[0];
            BigDecimal sum = (BigDecimal) row[1];
            long count = ((Number) row[2]).longValue();
            if (direction == CashDirection.IN) {
                totalIn = sum;
                countIn = count;
            } else if (direction == CashDirection.OUT) {
                totalOut = sum;
                countOut = count;
            }
        }

        List<CashStatsResponse.CategoryStat> byCategory = new ArrayList<>();
        List<Object[]> categoryRows = cashEntryRepository.aggregateByCategory(
                applied.fromDate(), applied.toDate(), applied.direction(), applied.categoryId(),
                applied.refType(), applied.refId(), applied.createdBy(), applied.checked(),
                applied.amountMin(), applied.amountMax(), blankToNull(applied.q()));
        for (Object[] row : categoryRows) {
            byCategory.add(new CashStatsResponse.CategoryStat(
                    ((Number) row[0]).longValue(),
                    (String) row[1],
                    (CashDirection) row[2],
                    (BigDecimal) row[3]));
        }

        return new CashStatsResponse(
                totalIn,
                totalOut,
                totalIn.subtract(totalOut),
                countIn,
                countOut,
                byCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CashEntryResponse getById(long id) {
        return CashEntryResponse.fromEntity(findDetailed(id));
    }

    @Override
    @Transactional
    public CashEntryResponse createManual(CreateManualCashEntryRequest request) {
        ExpenseCategory category = expenseCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseCategory", "id", request.categoryId()));
        User actor = actorResolver.requireActor();

        CashEntry entry = new CashEntry();
        entry.setEntryDate(request.entryDate() != null ? request.entryDate() : vietnamTime.today());
        entry.setDirection(request.direction());
        entry.setAmount(request.amount());
        entry.setCategory(category);
        entry.setDescription(request.description());
        entry.setNote(request.note());
        entry.setChecked(false);
        entry.setRefType(CashRefType.MANUAL);
        entry.setRefId(null);
        entry.setCreatedBy(actor);

        return CashEntryResponse.fromEntity(cashEntryRepository.save(entry));
    }

    @Override
    @Transactional
    public CashEntryResponse updateNote(long id, UpdateCashNoteRequest request) {
        CashEntry entry = findDetailed(id);
        entry.setNote(request.note());
        return CashEntryResponse.fromEntity(cashEntryRepository.save(entry));
    }

    @Override
    @Transactional
    public CashEntryResponse updateChecked(long id, UpdateCashCheckedRequest request) {
        CashEntry entry = findDetailed(id);
        boolean checked = Boolean.TRUE.equals(request.checked());
        entry.setChecked(checked);
        if (checked) {
            entry.setCheckedAt(Instant.now());
            entry.setCheckedBy(actorResolver.requireActor());
        } else {
            entry.setCheckedAt(null);
            entry.setCheckedBy(null);
        }
        return CashEntryResponse.fromEntity(cashEntryRepository.save(entry));
    }

    @Override
    @Transactional
    public void deleteManual(long id) {
        CashEntry entry = cashEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CashEntry", "id", id));
        if (entry.getRefType() != CashRefType.MANUAL) {
            throw new ConflictException("Chỉ được xóa phiếu MANUAL");
        }
        cashEntryRepository.delete(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportPdf(CashEntryFilter filter) {
        CashEntryFilter applied = applyDefaultDates(filter);
        List<CashEntry> entries = cashEntryRepository.findAll(
                CashEntrySpecs.withFilters(applied),
                Sort.by(Sort.Direction.DESC, "entryDate", "id"));
        CashStatsResponse stats = getStats(applied);
        String exportedAt = vietnamTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return pdfExporter.export(entries, stats, applied.fromDate(), applied.toDate(), exportedAt);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySummary> listCategories() {
        return expenseCategoryRepository.findAll(Sort.by("sortOrder", "name")).stream()
                .map(c -> new CategorySummary(c.getId(), c.getCode(), c.getName()))
                .toList();
    }

    private CashEntry findDetailed(long id) {
        return cashEntryRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CashEntry", "id", id));
    }

    private CashEntryFilter applyDefaultDates(CashEntryFilter filter) {
        var from = filter.fromDate() != null ? filter.fromDate() : vietnamTime.firstDayOfMonth();
        var to = filter.toDate() != null ? filter.toDate() : vietnamTime.today();
        return new CashEntryFilter(
                from,
                to,
                filter.direction(),
                filter.categoryId(),
                filter.refType(),
                filter.refId(),
                filter.createdBy(),
                filter.checked(),
                filter.amountMin(),
                filter.amountMax(),
                filter.q());
    }

    private Pageable toPageable(int page, int size, String sort) {
        int zeroBased = Math.max(page - 1, 0);
        return PageRequest.of(zeroBased, size, parseSort(sort));
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "entryDate", "id");
        }
        String[] parts = sort.split(",");
        if (parts.length == 2) {
            return Sort.by(Sort.Direction.fromString(parts[1].trim()), parts[0].trim());
        }
        return Sort.by(Sort.Direction.DESC, "entryDate", "id");
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
