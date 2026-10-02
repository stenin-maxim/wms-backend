package ru.wms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.github.f4b6a3.ulid.UlidCreator;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "cells",
    uniqueConstraints = {
        // Уникальный индекс: запрещает создавать ячейки с одинаковым адресом внутри ОДНОЙ компании
        @UniqueConstraint(columnNames = {"address", "company_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cell {
    @Id
    @Column(name = "id", length = 26, nullable = false)
    private String id;

    @NotBlank(message = "Адрес ячейки не может быть пустым")
    @Column(name = "address", nullable = false, length = 50)
    private String address; // Полный сгенерированный адрес, например: "A-01-02-04"

    @NotBlank(message = "Зона склада обязательна")
    @Column(name = "zone_name", nullable = false, length = 20)
    private String zoneName; // Например: "A", "B", "MEZZANINE"

    @NotBlank(message = "Номер стеллажа обязателен")
    @Column(name = "rack", nullable = false, length = 10)
    private String rack; // Например: "01", "02"

    @NotBlank(message = "Номер яруса обязателен")
    @Column(name = "shelf", nullable = false, length = 10)
    private String shelf; // Например: "01", "02"

    @NotBlank(message = "Номер позиции обязателен")
    @Column(name = "position", nullable = false, length = 10)
    private String position; // Например: "01", "02"

    @Column(name = "barcode", length = 50, unique = true)
    private String barcode; // Штрихкод ячейки, который наклеивается на стеллаж для сканирования ТСД

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked; // Флаг блокировки (например, стеллаж сломался или идет инвентаризация)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // SaaS-изоляция: жесткая привязка топологии к организации

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.id == null) {
            this.id = UlidCreator.getUlid().toString(); // Автогенерация ULID
        }
        // Автоматически формируем красивый штрихкод ячейки, если он не передан
        if (this.barcode == null) {
            this.barcode = "CELL-" + this.address.replace("-", "");
        }
        this.isBlocked = false;
    }
}
