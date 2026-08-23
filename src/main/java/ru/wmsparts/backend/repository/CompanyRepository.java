package ru.wmsparts.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.wmsparts.backend.model.Company;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByEmail(String email);
    boolean existsByEmail(String email);
}
