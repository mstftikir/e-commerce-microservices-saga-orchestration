package com.taltech.ecommerce.orderservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taltech.ecommerce.orderservice.dto.inventory.InventoryDto;
import com.taltech.ecommerce.orderservice.dto.payment.PaymentDto;
import com.taltech.ecommerce.orderservice.dto.payment.PaymentItemDto;
import com.taltech.ecommerce.orderservice.event.ChartEvent;
import com.taltech.ecommerce.orderservice.event.InventoryEvent;
import com.taltech.ecommerce.orderservice.event.PaymentEvent;
import com.taltech.ecommerce.orderservice.model.Order;
import com.taltech.ecommerce.orderservice.model.OrderEvent;
import com.taltech.ecommerce.orderservice.publisher.ChartEventPublisher;
import com.taltech.ecommerce.orderservice.publisher.InventoryEventPublisher;
import com.taltech.ecommerce.orderservice.publisher.PaymentEventPublisher;
import com.taltech.ecommerce.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository repository;
    private final InventoryEventPublisher inventoryEventPublisher;
    private final ChartEventPublisher chartEventPublisher;
    private final PaymentEventPublisher paymentEventPublisher;

    public Order placeOrder(Order order) {
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setId(UUID.randomUUID().toString());
        order.setOrderEvent(orderEvent);

        publishUpdateInventory(order);
        publishDeleteChart(order);
        publishSavePayment(order);

        addDates(order);
        return repository.save(order);
    }

    private void publishUpdateInventory(Order order) {
        List<InventoryDto> inventoryDtoList = new ArrayList<>();
        order.getOrderItems().forEach(orderItem -> inventoryDtoList.add(InventoryDto.builder()
            .code(orderItem.getInventoryCode())
            .quantity(orderItem.getQuantity())
            .build())
        );
        InventoryEvent inventoryEvent = InventoryEvent.builder()
            .eventId(order.getOrderEvent().getId())
            .inventoryList(inventoryDtoList)
            .build();
        inventoryEventPublisher.publishUpdateInventory(inventoryEvent);
    }

    private void publishDeleteChart(Order order) {
        ChartEvent chartEvent = ChartEvent.builder()
            .eventId(order.getOrderEvent().getId())
            .userId(order.getUserId())
            .build();
        chartEventPublisher.publishDeleteChart(chartEvent);
    }

    private void publishSavePayment(Order order) {
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
        PaymentEvent paymentEvent = PaymentEvent.builder()
            .eventId(order.getOrderEvent().getId())
            .payment(paymentDto)
            .build();
        paymentEventPublisher.publishSavePayment(paymentEvent);
    }

    private void addDates(Order order) {
        order.setInsertDate(LocalDateTime.now());
        order.setUpdateDate(LocalDateTime.now());

        order.getOrderItems().forEach(paymentItem -> {
            paymentItem.setInsertDate(LocalDateTime.now());
            paymentItem.setUpdateDate(LocalDateTime.now());
        });
    }

    public void inventoryUpdated(InventoryEvent inventoryEvent) {
        log.info("invoiceUpdated {}", inventoryEvent);
        //Save inv updated by id
    }

    public void inventoryUpdateFailed(InventoryEvent inventoryEvent) {
        log.info("invoiceUpdateFailed {}", inventoryEvent);
        //call rollbacks
    }

    public void inventoryRollbacked(InventoryEvent inventoryEvent) {
        log.info("invoiceRollbacked {}", inventoryEvent);
        //save as rollbacked
    }

    public void inventoryRollbackFailed(InventoryEvent inventoryEvent) {
        log.info("invoiceRollbackFailed {}", inventoryEvent);
        //call rollback again
    }

    public void chartDeleted(ChartEvent chartEvent) {
        log.info("chartDeleted {}", chartEvent);
        //Save chart deleted by id
    }

    public void chartDeleteFailed(ChartEvent chartEvent) {
        log.info("chartDeleteFailed {}", chartEvent);
        //call rollbacks
    }

    public void chartRollbacked(ChartEvent chartEvent) {
        log.info("chartRollbacked {}", chartEvent);
        //save as rollbacked
    }

    public void chartRollbackFailed(ChartEvent chartEvent) {
        log.info("chartRollbackFailed {}", chartEvent);
        //call rollback again
    }

    public void paymentSaved(PaymentEvent paymentEvent) {
        log.info("chartRollbackFailed {}", paymentEvent);
        //Save payment saved by id
    }

    public void paymentSaveFailed(PaymentEvent paymentEvent) {
        log.info("paymentSaveFailed {}", paymentEvent);
        //call rollbacks

    }

    public void paymentRollbacked(PaymentEvent paymentEvent) {
        log.info("paymentRollbacked {}", paymentEvent);
        //save as rollbacked

    }

    public void paymentRollbackFailed(PaymentEvent paymentEvent) {
        log.info("paymentRollbackFailed {}", paymentEvent);
        //call rollback again
    }
}
