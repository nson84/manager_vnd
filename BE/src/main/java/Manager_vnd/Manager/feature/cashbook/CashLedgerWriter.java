package Manager_vnd.Manager.feature.cashbook;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Manager_vnd.Manager.config.ActorResolver;
import Manager_vnd.Manager.exception.InvalidRequestException;
import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.expense.ExpenseCategory;
import Manager_vnd.Manager.feature.expense.ExpenseCategoryRepository;
import Manager_vnd.Manager.feature.user.User;

@Service
public class CashLedgerWriter {

    private final CashEntryRepository cashEntryRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ActorResolver actorResolver;

    public CashLedgerWriter(
            CashEntryRepository cashEntryRepository,
            ExpenseCategoryRepository expenseCategoryRepository,
            ActorResolver actorResolver) {
        this.cashEntryRepository = cashEntryRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.actorResolver = actorResolver;
    }

    @Transactional
    public CashEntry post(
            LocalDate entryDate,
            CashDirection direction,
            BigDecimal amount,
            ExpenseCategory category,
            String description,
            CashRefType refType,
            Long refId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRequestException("Số tiền sổ quỹ phải > 0");
        }
        User actor = actorResolver.requireActor();
        CashEntry entry = new CashEntry();
        entry.setEntryDate(entryDate);
        entry.setDirection(direction);
        entry.setAmount(amount);
        entry.setCategory(category);
        entry.setDescription(description);
        entry.setChecked(false);
        entry.setRefType(refType);
        entry.setRefId(refId);
        entry.setCreatedBy(actor);
        return cashEntryRepository.save(entry);
    }

    @Transactional
    public CashEntry postByCategoryCode(
            LocalDate entryDate,
            CashDirection direction,
            BigDecimal amount,
            String categoryCode,
            String description,
            CashRefType refType,
            Long refId) {
        ExpenseCategory category = expenseCategoryRepository.findByCode(categoryCode)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseCategory", "code", categoryCode));
        return post(entryDate, direction, amount, category, description, refType, refId);
    }

    @Transactional
    public CashEntry postWithCategoryId(
            LocalDate entryDate,
            CashDirection direction,
            BigDecimal amount,
            long categoryId,
            String description,
            CashRefType refType,
            Long refId) {
        ExpenseCategory category = expenseCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseCategory", "id", categoryId));
        return post(entryDate, direction, amount, category, description, refType, refId);
    }

    @Transactional(readOnly = true)
    public List<CashEntry> findByRef(CashRefType refType, long refId) {
        return cashEntryRepository.findByRefTypeAndRefId(refType, refId);
    }

    @Transactional
    public void reverseAll(CashRefType refType, long refId, String descriptionPrefix) {
        for (CashEntry original : findByRef(refType, refId)) {
            CashDirection reverse = original.getDirection() == CashDirection.IN
                    ? CashDirection.OUT
                    : CashDirection.IN;
            post(
                    original.getEntryDate(),
                    reverse,
                    original.getAmount(),
                    original.getCategory(),
                    descriptionPrefix + " #" + refId,
                    refType,
                    refId);
        }
    }
}
