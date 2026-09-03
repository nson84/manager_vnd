package Manager_vnd.Manager.feature.expense;

import java.time.LocalDate;

import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.feature.expense.dto.CreateExpenseRequest;
import Manager_vnd.Manager.feature.expense.dto.ExpenseResponse;

public interface ExpenseService {

    PaginatedResult<ExpenseResponse> getAll(
            int page, int size, String sort,
            Long categoryId, ExpenseStatus status,
            LocalDate fromDate, LocalDate toDate);

    ExpenseResponse getById(long id);

    ExpenseResponse create(CreateExpenseRequest request);

    ExpenseResponse cancel(long id);
}
