package Manager_vnd.Manager.feature.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import Manager_vnd.Manager.exception.ConflictException;
import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.company.dto.CreateCompanyRequest;
import Manager_vnd.Manager.feature.company.dto.UpdateCompanyRequest;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyServiceImpl companyService;

    @Test
    @DisplayName("Tạo công ty thành công")
    void create_success() {
        CreateCompanyRequest request = new CreateCompanyRequest("Acme", "Desc", "HN", null);
        when(companyRepository.existsByNameIgnoreCase("Acme")).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> {
            Company c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        var result = companyService.create(request);

        assertEquals(1L, result.id());
        assertTrue(result.active());
        assertEquals("Acme", result.name());
    }

    @Test
    @DisplayName("Tạo công ty trùng tên bị conflict")
    void create_duplicateName_throwsConflict() {
        when(companyRepository.existsByNameIgnoreCase("Acme")).thenReturn(true);
        assertThrows(ConflictException.class,
                () -> companyService.create(new CreateCompanyRequest("Acme", null, null, null)));
        verify(companyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Disable đặt active=false, không xóa")
    void disable_success() {
        Company company = new Company();
        company.setId(2L);
        company.setName("Acme");
        company.setActive(true);
        when(companyRepository.findById(2L)).thenReturn(Optional.of(company));
        when(companyRepository.save(company)).thenReturn(company);

        var result = companyService.disable(2L);

        assertFalse(result.active());
        verify(companyRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Disable công ty đã inactive bị conflict")
    void disable_alreadyInactive_throwsConflict() {
        Company company = new Company();
        company.setId(2L);
        company.setActive(false);
        when(companyRepository.findById(2L)).thenReturn(Optional.of(company));

        assertThrows(ConflictException.class, () -> companyService.disable(2L));
    }

    @Test
    @DisplayName("Enable kích hoạt lại công ty")
    void enable_success() {
        Company company = new Company();
        company.setId(3L);
        company.setName("Acme");
        company.setActive(false);
        when(companyRepository.findById(3L)).thenReturn(Optional.of(company));
        when(companyRepository.save(company)).thenReturn(company);

        var result = companyService.enable(3L);

        assertTrue(result.active());
    }

    @Test
    @DisplayName("Get by id không tìm thấy")
    void getById_notFound() {
        when(companyRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> companyService.getById(99L));
    }

    @Test
    @DisplayName("Update cập nhật tên")
    void update_success() {
        Company company = new Company();
        company.setId(1L);
        company.setName("Old");
        company.setActive(true);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(companyRepository.existsByNameIgnoreCaseAndIdNot("New", 1L)).thenReturn(false);
        when(companyRepository.save(company)).thenReturn(company);

        var result = companyService.update(new UpdateCompanyRequest(1L, "New", null, null, null));

        assertEquals("New", result.name());
    }

    @Test
    @DisplayName("List theo active=true")
    void getAll_filterActive() {
        Company company = new Company();
        company.setId(1L);
        company.setName("Acme");
        company.setActive(true);
        when(companyRepository.findByActive(org.mockito.ArgumentMatchers.eq(true), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(company)));

        var result = companyService.getAll(1, 10, "id,asc", true);

        assertEquals(1, result.result().size());
        assertTrue(result.result().get(0).active());
    }
}
