package Manager_vnd.Manager.feature.payslip;

import java.time.LocalDate;

import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.feature.payslip.dto.CreatePayslipRequest;
import Manager_vnd.Manager.feature.payslip.dto.PayslipResponse;
import Manager_vnd.Manager.feature.payslip.dto.UpdatePayslipRequest;

public interface PayslipService {

    PaginatedResult<PayslipResponse> getAll(
            int page, int size, String sort,
            Long workerId, PayslipStatus status,
            LocalDate periodFrom, LocalDate periodTo);

    PayslipResponse getById(long id);

    PayslipResponse create(CreatePayslipRequest request);

    PayslipResponse update(UpdatePayslipRequest request);

    PayslipResponse confirm(long id);

    PayslipResponse pay(long id);

    PayslipResponse cancel(long id);
}
