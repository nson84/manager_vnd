package Manager_vnd.Manager.feature.customer;

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
import Manager_vnd.Manager.feature.customer.dto.CreateCustomerRequest;
import Manager_vnd.Manager.feature.customer.dto.UpdateCustomerRequest;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    @DisplayName("Tạo khách hàng thành công")
    void create_success() {
        CreateCustomerRequest request = new CreateCustomerRequest("Nguyen A", "0901234567", "HCM", null);
        when(customerRepository.existsByPhone("0901234567")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        var result = customerService.create(request);

        assertEquals(1L, result.id());
        assertTrue(result.active());
        assertEquals(BigDecimal.ZERO, result.currentDebt());
    }

    @Test
    @DisplayName("Tạo khách trùng SĐT bị conflict")
    void create_duplicatePhone_throwsConflict() {
        when(customerRepository.existsByPhone("0901234567")).thenReturn(true);
        assertThrows(ConflictException.class,
                () -> customerService.create(new CreateCustomerRequest("A", "0901234567", null, null)));
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Disable không xóa")
    void disable_success() {
        Customer customer = build(1L, "A", true);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);

        var result = customerService.disable(1L);

        assertFalse(result.active());
        verify(customerRepository, never()).delete(any(Customer.class));
    }

    @Test
    @DisplayName("Enable kích hoạt lại")
    void enable_success() {
        Customer customer = build(2L, "B", false);
        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);

        assertTrue(customerService.enable(2L).active());
    }

    @Test
    @DisplayName("Get by id không tìm thấy")
    void getById_notFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> customerService.getById(99L));
    }

    @Test
    @DisplayName("Update tên thành công")
    void update_success() {
        Customer customer = build(1L, "Old", true);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);

        var result = customerService.update(new UpdateCustomerRequest(1L, "New", null, null, null));

        assertEquals("New", result.name());
    }

    private Customer build(long id, String name, boolean active) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        customer.setActive(active);
        customer.setCurrentDebt(BigDecimal.ZERO);
        return customer;
    }
}
