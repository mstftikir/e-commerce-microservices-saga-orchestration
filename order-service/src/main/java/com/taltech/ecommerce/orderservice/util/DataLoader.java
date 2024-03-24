package com.taltech.ecommerce.orderservice.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.orderservice.enumeration.EventStatus;
import com.taltech.ecommerce.orderservice.model.Order;
import com.taltech.ecommerce.orderservice.model.OrderEvent;
import com.taltech.ecommerce.orderservice.model.OrderItem;
import com.taltech.ecommerce.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final OrderRepository orderRepository;

    @Override
    public void run(String... args) {
        if (orderRepository.count() < 2) {
            //Order 1
            OrderItem orderItem = new OrderItem();
            orderItem.setInventoryCode("iphone_13");
            orderItem.setQuantity(1);
            orderItem.setPrice(new BigDecimal(1000));
            orderItem.setInsertDate(LocalDateTime.now());
            orderItem.setUpdateDate(LocalDateTime.now());

            OrderItem orderItem2 = new OrderItem();
            orderItem2.setInventoryCode("samsung_a12");
            orderItem2.setQuantity(1);
            orderItem2.setPrice(new BigDecimal(800));
            orderItem2.setInsertDate(LocalDateTime.now());
            orderItem2.setUpdateDate(LocalDateTime.now());

            Order order = new Order();
            OrderEvent orderEvent = new OrderEvent();
            orderEvent.setId(UUID.randomUUID().toString());
            orderEvent.setInventoryStatus(EventStatus.SUCCESSFUL);
            order.setOrderEvent(orderEvent);
            order.setUserId(12345L);
            order.setOrderItems(List.of(orderItem, orderItem2));
            order.setPaymentCode(UUID.randomUUID().toString());
            order.setInsertDate(LocalDateTime.now());
            order.setUpdateDate(LocalDateTime.now());
            orderRepository.save(order);

            //Order 2
            OrderItem orderItem3 = new OrderItem();
            orderItem3.setInventoryCode("iphone_13");
            orderItem3.setQuantity(2);
            orderItem3.setPrice(new BigDecimal(1000));
            orderItem3.setInsertDate(LocalDateTime.now());
            orderItem3.setUpdateDate(LocalDateTime.now());

            OrderItem orderItem4 = new OrderItem();
            orderItem4.setInventoryCode("samsung_a12");
            orderItem4.setQuantity(2);
            orderItem4.setPrice(new BigDecimal(800));
            orderItem4.setInsertDate(LocalDateTime.now());
            orderItem4.setUpdateDate(LocalDateTime.now());

            Order order2 = new Order();
            OrderEvent orderEvent2 = new OrderEvent();
            orderEvent2.setId(UUID.randomUUID().toString());
            orderEvent2.setInventoryStatus(EventStatus.SUCCESSFUL);
            order2.setOrderEvent(orderEvent2);
            order2.setUserId(67890L);
            order2.setOrderItems(List.of(orderItem3, orderItem4));
            order2.setPaymentCode(UUID.randomUUID().toString());
            order2.setInsertDate(LocalDateTime.now());
            order2.setUpdateDate(LocalDateTime.now());
            orderRepository.save(order2);
        }
    }
}
