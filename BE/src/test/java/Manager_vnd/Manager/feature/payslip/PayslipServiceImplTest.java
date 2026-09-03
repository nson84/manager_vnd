package Manager_vnd.Manager.feature.payslip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
import Manager_vnd.Manager.exception.InvalidRequestException;
import Manager_vnd.Manager.feature.cashbook.CashDirection;
import Manager_vnd.Manager.feature.cashbook.CashLedgerWriter;
import Manager_vnd.Manager.feature.cashbook.CashRefType;
import Manager_vnd.Manager.feature.debt.DebtEntryService;
import Manager_vnd.Manager.feature.debt.DebtEntryType;
import Manager_vnd.Manager.feature.debt.DebtRefType;
import Manager_vnd.Manager.feature.debt.LedgerDirection;
import Manager_vnd.Manager.feature.payslip.dto.CreatePayslipRequest;
import Manager_vnd.Manager.feature.user.User;
import Manager_vnd.Manager.feature.wage.WageEntry;
import Manager_vnd.Manager.feature.wage.WageEntryRepository;
import Manager_vnd.Manager.feature.worker.Worker;
import Manager_vnd.Manager.feature.worker.WorkerRepository;

@ExtendWith(MockitoExtension.class)
class PayslipServiceImplTest {

    @Mock
    private PayslipRepository payslipRepository;
    @Mock
    private WorkerRepository workerRepository;
    @Mock
    private WageEntryRepository wageEntryRepository;
    @Mock
    private ActorResolver actorResolver;
    @Mock
    private CashLedgerWriter cashLedgerWriter;
    @Mock
    private DebtEntryService debtEntryService;

    @InjectMocks
    private PayslipServiceImpl payslipService;

    @Test
    @DisplayName("Tạo DRAFT gộp công và tính net")
    void create_success() {
        Worker worker = worker(1L, new BigDecimal("100000"));
        WageEntry wage = new WageEntry();
        wage.setAmount(new BigDecimal("700000"));
        User actor = new User();
        actor.setId(1L);

        when(workerRepository.findById(1L)).thenReturn(Optional.of(worker));
        when(wageEntryRepository.findUnpaidInPeriod(1L, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 7)))
                .thenReturn(List.of(wage));
        when(actorResolver.requireActor()).thenReturn(actor);
        when(payslipRepository.save(any(Payslip.class))).thenAnswer(inv -> {
            Payslip p = inv.getArgument(0);
            p.setId(3L);
            return p;
        });

        var result = payslipService.create(new CreatePayslipRequest(
                1L, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 7),
                new BigDecimal("50000"), BigDecimal.ZERO, null));

        assertEquals(PayslipStatus.DRAFT, result.status());
        assertEquals(new BigDecimal("650000"), result.netAmount());
        verify(wageEntryRepository).saveAll(any());
    }

    @Test
    @DisplayName("Không có công trong kỳ")
    void create_noWages_invalid() {
        Worker worker = worker(1L, BigDecimal.ZERO);
        when(workerRepository.findById(1L)).thenReturn(Optional.of(worker));
        when(wageEntryRepository.findUnpaidInPeriod(anyLong(), any(), any())).thenReturn(List.of());

        assertThrows(InvalidRequestException.class, () -> payslipService.create(
                new CreatePayslipRequest(1L, LocalDate.now(), LocalDate.now(), null, null, null)));
    }

    @Test
    @DisplayName("Pay ghi quỹ và trừ ứng")
    void pay_success() {
        Worker worker = worker(1L, new BigDecimal("100000"));
        Payslip payslip = new Payslip();
        payslip.setId(3L);
        payslip.setWorker(worker);
        payslip.setPeriodEnd(LocalDate.of(2026, 9, 7));
        payslip.setGrossAmount(new BigDecimal("700000"));
        payslip.setAdvanceDeducted(new BigDecimal("50000"));
        payslip.setOtherDeduction(BigDecimal.ZERO);
        payslip.setNetAmount(new BigDecimal("650000"));
        payslip.setStatus(PayslipStatus.CONFIRMED);
        User actor = new User();
        actor.setId(1L);
        payslip.setCreatedBy(actor);

        when(payslipRepository.findById(3L)).thenReturn(Optional.of(payslip));
        when(payslipRepository.save(payslip)).thenReturn(payslip);
        when(debtEntryService.createInternal(
                isNull(), eq(1L), eq(DebtEntryType.PAYMENT), eq(LedgerDirection.DECREASE),
                eq(new BigDecimal("50000")), any(), any(), eq(DebtRefType.PAYSLIP), eq(3L), eq(false)))
                .thenReturn(null);

        assertEquals(PayslipStatus.PAID, payslipService.pay(3L).status());
        verify(cashLedgerWriter).postByCategoryCode(
                eq(LocalDate.of(2026, 9, 7)),
                eq(CashDirection.OUT),
                eq(new BigDecimal("650000")),
                eq("WAGE"),
                eq("Tra luong #3"),
                eq(CashRefType.PAYSLIP),
                eq(3L));
    }

    private Worker worker(long id, BigDecimal advance) {
        Worker w = new Worker();
        w.setId(id);
        w.setName("Tho");
        w.setCurrentAdvance(advance);
        return w;
    }
}
