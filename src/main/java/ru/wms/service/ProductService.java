package ru.wms.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.wms.dto.request.product.CreateProductRequest;
import ru.wms.dto.request.product.UpdateProductRequest;
import ru.wms.dto.response.ProductDto;
import ru.wms.model.Company;
import ru.wms.model.Product;
import ru.wms.repository.ProductRepository;

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
        String cleanBarcode = request.getBarcode() != null ? request.getBarcode().trim() : null;

        // Проверяем, нет ли уже такой детали у этой компании
        if (productRepository.existsBySkuAndBrandAndCompanyId(cleanSku, cleanBrand, company.getId())) {
            throw new IllegalArgumentException("Запчасть с таким артикулом и брендом уже есть в каталоге вашего склада");
        }

        // Создаем и сохраняем сущность
        Product product = Product.builder()
            .name(request.getName())
            .sku(cleanSku)
            .brand(cleanBrand)
            .barcode(cleanBarcode)
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

    /**
     * Редактирование товара с проверкой SaaS-прав доступа.
     */
    @Transactional
    public ProductDto updateProduct(@NonNull String productId, UpdateProductRequest request, Company company) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Товар с указанным ID не найден"));

        // Защита от попыток отредактировать товар чужого склада
        if (!product.getCompany().getId().equals(company.getId())) {
            throw new IllegalArgumentException("Доступ запрещен: этот товар принадлежит другой организации");
        }

        String newSku = request.getSku().toUpperCase().trim();
        String newBrand = request.getBrand().trim();
        
        if (!product.getSku().equals(newSku) || !product.getBrand().equalsIgnoreCase(newBrand)) {
            if (productRepository.existsBySkuAndBrandAndCompanyId(newSku, newBrand, company.getId())) {
                throw new IllegalArgumentException("Запчасть с таким артикулом и брендом уже существует в вашем каталоге");
            }
        }

        product.setName(request.getName());
        product.setSku(newSku);
        product.setBrand(newBrand);
        product.setBarcode(request.getBarcode() != null ? request.getBarcode().trim() : null);

        Product updatedProduct = productRepository.save(product);
        return mapToDto(updatedProduct);
    }

    /**
     * Удаление товара из каталога номенклатуры.
     */
    @Transactional
    public void deleteProduct(@NonNull String productId, Company company) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Товар с указанным ID не найден"));

        // Защита чужих данных
        if (!product.getCompany().getId().equals(company.getId())) {
            throw new IllegalArgumentException("Доступ запрещен: вы не можете удалить товар другой организации");
        }

        // TODO в будущем: Проверить остатки товара в CellRepository (если > 0, запретить удаление)

        productRepository.delete(product);
    }

}
