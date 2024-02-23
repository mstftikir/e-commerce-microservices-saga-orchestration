package com.taltech.ecommerce.orderservice.event;

import com.taltech.ecommerce.orderservice.dto.payment.PaymentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {

    private String eventId;
    private PaymentDto payment;
}
