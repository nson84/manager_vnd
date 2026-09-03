package Manager_vnd.Manager.feature.worker;

import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.feature.worker.dto.CreateWorkerRequest;
import Manager_vnd.Manager.feature.worker.dto.UpdateWorkerRequest;
import Manager_vnd.Manager.feature.worker.dto.WorkerResponse;

public interface WorkerService {

    PaginatedResult<WorkerResponse> getAll(int page, int size, String sort, Boolean active, String q);

    WorkerResponse getById(long id);

    WorkerResponse create(CreateWorkerRequest request);

    WorkerResponse update(UpdateWorkerRequest request);

    WorkerResponse disable(long id);

    WorkerResponse enable(long id);
}
