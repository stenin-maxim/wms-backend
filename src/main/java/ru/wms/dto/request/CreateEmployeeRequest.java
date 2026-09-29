package ru.wms.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.wms.enums.Role;

@Data
public class CreateEmployeeRequest {

    @NotBlank(message = "Имя сотрудника не может быть пустым")
    private String name;

    @NotBlank(message = "Email сотрудника обязателен")
    @Email(message = "Некорректный формат Email")
    private String Email;

    @NotBlank(message = "Пароль обязателен")
    private String password;

    @NotNull(message = "Необходимо указать роль сотрудника")
    private Role role;

    // Закрепляемое оборудование (например, модель ТСД), может быть null
    private String assignedEquipment;
}
