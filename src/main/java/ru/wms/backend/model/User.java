package ru.wms.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.wms.backend.enums.Role;
import ru.wms.backend.enums.UserStatus;
import java.time.LocalDateTime;
import com.github.f4b6a3.ulid.UlidCreator;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "id", length = 26, nullable = false)
    private String id;

    @NotBlank(message = "Имя сотрудника не может быть пустым")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Email сотрудника не может быть пустым")
    @Email(message = "Некорректный формат Email")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Column(name = "assigned_equipment") // закрепленное оборудование за сотрудником
    private String assignedEquipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = UserStatus.ACTIVE;
        }
        if (this.id == null) {
            // Генерируем настоящий строковый ULID (всегда 26 символов в верхнем регистре)
            this.id = UlidCreator.getUlid().toString();
        }
    }
}
