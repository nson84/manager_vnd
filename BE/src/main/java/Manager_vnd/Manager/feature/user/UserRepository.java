package Manager_vnd.Manager.feature.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"company", "roles"})
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"company", "roles"})
    Optional<User> findWithDetailsById(long id);

    @EntityGraph(attributePaths = {"company", "roles"})
    Page<User> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"company", "roles"})
    Page<User> findByActive(boolean active, Pageable pageable);

    @EntityGraph(attributePaths = "roles")
    List<User> findByRoles_Id(long roleId);
}
