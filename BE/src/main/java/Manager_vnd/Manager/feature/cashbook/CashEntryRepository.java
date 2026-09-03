package Manager_vnd.Manager.feature.cashbook;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CashEntryRepository extends JpaRepository<CashEntry, Long>, JpaSpecificationExecutor<CashEntry> {

    @EntityGraph(attributePaths = {"category", "createdBy", "checkedBy"})
    Optional<CashEntry> findWithDetailsById(long id);

    List<CashEntry> findByRefTypeAndRefId(CashRefType refType, Long refId);

    @Override
    @EntityGraph(attributePaths = {"category", "createdBy", "checkedBy"})
    Page<CashEntry> findAll(Specification<CashEntry> spec, Pageable pageable);

    @Query("""
            select e.direction, coalesce(sum(e.amount), 0), count(e)
            from CashEntry e
            where (:fromDate is null or e.entryDate >= :fromDate)
              and (:toDate is null or e.entryDate <= :toDate)
              and (:direction is null or e.direction = :direction)
              and (:categoryId is null or e.category.id = :categoryId)
              and (:refType is null or e.refType = :refType)
              and (:refId is null or e.refId = :refId)
              and (:createdBy is null or e.createdBy.id = :createdBy)
              and (:checked is null or e.checked = :checked)
              and (:amountMin is null or e.amount >= :amountMin)
              and (:amountMax is null or e.amount <= :amountMax)
              and (
                   :q is null or :q = ''
                   or lower(e.description) like lower(concat('%', :q, '%'))
                   or lower(e.note) like lower(concat('%', :q, '%'))
              )
            group by e.direction
            """)
    List<Object[]> aggregateByDirection(
            @Param("fromDate") java.time.LocalDate fromDate,
            @Param("toDate") java.time.LocalDate toDate,
            @Param("direction") CashDirection direction,
            @Param("categoryId") Long categoryId,
            @Param("refType") CashRefType refType,
            @Param("refId") Long refId,
            @Param("createdBy") Long createdBy,
            @Param("checked") Boolean checked,
            @Param("amountMin") java.math.BigDecimal amountMin,
            @Param("amountMax") java.math.BigDecimal amountMax,
            @Param("q") String q);

    @Query("""
            select e.category.id, e.category.name, e.direction, coalesce(sum(e.amount), 0)
            from CashEntry e
            where (:fromDate is null or e.entryDate >= :fromDate)
              and (:toDate is null or e.entryDate <= :toDate)
              and (:direction is null or e.direction = :direction)
              and (:categoryId is null or e.category.id = :categoryId)
              and (:refType is null or e.refType = :refType)
              and (:refId is null or e.refId = :refId)
              and (:createdBy is null or e.createdBy.id = :createdBy)
              and (:checked is null or e.checked = :checked)
              and (:amountMin is null or e.amount >= :amountMin)
              and (:amountMax is null or e.amount <= :amountMax)
              and (
                   :q is null or :q = ''
                   or lower(e.description) like lower(concat('%', :q, '%'))
                   or lower(e.note) like lower(concat('%', :q, '%'))
              )
            group by e.category.id, e.category.name, e.direction
            order by sum(e.amount) desc
            """)
    List<Object[]> aggregateByCategory(
            @Param("fromDate") java.time.LocalDate fromDate,
            @Param("toDate") java.time.LocalDate toDate,
            @Param("direction") CashDirection direction,
            @Param("categoryId") Long categoryId,
            @Param("refType") CashRefType refType,
            @Param("refId") Long refId,
            @Param("createdBy") Long createdBy,
            @Param("checked") Boolean checked,
            @Param("amountMin") java.math.BigDecimal amountMin,
            @Param("amountMax") java.math.BigDecimal amountMax,
            @Param("q") String q);
}
