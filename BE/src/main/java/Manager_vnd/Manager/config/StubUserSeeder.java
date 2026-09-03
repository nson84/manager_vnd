package Manager_vnd.Manager.config;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import Manager_vnd.Manager.feature.company.Company;
import Manager_vnd.Manager.feature.company.CompanyRepository;
import Manager_vnd.Manager.feature.role.Role;
import Manager_vnd.Manager.feature.role.RoleRepository;
import Manager_vnd.Manager.feature.user.User;
import Manager_vnd.Manager.feature.user.UserRepository;

@Component
@Order(1)
public class StubUserSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public StubUserSeeder(
            UserRepository userRepository,
            RoleRepository roleRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Role ADMIN chưa được seed"));
        Company shop = companyRepository.findByNameIgnoreCase("Tạp Hóa Phúc Sơn").orElse(null);

        userRepository.findByEmail("admin@local.dev").ifPresentOrElse(existing -> {
            if (!existing.getPassword().startsWith("$2")) {
                existing.setPassword(passwordEncoder.encode("password123"));
            }
            assignAdmin(existing, adminRole);
            if (existing.getCompany() == null && shop != null) {
                existing.setCompany(shop);
            }
            userRepository.save(existing);
        }, () -> {
            User user = new User();
            user.setName("Admin");
            user.setEmail("admin@local.dev");
            user.setPassword(passwordEncoder.encode("password123"));
            user.setRoles(List.of(adminRole));
            user.setCompany(shop);
            userRepository.save(user);
        });
    }

    private void assignAdmin(User user, Role adminRole) {
        boolean hasAdmin = user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));
        if (!hasAdmin) {
            user.getRoles().add(adminRole);
        }
    }
}
