package Manager_vnd.Manager.feature.debt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import Manager_vnd.Manager.feature.customer.Customer;
import Manager_vnd.Manager.feature.customer.CustomerRepository;
import Manager_vnd.Manager.feature.debt.dto.CreateDebtEntryRequest;
import Manager_vnd.Manager.feature.user.User;
import Manager_vnd.Manager.feature.worker.WorkerRepository;

@ExtendWith(MockitoExtension.class)
class DebtEntryServiceImplTest {

    @Mock
    private DebtEntryRepository debtEntryRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private WorkerRepository workerRepository;
    @Mock
    private ActorResolver actorResolver;
    @Mock
    private CashLedgerWriter cashLedgerWriter;

    @InjectMocks
    private DebtEntryServiceImpl debtEntryService;

    @Test
    @DisplayName("Khách trả nợ cập nhật cache và ghi quỹ IN")
    void create_customerPayment_postsCash() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("KH");
        customer.setCurrentDebt(new BigDecimal("500000"));
        User actor = new User();
        actor.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(actorResolver.requireActor()).thenReturn(actor);
        when(debtEntryRepository.save(any(DebtEntry.class))).thenAnswer(inv -> {
            DebtEntry e = inv.getArgument(0);
            e.setId(10L);
            return e;
        });

        var result = debtEntryService.create(new CreateDebtEntryRequest(
                1L, null, DebtEntryType.PAYMENT, null,
                new BigDecimal("200000"), LocalDate.of(2026, 9, 3), "Tra no"));

        assertEquals(new BigDecimal("300000"), customer.getCurrentDebt());
        assertEquals(DebtEntryType.PAYMENT, result.entryType());
        verify(cashLedgerWriter).postByCategoryCode(
                eq(LocalDate.of(2026, 9, 3)),
                eq(CashDirection.IN),
                eq(new BigDecimal("200000")),
                eq("CUSTOMER_REPAY"),
                anyString(),
                eq(CashRefType.CUSTOMER_PAYMENT),
                eq(10L));
    }

    @Test
    @DisplayName("Thiếu XOR customer/worker")
    void create_bothIds_invalid() {
        assertThrows(InvalidRequestException.class, () -> debtEntryService.create(
                new CreateDebtEntryRequest(1L, 2L, DebtEntryType.CHARGE, null,
                        BigDecimal.TEN, LocalDate.now(), null)));
    }
}
