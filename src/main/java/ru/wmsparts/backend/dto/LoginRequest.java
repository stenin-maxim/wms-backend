package ru.wmsparts.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email сотрудника обязателен для входа")
    @Email(message = "Некорректный формат Email адреса")
    private String email;

    @NotBlank(message = "Пароль обязателен для входа")
    private String password;
}
