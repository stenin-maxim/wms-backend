package ru.wms.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import ru.wms.model.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
    
    @Query("SELECT p FROM Product p JOIN FETCH p.company WHERE p.company.id = :companyId")
    List<Product> findByCompanyId(@Param("companyId") Long companyId);

    // Проверка дубликата по SKU и бренду внутри одной компании
    boolean existsBySkuAndBrandAndCompanyId(String sku, String brand, Long companyId);

    // Поиск детали по штрихкоду для ТСД кладовщика
    @Query("SELECT p FROM Product p JOIN FETCH p.company WHERE p.barcode = :barcode AND p.company.id = :companyId")
    Optional<Product> findByBarcodeAndCompanyId(@Param("barcode") String barcode, @Param("companyId") Long companyId);
}
