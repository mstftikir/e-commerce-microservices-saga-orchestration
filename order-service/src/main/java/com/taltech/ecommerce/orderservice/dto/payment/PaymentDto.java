package com.taltech.ecommerce.orderservice.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentDto {

    private Long userId;
    private String code;
    private List<PaymentItemDto> paymentItems;
    private BigDecimal totalPrice;
    private boolean active;
    private LocalDateTime insertDate;
    private LocalDateTime updateDate;
}
