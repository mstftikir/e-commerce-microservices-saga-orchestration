package com.taltech.ecommerce.orderservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.taltech.ecommerce.orderservice.dto.inventory.InventoryDto;
import com.taltech.ecommerce.orderservice.dto.payment.PaymentDto;
import com.taltech.ecommerce.orderservice.dto.payment.PaymentItemDto;
import com.taltech.ecommerce.orderservice.dto.user.UserDto;
import com.taltech.ecommerce.orderservice.exception.OrderNotPlacedException;
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

    private static final String PREPARE = "prepare";
    private static final String COMMIT = "commit";
    private static final String ROLLBACK = "rollback";
    private static final String SERVICES_FAILED_MESSAGE = "{} - {} failed with message: {}";
    private static final String STARTING_ROLLBACK_MESSAGE = "Starting rollback for {}";

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

        //Prepare phase
        updateInventory(PREPARE, order);
        deleteChart(PREPARE, order);
        savePayment(PREPARE, order);

        ///Commit Phase
        updateInventory(COMMIT, order);

        try {
            deleteChart(COMMIT, order);
        }
        catch (Exception exception) {
            log.error(SERVICES_FAILED_MESSAGE, COMMIT, "deleteChart", exception.getMessage());
            log.error(STARTING_ROLLBACK_MESSAGE, "updateInventory");
            updateInventory(ROLLBACK, order);
            throw new OrderNotPlacedException("deleteChart has been failed, updateInventory has been rollbacked");
        }

        PaymentDto commitPayment;
        try {
            commitPayment = savePayment(COMMIT, order);
        }
        catch (Exception exception) {
            log.error(SERVICES_FAILED_MESSAGE, COMMIT, "commitPayment", exception.getMessage());
            log.error(STARTING_ROLLBACK_MESSAGE, "deleteChart and updateInventory");
            deleteChart(ROLLBACK, order);
            updateInventory(ROLLBACK, order);
            throw new OrderNotPlacedException("commitPayment has been failed, deleteChart and updateInventory has been rollbacked");
        }

        order.setTotalPrice(commitPayment.getTotalPrice());
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

    private void updateInventory(String action, Order order) {
        Observation inventoryServiceObservation = Observation.createNotStarted(action + "-inventory-service-update", this.observationRegistry);
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
                .uri(inventoryServiceUrl + action)
                .body(Mono.just(inventoryArray), InventoryDto[].class)
                .retrieve()
                .bodyToMono(InventoryDto[].class)
                .block();
            if(inventoryResponseDtos == null || inventoryResponseDtos.length == 0) {
                throw new EntityNotFoundException(String.format("%s - Inventory is not updated '%s'", action, inventoryDtos));
            }
        });
    }

    private void deleteChart(String action, Order order) {
        Observation chartServiceObservation = Observation.createNotStarted(action + "-chart-service-delete", this.observationRegistry);
        chartServiceObservation.lowCardinalityKeyValue("chart", "user-service");
        chartServiceObservation.observe(() -> {
            webClientBuilder.build().delete()
                .uri(chartServiceUrl + action + "/" + order.getUserId())
                .retrieve()
                .toBodilessEntity()
                .block();
                return true;
            });
    }

    private PaymentDto savePayment(String action, Order order) {
        Observation paymentServiceObservation = Observation.createNotStarted(action + "-payment-service-save", this.observationRegistry);
        paymentServiceObservation.lowCardinalityKeyValue("call", "payment-service");

        List<PaymentItemDto> paymentItemDtos = new ArrayList<>();
        order.getOrderItems().forEach(orderItem -> paymentItemDtos.add(PaymentItemDto.builder()
            .inventoryCode(orderItem.getInventoryCode())
            .quantity(orderItem.getQuantity())
            .price(orderItem.getPrice())
            .build()));
        PaymentDto paymentDto = PaymentDto.builder()
            .code(UUID.randomUUID().toString())
            .userId(order.getUserId())
            .paymentItems(paymentItemDtos)
            .build();
        AtomicReference<PaymentDto> paymentResponseDto = new AtomicReference<>();
        paymentServiceObservation.observe(() -> {
            paymentResponseDto.set(webClientBuilder.build()
                .post()
                .uri(paymentServiceUrl + action)
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
