package Manager_vnd.Manager.feature.debt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DebtEntryRepository extends JpaRepository<DebtEntry, Long>, JpaSpecificationExecutor<DebtEntry> {
}
