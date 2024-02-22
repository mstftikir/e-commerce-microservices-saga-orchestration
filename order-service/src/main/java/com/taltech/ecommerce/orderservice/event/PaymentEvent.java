package com.taltech.ecommerce.orderservice.event;

import com.taltech.ecommerce.orderservice.dto.payment.PaymentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {

    private PaymentDto paymentDto;
}
