package com.taltech.ecommerce.orderservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.taltech.ecommerce.orderservice.dto.inventory.InventoryDto;
import com.taltech.ecommerce.orderservice.dto.payment.PaymentDto;
import com.taltech.ecommerce.orderservice.dto.payment.PaymentItemDto;
import com.taltech.ecommerce.orderservice.dto.user.UserDto;
import com.taltech.ecommerce.orderservice.enumeration.EventStatus;
import com.taltech.ecommerce.orderservice.event.ChartEvent;
import com.taltech.ecommerce.orderservice.event.InventoryEvent;
import com.taltech.ecommerce.orderservice.event.PaymentEvent;
import com.taltech.ecommerce.orderservice.model.Order;
import com.taltech.ecommerce.orderservice.model.OrderEvent;
import com.taltech.ecommerce.orderservice.publisher.ChartEventPublisher;
import com.taltech.ecommerce.orderservice.publisher.InventoryEventPublisher;
import com.taltech.ecommerce.orderservice.publisher.PaymentEventPublisher;
import com.taltech.ecommerce.orderservice.repository.OrderRepository;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Retryable
@Slf4j
public class OrderService {

    private final OrderRepository repository;
    private final WebClient.Builder webClientBuilder;
    private final ObservationRegistry observationRegistry;

    private final InventoryEventPublisher inventoryEventPublisher;
    private final ChartEventPublisher chartEventPublisher;
    private final PaymentEventPublisher paymentEventPublisher;


    @Value("${user.service.url}")
    private String userServiceUrl;
    public void placeOrder(Order order) {
        validations(order);

        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setId(UUID.randomUUID().toString());
        order.setOrderEvent(orderEvent);

        addDates(order);
        repository.save(order);

        log.info("Publishing events with eventId '{}'", order.getOrderEvent().getId());
        publishUpdateInventory(order);
        publishDeleteChart(order);
        publishSavePayment(order);
    }

    public void inventoryUpdated(InventoryEvent inventoryEvent) {
        Order order = findOrderByEventId(inventoryEvent.getEventId());
        order.getOrderEvent().setInventoryStatus(EventStatus.SUCCESSFUL);
        repository.saveAndFlush(order);
    }

    public void inventoryUpdateFailed(InventoryEvent inventoryEvent) {
        Order order = findOrderByEventId(inventoryEvent.getEventId());
        order.getOrderEvent().setInventoryStatus(EventStatus.FAILED);
        repository.saveAndFlush(order);

        log.info("Publishing rollbackChart and rollbackPayment events");
        publishRollbackChart(order);
        publishRollbackPayment(order);
    }

    public void inventoryRollbacked(InventoryEvent inventoryEvent) {
        Order order = findOrderByEventId(inventoryEvent.getEventId());
        order.getOrderEvent().setInventoryStatus(EventStatus.ROLLBACK);
        repository.saveAndFlush(order);
    }

    public void inventoryRollbackFailed(InventoryEvent inventoryEvent) {
        Order order = findOrderByEventId(inventoryEvent.getEventId());
        order.getOrderEvent().setInventoryStatus(EventStatus.ROLLBACK_FAILED);
        repository.saveAndFlush(order);
    }

    public void chartDeleted(ChartEvent chartEvent) {
        Order order = findOrderByEventId(chartEvent.getEventId());
        order.getOrderEvent().setChartStatus(EventStatus.SUCCESSFUL);
        repository.saveAndFlush(order);
    }

    public void chartDeleteFailed(ChartEvent chartEvent) {
        Order order = findOrderByEventId(chartEvent.getEventId());
        order.getOrderEvent().setChartStatus(EventStatus.FAILED);
        repository.saveAndFlush(order);

        log.info("Publishing rollbackInventory and rollbackPayment events");
        publishRollbackInventory(order);
        publishRollbackPayment(order);
    }

    public void chartRollbacked(ChartEvent chartEvent) {
        Order order = findOrderByEventId(chartEvent.getEventId());
        order.getOrderEvent().setChartStatus(EventStatus.ROLLBACK);
        repository.saveAndFlush(order);
    }

    public void chartRollbackFailed(ChartEvent chartEvent) {
        Order order = findOrderByEventId(chartEvent.getEventId());
        order.getOrderEvent().setChartStatus(EventStatus.ROLLBACK_FAILED);
        repository.saveAndFlush(order);
    }

