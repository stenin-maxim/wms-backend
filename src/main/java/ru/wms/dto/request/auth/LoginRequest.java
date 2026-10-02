package ru.wms.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Email сотрудника обязателен для входа")
    @Email(message = "Некорректный формат Email адреса")
    private String email;

    @NotBlank(message = "Пароль обязателен для входа")
    private String password;
}
