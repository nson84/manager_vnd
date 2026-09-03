package Manager_vnd.Manager.feature.expense;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {

    Optional<ExpenseCategory> findByCode(String code);
}
