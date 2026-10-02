package ru.wms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.wms.dto.request.product.CreateProductRequest;
import ru.wms.dto.request.product.UpdateProductRequest;
import ru.wms.dto.response.ProductDto;
import ru.wms.model.User;
import ru.wms.service.ProductService;
import java.util.List;


@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    /**
     * Получить каталог товаров вашей организации.
     */
    @GetMapping
    public ResponseEntity<List<ProductDto>> getMyProducts(@AuthenticationPrincipal User currentUser) {
        List<ProductDto> products = productService.getProductsByCompany(currentUser.getCompany());
        return ResponseEntity.ok(products);
    }
    
    /**
     * Создать новый товар в каталоге.
     */
    @PostMapping
    public ResponseEntity<ProductDto> addProduct(
            @Valid @RequestBody CreateProductRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        ProductDto created = productService.createProduct(request, currentUser.getCompany());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Изменить данные товара в каталоге.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable("id") @NonNull String  productId,
            @Valid @RequestBody UpdateProductRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        ProductDto response = productService.updateProduct(productId, request, currentUser.getCompany());
        return ResponseEntity.ok(response);
    }

    /**
     * Удалить товар из каталога.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable("id") @NonNull String productId,
            @AuthenticationPrincipal User currentUser
    ) {
        productService.deleteProduct(productId, currentUser.getCompany());
        return ResponseEntity.noContent().build(); // Возвращает статус 204 No Content при успешном удалении
    }
}
