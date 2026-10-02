package ru.wms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CellDto {
    private String id;
    private String address;
    private String zoneName;
    private String rack;
    private String shelf;
    private String position;
    private String barcode;
    private boolean isBlocked;
}
