package com.taltech.ecommerce.orderservice.event;

import com.taltech.ecommerce.orderservice.dto.payment.PaymentDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentEvent {

    private PaymentDto paymentDto;
}
