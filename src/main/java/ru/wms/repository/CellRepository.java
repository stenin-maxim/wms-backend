package ru.wms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.wms.model.Cell;
import java.util.List;
import java.util.Optional;

public interface CellRepository extends JpaRepository<Cell, String> {
    // Вытаскивает всю топологию конкретной компании
    @Query("SELECT c FROM Cell c JOIN FETCH c.company WHERE c.company.id = :companyId ORDER BY c.address ASC")
    List<Cell> findByCompanyId(@Param("companyId") Long companyId);

    // Проверка дубликата адреса ячейки внутри одной компании
    boolean existsByAddressAndCompanyId(String address, Long companyId);

    // Поиск ячейки по штрихкоду для ТСД кладовщика при размещении/отборе деталей
    @Query("SELECT c FROM Cell c JOIN FETCH c.company WHERE c.barcode = :barcode AND c.company.id = :companyId")
    Optional<Cell> findByBarcodeAndCompanyId(@Param("barcode") String barcode, @Param("companyId") Long companyId);
}
