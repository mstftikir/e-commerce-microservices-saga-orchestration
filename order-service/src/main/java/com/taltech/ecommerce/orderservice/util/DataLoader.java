package com.taltech.ecommerce.orderservice.util;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.orderservice.model.Order;
import com.taltech.ecommerce.orderservice.model.OrderLineItems;
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
            OrderLineItems orderLineItems1 = new OrderLineItems();
            orderLineItems1.setSkuCode("sku-code-1");
            orderLineItems1.setPrice(BigDecimal.ONE);
            orderLineItems1.setQuantity(1);
            order1.setOrderLineItemsList(List.of(orderLineItems1));

            orderRepository.save(order1);

            Order order2 = new Order();
            order2.setOrderNumber("Order 2");
            OrderLineItems orderLineItems2 = new OrderLineItems();
            orderLineItems2.setSkuCode("sku-code-2");
            orderLineItems2.setPrice(BigDecimal.TEN);
            orderLineItems2.setQuantity(2);
            order2.setOrderLineItemsList(List.of(orderLineItems2));

            orderRepository.save(order2);
        }
    }
}
