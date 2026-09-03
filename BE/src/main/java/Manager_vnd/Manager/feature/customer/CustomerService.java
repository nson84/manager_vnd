package Manager_vnd.Manager.feature.customer;

import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.feature.customer.dto.CreateCustomerRequest;
import Manager_vnd.Manager.feature.customer.dto.CustomerResponse;
import Manager_vnd.Manager.feature.customer.dto.UpdateCustomerRequest;

public interface CustomerService {

    PaginatedResult<CustomerResponse> getAll(int page, int size, String sort, Boolean active, String q);

    CustomerResponse getById(long id);

    CustomerResponse create(CreateCustomerRequest request);

    CustomerResponse update(UpdateCustomerRequest request);

    CustomerResponse disable(long id);

    CustomerResponse enable(long id);
}
