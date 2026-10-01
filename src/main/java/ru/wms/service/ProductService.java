package ru.wms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.wms.dto.request.CreateProductRequest;
import ru.wms.dto.response.ProductDto;
import ru.wms.model.Company;
import ru.wms.model.Product;
import ru.wms.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    /**
     * Получить всю номенклатуру товаров текущей компании.
     */
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByCompany(Company company) {
        return productRepository.findByCompanyId(company.getId()).stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    /**
     * Добавить новую деталь в каталог склада.
     */
    @Transactional
    public ProductDto createProduct(CreateProductRequest request, Company company) {

        String cleanSku = request.getSku().toUpperCase().trim();
        String cleanBrand = request.getBrand().trim();

        // Проверяем, нет ли уже такой детали у этой компании
        if (productRepository.existsBySkuAndBrandAndCompanyId(cleanSku, cleanBrand, company.getId())) {
            throw new IllegalArgumentException("Запчасть с таким артикулом и брендом уже есть в каталоге вашего склада");
        }

        // Создаем и сохраняем сущность
        Product product = Product.builder()
            .name(request.getName())
            .sku(request.getSku().toUpperCase().trim())
            .brand(request.getBrand().trim())
            .barcode(request.getBarcode() != null ? request.getBarcode().trim() : null)
            .company(company)
            .build();

        @SuppressWarnings("null")
        Product savedProduct = productRepository.save(product);

        return mapToDto(savedProduct);
    }

    private ProductDto mapToDto(Product product) {
        return ProductDto.builder()
            .id(product.getId())
            .name(product.getName())
            .sku(product.getSku())
            .brand(product.getBrand())
            .barcode(product.getBarcode())
            .build();
    }
}
