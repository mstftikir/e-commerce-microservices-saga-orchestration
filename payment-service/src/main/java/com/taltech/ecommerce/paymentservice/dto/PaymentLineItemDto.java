package com.taltech.ecommerce.paymentservice.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentLineItemDto {

    private String skuCode;
    private BigDecimal price;
    private Integer quantity;
}
