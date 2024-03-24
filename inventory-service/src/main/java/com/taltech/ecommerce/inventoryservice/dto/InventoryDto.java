package com.taltech.ecommerce.inventoryservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class InventoryDto {

    private String code;
    private String name;
    private String description;
    private BigDecimal quantity;
    private BigDecimal price;
    private LocalDateTime insertDate;
    private LocalDateTime updateDate;
}
