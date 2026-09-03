package Manager_vnd.Manager.feature.company;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findByActiveTrueOrderByNameAsc();

    Optional<Company> findByNameIgnoreCase(String name);

    Page<Company> findByActive(boolean active, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, long id);
}
