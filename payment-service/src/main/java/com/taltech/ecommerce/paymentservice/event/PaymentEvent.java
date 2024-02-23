package com.taltech.ecommerce.paymentservice.event;

import com.taltech.ecommerce.paymentservice.dto.PaymentDto;

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
