package com.taltech.ecommerce.orderservice.util;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.orderservice.model.Order;
import com.taltech.ecommerce.orderservice.model.OrderLineItem;
import com.taltech.ecommerce.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final OrderRepository orderRepository;

    @Override
    public void run(String... args) {
        if (orderRepository.count() < 2) {
            Order order1 = new Order();
            order1.setOrderNumber("Order 1");
            OrderLineItem orderLineItem1 = new OrderLineItem();
            orderLineItem1.setSkuCode("sku-code-1");
            orderLineItem1.setPrice(BigDecimal.ONE);
            orderLineItem1.setQuantity(1);
            order1.setOrderLineItems(List.of(orderLineItem1));

            orderRepository.save(order1);

            Order order2 = new Order();
            order2.setOrderNumber("Order 2");
            OrderLineItem orderLineItem2 = new OrderLineItem();
            orderLineItem2.setSkuCode("sku-code-2");
            orderLineItem2.setPrice(BigDecimal.TEN);
            orderLineItem2.setQuantity(2);
            order2.setOrderLineItems(List.of(orderLineItem2));

            orderRepository.save(order2);
        }
    }
}
