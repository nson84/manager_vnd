package Manager_vnd.Manager.feature.company;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.dto.PaginationMeta;
import Manager_vnd.Manager.exception.ConflictException;
import Manager_vnd.Manager.exception.InvalidRequestException;
import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.company.dto.CompanyResponse;
import Manager_vnd.Manager.feature.company.dto.CreateCompanyRequest;
import Manager_vnd.Manager.feature.company.dto.UpdateCompanyRequest;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<CompanyResponse> getAll(int page, int size, String sort, Boolean active) {
        Pageable pageable = toPageable(page, size, sort);
        Page<Company> result = active == null
                ? companyRepository.findAll(pageable)
                : companyRepository.findByActive(active, pageable);
        List<CompanyResponse> items = result.getContent().stream()
                .map(CompanyResponse::fromEntity)
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
    public CompanyResponse getById(long id) {
        return CompanyResponse.fromEntity(findCompany(id));
    }

    @Override
    @Transactional
    public CompanyResponse create(CreateCompanyRequest request) {
        if (companyRepository.existsByNameIgnoreCase(request.name().trim())) {
            throw new ConflictException("Tên công ty đã tồn tại");
        }
        Company company = new Company();
        company.setName(request.name().trim());
        company.setDescription(request.description());
        company.setAddress(request.address());
        company.setLogo(request.logo());
        company.setActive(true);
        return CompanyResponse.fromEntity(companyRepository.save(company));
    }

    @Override
    @Transactional
    public CompanyResponse update(UpdateCompanyRequest request) {
        Company company = findCompany(request.id());
        if (request.name() != null) {
            String name = request.name().trim();
            if (name.isEmpty()) {
                throw new InvalidRequestException("Tên công ty không được để trống");
            }
            if (companyRepository.existsByNameIgnoreCaseAndIdNot(name, company.getId())) {
                throw new ConflictException("Tên công ty đã tồn tại");
            }
            company.setName(name);
        }
        if (request.description() != null) {
            company.setDescription(request.description());
        }
        if (request.address() != null) {
            company.setAddress(request.address());
        }
        if (request.logo() != null) {
            company.setLogo(request.logo());
        }
        return CompanyResponse.fromEntity(companyRepository.save(company));
    }

    @Override
    @Transactional
    public CompanyResponse disable(long id) {
        Company company = findCompany(id);
        if (!company.isActive()) {
            throw new ConflictException("Công ty đã bị vô hiệu hóa");
        }
        company.setActive(false);
        return CompanyResponse.fromEntity(companyRepository.save(company));
    }

    @Override
    @Transactional
    public CompanyResponse enable(long id) {
        Company company = findCompany(id);
        if (company.isActive()) {
            throw new ConflictException("Công ty đang hoạt động");
        }
        company.setActive(true);
        return CompanyResponse.fromEntity(companyRepository.save(company));
    }

    private Company findCompany(long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
    }

    private Pageable toPageable(int page, int size, String sort) {
        int zeroBased = Math.max(page - 1, 0);
        return PageRequest.of(zeroBased, size, parseSort(sort));
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by("id").ascending();
        }
        String[] parts = sort.split(",");
        if (parts.length == 2) {
            return Sort.by(Sort.Direction.fromString(parts[1].trim()), parts[0].trim());
        }
        return Sort.by("id").ascending();
    }
}
