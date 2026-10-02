package ru.wms.dto.request.employee;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor 
public class UpdateEmployeeStatusRequest {
    @NotBlank(message = "ID сотрудника не может быть пустым")
    private String employeeId; 

    @NotBlank(message = "Статус не может быть пустым")
    private String status;
}