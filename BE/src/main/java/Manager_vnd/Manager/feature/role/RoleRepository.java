package Manager_vnd.Manager.feature.role;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, long id);

    @EntityGraph(attributePaths = "permissions")
    @Override
    Optional<Role> findById(Long id);

    @EntityGraph(attributePaths = "permissions")
    @Override
    Page<Role> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "permissions")
    List<Role> findByPermissions_Id(long permissionId);
}
