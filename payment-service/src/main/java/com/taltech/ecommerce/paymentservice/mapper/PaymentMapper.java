package com.taltech.ecommerce.paymentservice.mapper;

import org.mapstruct.Mapper;

import com.taltech.ecommerce.paymentservice.dto.PaymentDto;
import com.taltech.ecommerce.paymentservice.model.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentDto toDto(Payment payment);

    Payment toModel(PaymentDto dto);
}
