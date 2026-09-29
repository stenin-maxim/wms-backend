package ru.wms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    
    @NotBlank(message = "ID сотрудника не может быть пустым")
    private String employeeId; 

    @NotBlank(message = "Статус не может быть пустым")
    private String status;
}