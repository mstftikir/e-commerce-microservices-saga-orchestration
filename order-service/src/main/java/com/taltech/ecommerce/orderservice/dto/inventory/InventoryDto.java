package com.taltech.ecommerce.orderservice.dto.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryDto {

    private String code;
    private String name;
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private LocalDateTime insertDate;
    private LocalDateTime updateDate;
}
