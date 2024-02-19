package com.taltech.ecommerce.orderservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.taltech.ecommerce.orderservice.dto.inventory.InventoryDto;
import com.taltech.ecommerce.orderservice.dto.payment.PaymentDto;
import com.taltech.ecommerce.orderservice.dto.payment.PaymentItemDto;
import com.taltech.ecommerce.orderservice.dto.user.UserDto;
import com.taltech.ecommerce.orderservice.model.Order;
import com.taltech.ecommerce.orderservice.repository.OrderRepository;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository repository;
    private final WebClient.Builder webClientBuilder;
    private final ObservationRegistry observationRegistry;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    @Value("${chart.service.url}")
    private String chartServiceUrl;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    public Order placeOrder(Order order) {
        validations(order);

        updateInventory(order);
        deleteChart(order);
        PaymentDto payment = savePayment(order);

        order.setTotalPrice(payment.getTotalPrice());
        addDates(order);

        return repository.save(order);
    }

    private void validations(Order order) {
        validateUser(order);
        // other validations
    }

    private void validateUser(Order order) {
        Observation userServiceObservation = Observation.createNotStarted("user-service-validation", this.observationRegistry);
        userServiceObservation.lowCardinalityKeyValue("call", "user-service");
        userServiceObservation.observe(() -> {
            UserDto userResponseDto = webClientBuilder.build().get()
                .uri(userServiceUrl + order.getUserId())
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
            if(userResponseDto == null) {
                throw new EntityNotFoundException(String.format("User is not found '%s'", order.getUserId()));
            }
        });
    }

    private void updateInventory(Order order) {
        Observation inventoryServiceObservation = Observation.createNotStarted("inventory-service-update", this.observationRegistry);
        inventoryServiceObservation.lowCardinalityKeyValue("call", "inventory-service");

        List<InventoryDto> inventoryDtos = new ArrayList<>();
        order.getOrderItems().forEach(orderItem -> inventoryDtos.add(InventoryDto.builder()
            .code(orderItem.getInventoryCode())
            .quantity(orderItem.getQuantity())
            .build())
        );
        InventoryDto[] inventoryArray = inventoryDtos.toArray(new InventoryDto[0]);
        inventoryServiceObservation.observe(() -> {
            InventoryDto[] inventoryResponseDtos = webClientBuilder.build().put()
                .uri(inventoryServiceUrl)
                .body(Mono.just(inventoryArray), InventoryDto[].class)
                .retrieve()
                .bodyToMono(InventoryDto[].class)
                .block();
            if(inventoryResponseDtos == null || inventoryResponseDtos.length == 0) {
                throw new EntityNotFoundException(String.format("Inventory is not updated '%s'", inventoryDtos));
            }
        });
    }

    private void deleteChart(Order order) {
        Observation chartServiceObservation = Observation.createNotStarted("chart-service-delete", this.observationRegistry);
        chartServiceObservation.lowCardinalityKeyValue("chart", "user-service");
        chartServiceObservation.observe(() ->
            webClientBuilder.build().delete()
                .uri(chartServiceUrl + order.getUserId())
                .retrieve()
                .toBodilessEntity()
                .block()
        );
    }

    private PaymentDto savePayment(Order order) {
        AtomicReference<PaymentDto> paymentResponseDto = new AtomicReference<>();
        Observation paymentServiceObservation = Observation.createNotStarted("payment-service-save", this.observationRegistry);
        paymentServiceObservation.lowCardinalityKeyValue("call", "payment-service");

        List<PaymentItemDto> paymentItemDtos = new ArrayList<>();
        order.getOrderItems().forEach(orderItem -> paymentItemDtos.add(PaymentItemDto.builder()
            .inventoryCode(orderItem.getInventoryCode())
            .quantity(orderItem.getQuantity())
            .price(orderItem.getPrice())
            .build()));
        PaymentDto paymentDto = PaymentDto.builder()
            .userId(order.getUserId())
            .paymentItems(paymentItemDtos)
            .build();
        paymentServiceObservation.observe(() -> {
            paymentResponseDto.set(webClientBuilder.build()
                .post()
                .uri(paymentServiceUrl)
                .body(Mono.just(paymentDto), PaymentDto.class)
                .retrieve()
                .bodyToMono(PaymentDto.class)
                .block());
            if(paymentResponseDto.get() == null) {
                throw new EntityNotFoundException(String.format("Payment is not saved '%s'", paymentResponseDto));
            }
        });
        return paymentResponseDto.get();
    }

    private void addDates(Order order) {
        order.setInsertDate(LocalDateTime.now());
        order.setUpdateDate(LocalDateTime.now());

        order.getOrderItems().forEach(paymentItem -> {
            paymentItem.setInsertDate(LocalDateTime.now());
            paymentItem.setUpdateDate(LocalDateTime.now());
        });
    }
}
