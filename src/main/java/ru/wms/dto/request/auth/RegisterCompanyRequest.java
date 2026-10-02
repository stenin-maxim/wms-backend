package ru.wms.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCompanyRequest {
    @NotBlank(message = "Название компании обязательно")
    private String companyName;

    @NotBlank(message = "Имя администратора обязательно")
    private String adminName;

    @NotBlank(message = "Email администратора обязателен")
    @Email(message = "Некорректный формат Email")
    private String adminEmail;

    @NotBlank(message = "Пароль обязателен")
    private String password;
}