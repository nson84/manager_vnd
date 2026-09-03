package Manager_vnd.Manager.feature.cashbook;

import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.feature.cashbook.dto.CashEntryResponse;
import Manager_vnd.Manager.feature.cashbook.dto.CashStatsResponse;
import Manager_vnd.Manager.feature.cashbook.dto.CategorySummary;
import Manager_vnd.Manager.feature.cashbook.dto.CreateManualCashEntryRequest;
import Manager_vnd.Manager.feature.cashbook.dto.UpdateCashCheckedRequest;
import Manager_vnd.Manager.feature.cashbook.dto.UpdateCashNoteRequest;

import java.util.List;

public interface CashbookService {

    PaginatedResult<CashEntryResponse> getEntries(CashEntryFilter filter, int page, int size, String sort);

    CashStatsResponse getStats(CashEntryFilter filter);

    CashEntryResponse getById(long id);

    CashEntryResponse createManual(CreateManualCashEntryRequest request);

    CashEntryResponse updateNote(long id, UpdateCashNoteRequest request);

    CashEntryResponse updateChecked(long id, UpdateCashCheckedRequest request);

    void deleteManual(long id);

    byte[] exportPdf(CashEntryFilter filter);

    List<CategorySummary> listCategories();
}
