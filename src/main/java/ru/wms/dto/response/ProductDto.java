package ru.wms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDto {
    private String id;
    private String name;
    private String sku;
    private String brand;
    private String barcode;
}
