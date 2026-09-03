package Manager_vnd.Manager.feature.cashbook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import Manager_vnd.Manager.config.ActorResolver;
import Manager_vnd.Manager.exception.ConflictException;
import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.cashbook.dto.CreateManualCashEntryRequest;
import Manager_vnd.Manager.feature.cashbook.dto.UpdateCashCheckedRequest;
import Manager_vnd.Manager.feature.expense.ExpenseCategory;
import Manager_vnd.Manager.feature.expense.ExpenseCategoryRepository;
import Manager_vnd.Manager.feature.user.User;
import Manager_vnd.Manager.util.VietnamTime;

@ExtendWith(MockitoExtension.class)
class CashbookServiceImplTest {

    @Mock
    private CashEntryRepository cashEntryRepository;

    @Mock
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Mock
    private ActorResolver actorResolver;

    @Mock
    private VietnamTime vietnamTime;

    @Mock
    private CashbookPdfExporter pdfExporter;

    @InjectMocks
    private CashbookServiceImpl cashbookService;

    @Test
    @DisplayName("Tạo phiếu MANUAL thành công")
    void createManual_success() {
        CreateManualCashEntryRequest request = new CreateManualCashEntryRequest(
                LocalDate.of(2026, 9, 3), CashDirection.OUT, new BigDecimal("100000"), 1L, "Chi van phong", null);
        ExpenseCategory category = new ExpenseCategory();
        category.setId(1L);
        category.setCode("OTHER");
        category.setName("Khac");
        User user = new User();
        user.setId(1L);
        user.setName("Admin");

        when(expenseCategoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(actorResolver.requireActor()).thenReturn(user);
        when(cashEntryRepository.save(any(CashEntry.class))).thenAnswer(inv -> {
            CashEntry e = inv.getArgument(0);
            e.setId(10L);
            return e;
        });

        var result = cashbookService.createManual(request);

        assertEquals(10L, result.id());
        assertEquals(CashRefType.MANUAL, result.refType());
        assertEquals(CashDirection.OUT, result.direction());
    }

    @Test
    @DisplayName("Xóa dòng không phải MANUAL bị Conflict")
    void deleteManual_systemEntry_throwsConflict() {
        CashEntry entry = new CashEntry();
        entry.setId(5L);
        entry.setRefType(CashRefType.PAYSLIP);
        when(cashEntryRepository.findById(5L)).thenReturn(Optional.of(entry));

        assertThrows(ConflictException.class, () -> cashbookService.deleteManual(5L));
        verify(cashEntryRepository, never()).delete(any(CashEntry.class));
    }

    @Test
    @DisplayName("Xóa MANUAL thành công")
    void deleteManual_success() {
        CashEntry entry = new CashEntry();
        entry.setId(5L);
        entry.setRefType(CashRefType.MANUAL);
        when(cashEntryRepository.findById(5L)).thenReturn(Optional.of(entry));

        cashbookService.deleteManual(5L);

        verify(cashEntryRepository).delete(entry);
    }

    @Test
    @DisplayName("Toggle checked cập nhật trạng thái")
    void updateChecked_true_setsFlags() {
        CashEntry entry = new CashEntry();
        entry.setId(3L);
        entry.setChecked(false);
        ExpenseCategory category = new ExpenseCategory();
        category.setId(1L);
        category.setCode("OTHER");
        category.setName("Khac");
        entry.setCategory(category);
        User user = new User();
        user.setId(1L);
        user.setName("Admin");
        entry.setCreatedBy(user);
        entry.setRefType(CashRefType.MANUAL);
        entry.setDirection(CashDirection.IN);
        entry.setAmount(BigDecimal.TEN);
        entry.setEntryDate(LocalDate.now());

        when(cashEntryRepository.findWithDetailsById(3L)).thenReturn(Optional.of(entry));
        when(actorResolver.requireActor()).thenReturn(user);
        when(cashEntryRepository.save(entry)).thenReturn(entry);

        var result = cashbookService.updateChecked(3L, new UpdateCashCheckedRequest(true));

        assertEquals(true, result.checked());
    }

    @Test
    @DisplayName("Get by id không tồn tại")
    void getById_notFound() {
        when(cashEntryRepository.findWithDetailsById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cashbookService.getById(99L));
    }

    @Test
    @DisplayName("Stats tính balance = in - out")
    void getStats_computesBalance() {
        when(vietnamTime.firstDayOfMonth()).thenReturn(LocalDate.of(2026, 9, 1));
        when(vietnamTime.today()).thenReturn(LocalDate.of(2026, 9, 3));
        when(cashEntryRepository.aggregateByDirection(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(
                        new Object[]{CashDirection.IN, new BigDecimal("1000"), 2L},
                        new Object[]{CashDirection.OUT, new BigDecimal("400"), 1L}));
        when(cashEntryRepository.aggregateByCategory(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        var stats = cashbookService.getStats(new CashEntryFilter(
                null, null, null, null, null, null, null, null, null, null, null));

        assertEquals(new BigDecimal("1000"), stats.totalIn());
        assertEquals(new BigDecimal("400"), stats.totalOut());
        assertEquals(new BigDecimal("600"), stats.balance());
    }
}
