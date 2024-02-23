package com.taltech.ecommerce.orderservice.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentItemDto {

    private String inventoryCode;
    private Integer quantity;
    private BigDecimal price;
    private LocalDateTime insertDate;
    private LocalDateTime updateDate;
}
