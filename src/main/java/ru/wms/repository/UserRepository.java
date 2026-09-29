package ru.wms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.wms.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByCompanyId(Long companyId);
}