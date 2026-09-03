package Manager_vnd.Manager.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import Manager_vnd.Manager.feature.company.Company;
import Manager_vnd.Manager.feature.company.CompanyRepository;

@Component
@Order(0)
public class CompanySeeder implements ApplicationRunner {

    private static final String SHOP_NAME = "Tạp Hóa Phúc Sơn";

    private final CompanyRepository companyRepository;

    public CompanySeeder(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (companyRepository.existsByNameIgnoreCase(SHOP_NAME)) {
            return;
        }
        Company company = new Company();
        company.setName(SHOP_NAME);
        company.setDescription("Cửa hàng tạp hóa");
        company.setActive(true);
        companyRepository.save(company);
    }
}
