package ru.wms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "Название товара не может быть пустым")
    @Size(max = 255, message = "Название товара не должно превышать 255 символов")
    private String name;

    @NotBlank(message = "Артикул (SKU) обязателен")
    @Size(min = 2, max = 50, message = "Длина артикула должна быть от 2 до 50 символов")
    private String sku;

    @NotBlank(message = "Бренд обязателен")
    @Size(max = 100, message = "Название бренда не должно превышать 100 символов")
    private String brand;

    @Size(max = 50, message = "Штрихкод не должен превышать 50 символов")
    private String barcode; // Опционально (можно привязать позже при приемке)
}
