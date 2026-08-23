package ru.wmsparts.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import ru.wmsparts.backend.enums.CompanyStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название компании не может быть пустым")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Email компании не может быть пустым")
    @Email(message = "Некорректный формат Email")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^\\d{10}$", message = "ИНН юридического лица должен состоять ровно из 10 цифр")
    @Column(name = "inn", nullable = true, unique = true, length = 10)
    private String inn;

    @Pattern(regexp = "^\\d{13}$", message = "ОГРН должен состоять ровно из 13 цифр")
    @Column(name = "ogrn", nullable = true, unique = true, length = 13)
    private String ogrn;

    @Column(name = "main_warehouse_address", nullable = true)
    private String mainWarehouseAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CompanyStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = CompanyStatus.TRIAL; // При первой регистрации даем компании TRIAL статус
        }
    }
}
