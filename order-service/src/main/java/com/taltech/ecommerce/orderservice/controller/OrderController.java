package com.taltech.ecommerce.orderservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.taltech.ecommerce.orderservice.dto.OrderDto;
import com.taltech.ecommerce.orderservice.mapper.OrderMapper;
import com.taltech.ecommerce.orderservice.model.Order;
import com.taltech.ecommerce.orderservice.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService service;
    private final OrderMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void placeOrder(@RequestBody OrderDto orderDto) {
        log.info("Order request received for userId '{}'", orderDto.getUserId());

        Order orderModel = mapper.toModel(orderDto);
        service.placeOrder(orderModel);
    }
}
