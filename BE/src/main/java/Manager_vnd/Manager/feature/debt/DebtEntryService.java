package Manager_vnd.Manager.feature.debt;

import java.time.LocalDate;

import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.feature.debt.dto.CreateDebtEntryRequest;
import Manager_vnd.Manager.feature.debt.dto.DebtEntryResponse;

public interface DebtEntryService {

    PaginatedResult<DebtEntryResponse> getAll(
            int page, int size, String sort,
            Long customerId, Long workerId,
            LocalDate fromDate, LocalDate toDate,
            DebtEntryType entryType);

    DebtEntryResponse getById(long id);

    DebtEntryResponse create(CreateDebtEntryRequest request);

    /** Internal: tạo bút toán từ Payslip (không qua API public). */
    DebtEntryResponse createInternal(
            Long customerId,
            Long workerId,
            DebtEntryType entryType,
            LedgerDirection direction,
            java.math.BigDecimal amount,
            LocalDate entryDate,
            String note,
            DebtRefType refType,
            Long refId,
            boolean postCash);
}
