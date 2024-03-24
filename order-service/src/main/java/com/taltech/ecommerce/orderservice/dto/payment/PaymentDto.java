package com.taltech.ecommerce.orderservice.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {

    private Long userId;
    private String code;
    private List<PaymentItemDto> paymentItems;
    private BigDecimal totalPrice;
    private String discountId;
    private boolean active;
    private LocalDateTime insertDate;
    private LocalDateTime updateDate;
}
