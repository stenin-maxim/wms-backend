package ru.wms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.wms.dto.request.cell.CreateCellRequest;
import ru.wms.dto.response.CellDto;
import ru.wms.model.Company;
import ru.wms.model.Cell;
import ru.wms.repository.CellRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CellService {
    private final CellRepository cellRepository;

    /**
     * Получить список всех ячеек топологии текущей компании.
     */
    @Transactional(readOnly = true)
    public List<CellDto> getCellsByCompany(Company company) {
        return cellRepository.findByCompanyId(company.getId()).stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    /**
     * Добавить новую адресную ячейку на склад.
     */
    @Transactional
    public CellDto createCell(CreateCellRequest request, Company company) {
        // Форматируем составляющие и собираем стандартизированный адрес: "A-01-02-05"
        String zone = request.getZoneName().toUpperCase().trim();
        String rack = request.getRack().trim();
        String shelf = request.getShelf().trim();
        String pos = request.getPosition().trim();
        String generatedAddress = String.format("%s-%s-%s-%s", zone, rack, shelf, pos);

        // Проверяем, нет ли уже ячейки с таким адресом в нашей компании
        if (cellRepository.existsByAddressAndCompanyId(generatedAddress, company.getId())) {
            throw new IllegalArgumentException("Ячейка с адресом " + generatedAddress + " уже существует на вашем складе");
        }

        Cell cell = Cell.builder()
            .address(generatedAddress)
            .zoneName(zone)
            .rack(rack)
            .shelf(shelf)
            .position(pos)
            .company(company)
            .build();

        @SuppressWarnings("null")
        Cell savedCell = cellRepository.save(cell);
        return mapToDto(savedCell);
    }

    /**
     * Переключить блокировку ячейки (для инвентаризации или ремонта).
     */
    @Transactional
    public CellDto toggleCellBlock(@NonNull String cellId, boolean blockStatus, Company company) {
        Cell cell = cellRepository.findById(cellId)
            .orElseThrow(() -> new IllegalArgumentException("Ячейка с указанным ID не найден"));

        if (!cell.getCompany().getId().equals(company.getId())) {
            throw new IllegalArgumentException("Доступ запрещен: эта ячейка принадлежит другой организации");
        }

        cell.setBlocked(blockStatus);
        return mapToDto(cellRepository.save(cell));
    }

    private CellDto mapToDto(Cell cell) {
        return CellDto.builder()
            .id(cell.getId())
            .address(cell.getAddress())
            .zoneName(cell.getZoneName())
            .rack(cell.getRack())
            .shelf(cell.getShelf())
            .position(cell.getPosition())
            .barcode(cell.getBarcode())
            .isBlocked(cell.isBlocked())
            .build();
    }
}