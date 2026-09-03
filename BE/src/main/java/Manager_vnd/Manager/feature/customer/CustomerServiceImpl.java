package Manager_vnd.Manager.feature.customer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.dto.PaginationMeta;
import Manager_vnd.Manager.exception.ConflictException;
import Manager_vnd.Manager.exception.InvalidRequestException;
import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.customer.dto.CreateCustomerRequest;
import Manager_vnd.Manager.feature.customer.dto.CustomerResponse;
import Manager_vnd.Manager.feature.customer.dto.UpdateCustomerRequest;
import jakarta.persistence.criteria.Predicate;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<CustomerResponse> getAll(int page, int size, String sort, Boolean active, String q) {
        Pageable pageable = toPageable(page, size, sort);
        Page<Customer> result = customerRepository.findAll(buildSpec(active, q), pageable);
        List<CustomerResponse> items = result.getContent().stream()
                .map(CustomerResponse::fromEntity)
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
    public CustomerResponse getById(long id) {
        return CustomerResponse.fromEntity(findCustomer(id));
    }

    @Override
    @Transactional
    public CustomerResponse create(CreateCustomerRequest request) {
        String phone = normalizePhone(request.phone());
        assertPhoneUnique(phone, null);

        Customer customer = new Customer();
        customer.setName(request.name().trim());
        customer.setPhone(phone);
        customer.setAddress(blankToNull(request.address()));
        customer.setNote(blankToNull(request.note()));
        customer.setActive(true);
        customer.setCurrentDebt(BigDecimal.ZERO);
        return CustomerResponse.fromEntity(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public CustomerResponse update(UpdateCustomerRequest request) {
        Customer customer = findCustomer(request.id());
        if (request.name() != null) {
            String name = request.name().trim();
            if (name.isEmpty()) {
                throw new InvalidRequestException("Tên khách hàng không được để trống");
            }
            customer.setName(name);
        }
        if (request.phone() != null) {
            String phone = normalizePhone(request.phone());
            assertPhoneUnique(phone, customer.getId());
            customer.setPhone(phone);
        }
        if (request.address() != null) {
            customer.setAddress(blankToNull(request.address()));
        }
        if (request.note() != null) {
            customer.setNote(blankToNull(request.note()));
        }
        return CustomerResponse.fromEntity(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public CustomerResponse disable(long id) {
        Customer customer = findCustomer(id);
        if (!customer.isActive()) {
            throw new ConflictException("Khách hàng đã bị vô hiệu hóa");
        }
        customer.setActive(false);
        return CustomerResponse.fromEntity(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public CustomerResponse enable(long id) {
        Customer customer = findCustomer(id);
        if (customer.isActive()) {
            throw new ConflictException("Khách hàng đang hoạt động");
        }
        customer.setActive(true);
        return CustomerResponse.fromEntity(customerRepository.save(customer));
    }

    private Specification<Customer> buildSpec(Boolean active, String q) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }
            if (q != null && !q.isBlank()) {
                String pattern = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("phone"), "")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("note"), "")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("address"), "")), pattern)));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Customer findCustomer(long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
    }

    private void assertPhoneUnique(String phone, Long excludeId) {
        if (phone == null) {
            return;
        }
        boolean exists = excludeId == null
                ? customerRepository.existsByPhone(phone)
                : customerRepository.existsByPhoneAndIdNot(phone, excludeId);
        if (exists) {
            throw new ConflictException("Số điện thoại đã tồn tại");
        }
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        return phone.trim();
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
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
