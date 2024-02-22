package com.taltech.ecommerce.paymentservice.event;

import com.taltech.ecommerce.paymentservice.dto.PaymentDto;

import lombok.Data;

@Data
public class PaymentEvent {

    private PaymentDto paymentDto;
}
