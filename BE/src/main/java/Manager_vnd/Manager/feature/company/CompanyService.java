package Manager_vnd.Manager.feature.company;

import java.util.List;

import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.feature.company.dto.CompanyResponse;
import Manager_vnd.Manager.feature.company.dto.CreateCompanyRequest;
import Manager_vnd.Manager.feature.company.dto.PublicCompanyResponse;
import Manager_vnd.Manager.feature.company.dto.UpdateCompanyRequest;

public interface CompanyService {

    List<PublicCompanyResponse> listPublic();

    PaginatedResult<CompanyResponse> getAll(int page, int size, String sort, Boolean active);

    CompanyResponse getById(long id);

    CompanyResponse create(CreateCompanyRequest request);

    CompanyResponse update(UpdateCompanyRequest request);

    CompanyResponse disable(long id);

    CompanyResponse enable(long id);
}