    public void paymentSaved(PaymentEvent paymentEvent) {
        Order order = findOrderByEventId(paymentEvent.getEventId());
        order.getOrderEvent().setPaymentStatus(EventStatus.SUCCESSFUL);
        order.setPaymentCode(paymentEvent.getPayment().getCode());
        order.setTotalPrice(paymentEvent.getPayment().getTotalPrice());
        repository.saveAndFlush(order);
    }

    public void paymentSaveFailed(PaymentEvent paymentEvent) {
        Order order = findOrderByEventId(paymentEvent.getEventId());
        order.getOrderEvent().setPaymentStatus(EventStatus.FAILED);
        repository.saveAndFlush(order);

        log.info("Publishing rollbackInventory and rollbackChart events");
        publishRollbackInventory(order);
        publishRollbackChart(order);

    }

    public void paymentRollbacked(PaymentEvent paymentEvent) {
        Order order = findOrderByEventId(paymentEvent.getEventId());
        order.getOrderEvent().setPaymentStatus(EventStatus.ROLLBACK);
        repository.saveAndFlush(order);

    }

    public void paymentRollbackFailed(PaymentEvent paymentEvent) {
        Order order = findOrderByEventId(paymentEvent.getEventId());
        order.getOrderEvent().setPaymentStatus(EventStatus.ROLLBACK_FAILED);
        repository.saveAndFlush(order);
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
                throw new EntityNotFoundException(String.format("User '%s' is not found", order.getUserId()));
            }
        });
    }

    private void publishUpdateInventory(Order order) {
        InventoryEvent inventoryEvent = getInventoryEvent(order);
        inventoryEventPublisher.publishUpdateInventory(inventoryEvent);
    }

    private void publishRollbackInventory(Order order) {
        InventoryEvent inventoryEvent = getInventoryEvent(order);
        inventoryEventPublisher.publishRollbackInventory(inventoryEvent);
    }

    private InventoryEvent getInventoryEvent(Order order) {
        List<InventoryDto> inventoryDtoList = new ArrayList<>();
        order.getOrderItems().forEach(orderItem -> inventoryDtoList.add(InventoryDto.builder()
            .code(orderItem.getInventoryCode())
            .quantity(orderItem.getQuantity())
            .build())
        );
        return InventoryEvent.builder()
            .eventId(order.getOrderEvent().getId())
            .inventoryList(inventoryDtoList)
            .build();
    }

    private void publishDeleteChart(Order order) {
        ChartEvent chartEvent = getChartEvent(order);
        chartEventPublisher.publishDeleteChart(chartEvent);
    }

    private void publishRollbackChart(Order order) {
        ChartEvent chartEvent = getChartEvent(order);
        chartEventPublisher.publishRollbackChart(chartEvent);
    }

    private ChartEvent getChartEvent(Order order) {
        return ChartEvent.builder()
            .eventId(order.getOrderEvent().getId())
            .userId(order.getUserId())
            .build();
    }

    private void publishSavePayment(Order order) {
        PaymentEvent paymentEvent = getPaymentEvent(order);
        paymentEventPublisher.publishSavePayment(paymentEvent);
    }

    private void publishRollbackPayment(Order order) {
        PaymentEvent paymentEvent = getPaymentEvent(order);
        paymentEventPublisher.publishRollbackPayment(paymentEvent);
    }

    private PaymentEvent getPaymentEvent(Order order) {
        String paymentCode = order.getPaymentCode() == null
            ? UUID.randomUUID().toString()
            : order.getPaymentCode();
        List<PaymentItemDto> paymentItemDtos = new ArrayList<>();
        order.getOrderItems().forEach(orderItem -> paymentItemDtos.add(PaymentItemDto.builder()
            .inventoryCode(orderItem.getInventoryCode())
            .quantity(orderItem.getQuantity())
            .price(orderItem.getPrice())
            .build()));
        PaymentDto paymentDto = PaymentDto.builder()
            .code(paymentCode)
            .userId(order.getUserId())
            .paymentItems(paymentItemDtos)
            .build();
        return PaymentEvent.builder()
            .eventId(order.getOrderEvent().getId())
            .payment(paymentDto)
            .build();
    }

    private void addDates(Order order) {
        order.setInsertDate(LocalDateTime.now());
        order.setUpdateDate(LocalDateTime.now());

        order.getOrderItems().forEach(paymentItem -> {
            paymentItem.setInsertDate(LocalDateTime.now());
            paymentItem.setUpdateDate(LocalDateTime.now());
        });
    }

    private Order findOrderByEventId(String eventId) {
        return repository.findByOrderEventId(eventId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Order not found by eventId '%s'",
                eventId)));
    }
}
