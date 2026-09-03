package Manager_vnd.Manager.feature.expense;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import Manager_vnd.Manager.config.ActorResolver;
import Manager_vnd.Manager.exception.InvalidRequestException;
import Manager_vnd.Manager.feature.cashbook.CashDirection;
import Manager_vnd.Manager.feature.cashbook.CashLedgerWriter;
import Manager_vnd.Manager.feature.cashbook.CashRefType;
import Manager_vnd.Manager.feature.expense.dto.CreateExpenseRequest;
import Manager_vnd.Manager.feature.user.User;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private ExpenseCategoryRepository expenseCategoryRepository;
    @Mock
    private ActorResolver actorResolver;
    @Mock
    private CashLedgerWriter cashLedgerWriter;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @Test
    @DisplayName("Tạo phiếu chi POSTED + quỹ OUT")
    void create_success() {
        ExpenseCategory category = new ExpenseCategory();
        category.setId(2L);
        category.setCode("RENT");
        category.setName("Thue");
        User actor = new User();
        actor.setId(1L);

        when(expenseCategoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(actorResolver.requireActor()).thenReturn(actor);
        when(expenseRepository.save(any(Expense.class))).thenAnswer(inv -> {
            Expense e = inv.getArgument(0);
            e.setId(7L);
            return e;
        });

        var result = expenseService.create(new CreateExpenseRequest(
                2L, new BigDecimal("1000000"), LocalDate.of(2026, 9, 1), "Thue nha"));

        assertEquals(ExpenseStatus.POSTED, result.status());
        verify(cashLedgerWriter).post(
                eq(LocalDate.of(2026, 9, 1)),
                eq(CashDirection.OUT),
                eq(new BigDecimal("1000000")),
                eq(category),
                eq("Phieu chi #7"),
                eq(CashRefType.EXPENSE),
                eq(7L));
    }

    @Test
    @DisplayName("Cấm category WAGE")
    void create_wageCategory_invalid() {
        ExpenseCategory category = new ExpenseCategory();
        category.setId(1L);
        category.setCode("WAGE");
        when(expenseCategoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(InvalidRequestException.class, () -> expenseService.create(
                new CreateExpenseRequest(1L, BigDecimal.TEN, LocalDate.now(), null)));
    }

    @Test
    @DisplayName("Hủy phiếu chi ghi quỹ IN")
    void cancel_success() {
        ExpenseCategory category = new ExpenseCategory();
        category.setId(2L);
        category.setCode("RENT");
        Expense expense = new Expense();
        expense.setId(7L);
        expense.setCategory(category);
        expense.setAmount(new BigDecimal("1000000"));
        expense.setExpenseDate(LocalDate.of(2026, 9, 1));
        expense.setStatus(ExpenseStatus.POSTED);
        User actor = new User();
        actor.setId(1L);
        expense.setCreatedBy(actor);

        when(expenseRepository.findById(7L)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(expense)).thenReturn(expense);

        assertEquals(ExpenseStatus.CANCELLED, expenseService.cancel(7L).status());
        verify(cashLedgerWriter).post(
                eq(LocalDate.of(2026, 9, 1)),
                eq(CashDirection.IN),
                eq(new BigDecimal("1000000")),
                eq(category),
                eq("Huy phieu chi #7"),
                eq(CashRefType.EXPENSE),
                eq(7L));
    }
}
