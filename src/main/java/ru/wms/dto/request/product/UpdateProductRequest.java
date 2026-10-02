package ru.wms.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // КРИТИЧЕСКИ ВАЖНО для десериализации Jackson!
@AllArgsConstructor // Полезно для тестов, чтобы быстро собирать DTO вручную
public class UpdateProductRequest {
    @NotBlank(message = "Название товара не может быть пустым")
    private String name;

    @NotBlank(message = "Артикул (SKU) обязателен")
    private String sku;

    @NotBlank(message = "Бренд обязателен")
    private String brand;

    private String barcode;
}
