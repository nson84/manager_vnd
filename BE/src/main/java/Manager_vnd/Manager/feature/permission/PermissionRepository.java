package Manager_vnd.Manager.feature.permission;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    boolean existsByApiPathAndMethod(String apiPath, String method);

    boolean existsByApiPathAndMethodAndIdNot(String apiPath, String method, long id);
}
