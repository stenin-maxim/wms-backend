package ru.wms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.wms.dto.request.CreateProductRequest;
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
}
