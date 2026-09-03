package Manager_vnd.Manager.feature.wage;

import java.time.LocalDate;

import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.feature.wage.dto.CreateWageEntryRequest;
import Manager_vnd.Manager.feature.wage.dto.UpdateWageEntryRequest;
import Manager_vnd.Manager.feature.wage.dto.WageEntryResponse;

public interface WageEntryService {

    PaginatedResult<WageEntryResponse> getAll(
            int page, int size, String sort,
            Long workerId, LocalDate fromDate, LocalDate toDate, Boolean unpaidOnly);

    WageEntryResponse getById(long id);

    WageEntryResponse create(CreateWageEntryRequest request);

    WageEntryResponse update(UpdateWageEntryRequest request);

    void delete(long id);
}
