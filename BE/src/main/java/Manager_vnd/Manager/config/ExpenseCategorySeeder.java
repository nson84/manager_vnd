package Manager_vnd.Manager.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import Manager_vnd.Manager.feature.expense.ExpenseCategory;
import Manager_vnd.Manager.feature.expense.ExpenseCategoryRepository;

@Component
public class ExpenseCategorySeeder implements ApplicationRunner {

    private final ExpenseCategoryRepository expenseCategoryRepository;

    public ExpenseCategorySeeder(ExpenseCategoryRepository expenseCategoryRepository) {
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seed("WAGE", "Tien luong", true, 10);
        seed("WORKER_ADVANCE", "Ung tho", true, 20);
        seed("CUSTOMER_REPAY", "Thu no khach", true, 30);
        seed("UTILITIES", "Dien nuoc", false, 40);
        seed("RENT", "Thue mat bang", false, 50);
        seed("GOODS", "Hang hoa / vat tu", false, 60);
        seed("OTHER", "Chi khac", false, 70);
        seed("MANUAL_IN", "Thu thu cong", false, 80);
    }

    private void seed(String code, String name, boolean system, int sortOrder) {
        if (expenseCategoryRepository.findByCode(code).isPresent()) {
            return;
        }
        ExpenseCategory category = new ExpenseCategory();
        category.setCode(code);
        category.setName(name);
        category.setSystem(system);
        category.setSortOrder(sortOrder);
        expenseCategoryRepository.save(category);
    }
}
