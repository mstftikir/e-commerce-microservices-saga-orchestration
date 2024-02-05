package com.taltech.ecommerce.orderservice.mapper;

import org.mapstruct.Mapper;

import com.taltech.ecommerce.orderservice.dto.OrderDto;
import com.taltech.ecommerce.orderservice.model.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toModel(OrderDto dto);
}
