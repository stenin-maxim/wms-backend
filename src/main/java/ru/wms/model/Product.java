package ru.wms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.github.f4b6a3.ulid.UlidCreator;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "products",
    uniqueConstraints= {
        @UniqueConstraint(columnNames = {"sku", "brand", "company_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    
    @Id
    @Column(name = "id", length = 26, nullable = false)
    private String id;

    @NotBlank(message = "Название товара не может быть пустым")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Артикул (SKU) обязателен для автозапчастей")
    @Column(name = "sku", nullable = false)
    private String sku; // Например: "55503-38010"

    @NotBlank(message = "Бренд / Производитель обязателен")
    @Column(name = "brand", nullable = false)
    private String brand; // Например: "Hyundai / KIA"

    @Column(name = "barcode", length = 50)
    private String barcode; // Штрихкод для сканирования ТСД (EAN-13 / UPC), может быть null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.id == null) {
            this.id = UlidCreator.getUlid().toString(); // Автогенерация ULID
        }
    }
}
