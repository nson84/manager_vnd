package Manager_vnd.Manager.feature.expense;

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
import Manager_vnd.Manager.exception.InvalidRequestException;
import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.cashbook.CashDirection;
import Manager_vnd.Manager.feature.cashbook.CashLedgerWriter;
import Manager_vnd.Manager.feature.cashbook.CashRefType;
import Manager_vnd.Manager.feature.expense.dto.CreateExpenseRequest;
import Manager_vnd.Manager.feature.expense.dto.ExpenseResponse;
import jakarta.persistence.criteria.Predicate;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ActorResolver actorResolver;
    private final CashLedgerWriter cashLedgerWriter;

    public ExpenseServiceImpl(
            ExpenseRepository expenseRepository,
            ExpenseCategoryRepository expenseCategoryRepository,
            ActorResolver actorResolver,
            CashLedgerWriter cashLedgerWriter) {
        this.expenseRepository = expenseRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.actorResolver = actorResolver;
        this.cashLedgerWriter = cashLedgerWriter;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<ExpenseResponse> getAll(
            int page, int size, String sort,
            Long categoryId, ExpenseStatus status,
            LocalDate fromDate, LocalDate toDate) {
        Pageable pageable = toPageable(page, size, sort);
        Page<Expense> result = expenseRepository.findAll(
                buildSpec(categoryId, status, fromDate, toDate), pageable);
        List<ExpenseResponse> items = result.getContent().stream()
                .map(ExpenseResponse::fromEntity)
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
    public ExpenseResponse getById(long id) {
        return ExpenseResponse.fromEntity(findExpense(id));
    }

    @Override
    @Transactional
    public ExpenseResponse create(CreateExpenseRequest request) {
        ExpenseCategory category = expenseCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseCategory", "id", request.categoryId()));
        if ("WAGE".equalsIgnoreCase(category.getCode())) {
            throw new InvalidRequestException("Không dùng category WAGE cho phiếu chi — dùng Payslip");
        }

        Expense expense = new Expense();
        expense.setCategory(category);
        expense.setAmount(request.amount());
        expense.setExpenseDate(request.expenseDate());
        expense.setNote(blankToNull(request.note()));
        expense.setStatus(ExpenseStatus.POSTED);
        expense.setCreatedBy(actorResolver.requireActor());
        Expense saved = expenseRepository.save(expense);

        cashLedgerWriter.post(
                saved.getExpenseDate(),
                CashDirection.OUT,
                saved.getAmount(),
                category,
                "Phieu chi #" + saved.getId(),
                CashRefType.EXPENSE,
                saved.getId());

        return ExpenseResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public ExpenseResponse cancel(long id) {
        Expense expense = findExpense(id);
        if (expense.getStatus() == ExpenseStatus.CANCELLED) {
            throw new ConflictException("Phiếu chi đã hủy");
        }
        expense.setStatus(ExpenseStatus.CANCELLED);
        Expense saved = expenseRepository.save(expense);

        cashLedgerWriter.post(
                saved.getExpenseDate(),
                CashDirection.IN,
                saved.getAmount(),
                saved.getCategory(),
                "Huy phieu chi #" + saved.getId(),
                CashRefType.EXPENSE,
                saved.getId());

        return ExpenseResponse.fromEntity(saved);
    }

    private Specification<Expense> buildSpec(
            Long categoryId, ExpenseStatus status, LocalDate fromDate, LocalDate toDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("expenseDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("expenseDate"), toDate));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Expense findExpense(long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
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
            return Sort.by("expenseDate").descending();
        }
        String[] parts = sort.split(",");
        if (parts.length == 2) {
            return Sort.by(Sort.Direction.fromString(parts[1].trim()), parts[0].trim());
        }
        return Sort.by("expenseDate").descending();
    }
}
