package ru.wms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.wms.dto.request.cell.CreateCellRequest;
import ru.wms.dto.request.cell.UpdateCellBlockRequest;
import ru.wms.dto.response.CellDto;
import ru.wms.model.User;
import ru.wms.service.CellService;
import java.util.List;

@RestController
@RequestMapping("/cells")
@RequiredArgsConstructor
public class CellController {
    private final CellService cellService;

    /**
     * Получить всю адресную топологию вашего склада.
     */
    @GetMapping
    public ResponseEntity<List<CellDto>> getMyCells(@AuthenticationPrincipal User currentUser) {
        List<CellDto> cells = cellService.getCellsByCompany(currentUser.getCompany());
        return ResponseEntity.ok(cells);
    }

    /**
     * Создать новую адресную ячейку.
     */
    @PostMapping
    public ResponseEntity<CellDto> addCell(
        @Valid @RequestBody CreateCellRequest request,
        @AuthenticationPrincipal User currentUser
    ) {
        CellDto response = cellService.createCell(request, currentUser.getCompany());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Заблокировать или разблокировать ячейку для операций.
     */
    @PatchMapping("/{id}/block")
    public ResponseEntity<CellDto> changeCellBlockStatus(
        @PathVariable("id") @NonNull String cellId,
        @Valid @RequestBody UpdateCellBlockRequest request,
        @AuthenticationPrincipal User currentUser
    ) {
        CellDto response = cellService.toggleCellBlock(cellId, request.getIsBlocked(), currentUser.getCompany());
        return ResponseEntity.ok(response);
    }
}
