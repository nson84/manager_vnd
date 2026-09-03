package Manager_vnd.Manager.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import Manager_vnd.Manager.feature.role.Role;
import Manager_vnd.Manager.feature.role.RoleRepository;

@Component
@Order(0)
public class RoleSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seed("ADMIN", "Chủ hệ thống — đầy đủ tính năng");
        seed("MANAGER", "Quản lý cửa hàng — sổ quỹ, lương, phiếu chi");
        seed("STAFF", "Nhân viên — ghi công, khách, công nợ");
        seed("USER", "Nhân viên cửa hàng (cũ, cùng quyền STAFF)");
    }

    private void seed(String name, String description) {
        if (roleRepository.findByName(name).isPresent()) {
            return;
        }
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        roleRepository.save(role);
    }
}
