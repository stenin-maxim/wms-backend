package ru.wms.dto.request.cell;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCellBlockRequest {
    @NotNull(message = "Статус блокировки обязателен (true/false)")
    private Boolean isBlocked;
}