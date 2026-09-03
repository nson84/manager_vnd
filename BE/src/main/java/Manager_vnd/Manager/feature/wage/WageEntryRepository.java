package Manager_vnd.Manager.feature.wage;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WageEntryRepository extends JpaRepository<WageEntry, Long>, JpaSpecificationExecutor<WageEntry> {

    @Query("""
            select w from WageEntry w
            where w.worker.id = :workerId
              and w.workDate >= :from
              and w.workDate <= :to
              and w.payslip is null
            """)
    List<WageEntry> findUnpaidInPeriod(
            @Param("workerId") long workerId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    List<WageEntry> findByPayslipId(long payslipId);
}
