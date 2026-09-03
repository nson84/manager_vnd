package Manager_vnd.Manager.feature.wage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import Manager_vnd.Manager.exception.ConflictException;
import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.payslip.Payslip;
import Manager_vnd.Manager.feature.user.User;
import Manager_vnd.Manager.feature.wage.dto.CreateWageEntryRequest;
import Manager_vnd.Manager.feature.worker.WageType;
import Manager_vnd.Manager.feature.worker.Worker;
import Manager_vnd.Manager.feature.worker.WorkerRepository;

@ExtendWith(MockitoExtension.class)
class WageEntryServiceImplTest {

    @Mock
    private WageEntryRepository wageEntryRepository;
    @Mock
    private WorkerRepository workerRepository;
    @Mock
    private ActorResolver actorResolver;

    @InjectMocks
    private WageEntryServiceImpl wageEntryService;

    @Test
    @DisplayName("Tạo ghi công tính amount = qty * rate")
    void create_success() {
        Worker worker = worker(1L);
        User actor = new User();
        actor.setId(1L);
        when(workerRepository.findById(1L)).thenReturn(Optional.of(worker));
        when(actorResolver.requireActor()).thenReturn(actor);
        when(wageEntryRepository.save(any(WageEntry.class))).thenAnswer(inv -> {
            WageEntry e = inv.getArgument(0);
            e.setId(5L);
            return e;
        });

        var result = wageEntryService.create(new CreateWageEntryRequest(
                1L, LocalDate.of(2026, 9, 1), null,
                new BigDecimal("2"), null, null));

        assertEquals(new BigDecimal("700000.00"), result.amount());
        assertEquals(WageType.DAILY, result.wageType());
    }

    @Test
    @DisplayName("Xóa khi đã gắn payslip bị conflict")
    void delete_linkedPayslip_conflict() {
        WageEntry entry = new WageEntry();
        entry.setId(1L);
        entry.setPayslip(new Payslip());
        when(wageEntryRepository.findById(1L)).thenReturn(Optional.of(entry));

        assertThrows(ConflictException.class, () -> wageEntryService.delete(1L));
        verify(wageEntryRepository, never()).delete(any(WageEntry.class));
    }

    @Test
    @DisplayName("Get by id not found")
    void getById_notFound() {
        when(wageEntryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> wageEntryService.getById(99L));
    }

    private Worker worker(long id) {
        Worker w = new Worker();
        w.setId(id);
        w.setName("Tho");
        w.setWageType(WageType.DAILY);
        w.setDefaultUnitRate(new BigDecimal("350000"));
        return w;
    }
}
