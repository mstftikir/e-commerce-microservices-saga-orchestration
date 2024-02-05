package com.taltech.ecommerce.orderservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.taltech.ecommerce.orderservice.dto.InventoryDto;
import com.taltech.ecommerce.orderservice.model.Order;
import com.taltech.ecommerce.orderservice.model.OrderLineItem;
import com.taltech.ecommerce.orderservice.repository.OrderRepository;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final ObservationRegistry observationRegistry;

    public String placeOrder(Order order) {
        order.setOrderNumber(UUID.randomUUID().toString());

        List<String> skuCodes = order.getOrderLineItems().stream()
                .map(OrderLineItem::getSkuCode)
                .toList();

        Observation inventoryServiceObservation = Observation.createNotStarted("inventory-service-lookup",
                this.observationRegistry);
        inventoryServiceObservation.lowCardinalityKeyValue("call", "inventory-service");
        return inventoryServiceObservation.observe(() -> {
            InventoryDto[] inventoryDtoArray = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryDto[].class)
                    .block();

            boolean allProductsInStock = inventoryDtoArray != null && inventoryDtoArray.length > 0
                && Arrays.stream(inventoryDtoArray)
                .allMatch(InventoryDto::isInStock);

            if (allProductsInStock) {
                orderRepository.save(order);
                return "Order Placed";
            } else {
                throw new IllegalArgumentException("Product is not in stock, please try again later");
            }
        });

    }
}
