package ru.wms.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.wms.backend.model.Company;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByEmail(String email);
    boolean existsByEmail(String email);
}
