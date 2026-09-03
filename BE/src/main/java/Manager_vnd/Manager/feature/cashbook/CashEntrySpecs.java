package Manager_vnd.Manager.feature.cashbook;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

public final class CashEntrySpecs {

    private CashEntrySpecs() {
    }

    public static Specification<CashEntry> withFilters(CashEntryFilter filter) {
        return Specification
                .where(entryDateFrom(filter.fromDate()))
                .and(entryDateTo(filter.toDate()))
                .and(directionEq(filter.direction()))
                .and(categoryIdEq(filter.categoryId()))
                .and(refTypeEq(filter.refType()))
                .and(refIdEq(filter.refId()))
                .and(createdByEq(filter.createdBy()))
                .and(checkedEq(filter.checked()))
                .and(amountMin(filter.amountMin()))
                .and(amountMax(filter.amountMax()))
                .and(search(filter.q()));
    }

    private static Specification<CashEntry> entryDateFrom(LocalDate from) {
        return (root, query, cb) -> from == null ? null : cb.greaterThanOrEqualTo(root.get("entryDate"), from);
    }

    private static Specification<CashEntry> entryDateTo(LocalDate to) {
        return (root, query, cb) -> to == null ? null : cb.lessThanOrEqualTo(root.get("entryDate"), to);
    }

    private static Specification<CashEntry> directionEq(CashDirection direction) {
        return (root, query, cb) -> direction == null ? null : cb.equal(root.get("direction"), direction);
    }

    private static Specification<CashEntry> categoryIdEq(Long categoryId) {
        return (root, query, cb) -> categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);
    }

    private static Specification<CashEntry> refTypeEq(CashRefType refType) {
        return (root, query, cb) -> refType == null ? null : cb.equal(root.get("refType"), refType);
    }

    private static Specification<CashEntry> refIdEq(Long refId) {
        return (root, query, cb) -> refId == null ? null : cb.equal(root.get("refId"), refId);
    }

    private static Specification<CashEntry> createdByEq(Long createdBy) {
        return (root, query, cb) -> createdBy == null ? null : cb.equal(root.get("createdBy").get("id"), createdBy);
    }

    private static Specification<CashEntry> checkedEq(Boolean checked) {
        return (root, query, cb) -> checked == null ? null : cb.equal(root.get("checked"), checked);
    }

    private static Specification<CashEntry> amountMin(BigDecimal min) {
        return (root, query, cb) -> min == null ? null : cb.greaterThanOrEqualTo(root.get("amount"), min);
    }

    private static Specification<CashEntry> amountMax(BigDecimal max) {
        return (root, query, cb) -> max == null ? null : cb.lessThanOrEqualTo(root.get("amount"), max);
    }

    private static Specification<CashEntry> search(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return null;
            }
            String pattern = "%" + q.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(cb.coalesce(root.get("description"), "")), pattern),
                    cb.like(cb.lower(cb.coalesce(root.get("note"), "")), pattern));
        };
    }
}
