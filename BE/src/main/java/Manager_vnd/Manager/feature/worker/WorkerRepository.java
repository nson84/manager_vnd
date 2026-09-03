package Manager_vnd.Manager.feature.worker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WorkerRepository extends JpaRepository<Worker, Long>, JpaSpecificationExecutor<Worker> {

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, long id);
}
