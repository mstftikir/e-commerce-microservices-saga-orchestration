package com.taltech.ecommerce.paymentservice.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private BigDecimal totalPrice;
    private List<PaymentLineItemDto> paymentLineItems;
}
