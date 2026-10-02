package ru.wms.dto.request.cell;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCellRequest {
    @NotBlank(message = "Имя зоны обязательно (например: A)")
    @Size(min = 1, max = 10, message = "Имя зоны должно быть от 1 до 10 символов")
    // Проверка: разрешены только заглавные латинские буквы и цифры (например: A, B, MEZZ1)
    @Pattern(regexp = "^[A-Z0-String9]+$", message = "Имя зоны должно содержать только заглавные латинские буквы и цифры")
    private String zoneName;

    @NotBlank(message = "Номер стеллажа обязателен (например: 01)")
    // Проверка: ровно две цифры от 01 до 99. Никаких пробелов и букв
    @Pattern(regexp = "^[0-9]{2}$", message = "Номер стеллажа должен состоять ровно из двух цифр (например: 01, 09, 12)")
    private String rack;

    @NotBlank(message = "Номер яруса обязателен (например: 02)")
    // Проверка: ровно две цифры (ярус / полка)
    @Pattern(regexp = "^[0-9]{2}$", message = "Номер яруса должен состоять ровно из двух цифр (например: 01, 05)")
    private String shelf;

    @NotBlank(message = "Номер позиции обязателен (например: 05)")
    // Проверка: ровно две цифры (конкретный лоток или место на полке)
    @Pattern(regexp = "^[0-9]{2}$", message = "Номер позиции должен состоять ровно из двух цифр (например: 01, 15)")
    private String position;
}
