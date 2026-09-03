package Manager_vnd.Manager.feature.worker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import Manager_vnd.Manager.exception.ConflictException;
import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.worker.dto.CreateWorkerRequest;
import Manager_vnd.Manager.feature.worker.dto.UpdateWorkerRequest;

@ExtendWith(MockitoExtension.class)
class WorkerServiceImplTest {

    @Mock
    private WorkerRepository workerRepository;

    @InjectMocks
    private WorkerServiceImpl workerService;

    @Test
    @DisplayName("Tạo thợ thành công")
    void create_success() {
        CreateWorkerRequest request = new CreateWorkerRequest(
                "Tho A", "0901111222", null, "Thợ hồ", WageType.DAILY,
                new BigDecimal("350000"), null, null);
        when(workerRepository.existsByPhone("0901111222")).thenReturn(false);
        when(workerRepository.save(any(Worker.class))).thenAnswer(inv -> {
            Worker w = inv.getArgument(0);
            w.setId(1L);
            return w;
        });

        var result = workerService.create(request);

        assertEquals(1L, result.id());
        assertTrue(result.active());
        assertEquals(BigDecimal.ZERO, result.currentAdvance());
    }

    @Test
    @DisplayName("Tạo thợ trùng SĐT bị conflict")
    void create_duplicatePhone_throwsConflict() {
        when(workerRepository.existsByPhone("0901111222")).thenReturn(true);
        assertThrows(ConflictException.class, () -> workerService.create(
                new CreateWorkerRequest("A", "0901111222", null, null, WageType.HOURLY,
                        new BigDecimal("50000"), null, null)));
        verify(workerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Disable soft")
    void disable_success() {
        Worker worker = build(1L, true);
        when(workerRepository.findById(1L)).thenReturn(Optional.of(worker));
        when(workerRepository.save(worker)).thenReturn(worker);

        assertFalse(workerService.disable(1L).active());
        verify(workerRepository, never()).delete(any(Worker.class));
    }

    @Test
    @DisplayName("Get by id không tìm thấy")
    void getById_notFound() {
        when(workerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> workerService.getById(99L));
    }

    @Test
    @DisplayName("Update tên thành công")
    void update_success() {
        Worker worker = build(1L, true);
        when(workerRepository.findById(1L)).thenReturn(Optional.of(worker));
        when(workerRepository.save(worker)).thenReturn(worker);

        var result = workerService.update(new UpdateWorkerRequest(
                1L, "Tho B", null, null, null, null, null, null, null));
        assertEquals("Tho B", result.name());
    }

    private Worker build(long id, boolean active) {
        Worker w = new Worker();
        w.setId(id);
        w.setName("Tho");
        w.setWageType(WageType.DAILY);
        w.setDefaultUnitRate(new BigDecimal("300000"));
        w.setActive(active);
        w.setCurrentAdvance(BigDecimal.ZERO);
        return w;
    }
}
