package com.taltech.ecommerce.orderservice.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.orderservice.event.InventoryEvent;
import com.taltech.ecommerce.orderservice.service.OrderService;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

    private final OrderService service;
    private final ObservationRegistry observationRegistry;

    @KafkaListener(topics = "inventoryUpdatedTopic")
    public void receiveInventoryUpdatedEvent(InventoryEvent inventoryEvent) {
        Observation.createNotStarted("on-inventory-updated-event-received", this.observationRegistry)
            .observe(() -> {
                log.info("Inventory updated event received");
                service.invoiceUpdated(inventoryEvent);
            });
    }

    @KafkaListener(topics = "inventoryUpdateFailedTopic")
    public void receiveInventoryUpdateFailedEvent(InventoryEvent inventoryEvent) {
        Observation.createNotStarted("on-inventory-update-failed-event-received", this.observationRegistry)
            .observe(() -> {
                log.info("Inventory update failed event received");
                service.invoiceUpdateFailed(inventoryEvent);
            });
    }

    @KafkaListener(topics = "inventoryRollbackedTopic")
    public void receiveInventoryRollbackedEvent(InventoryEvent inventoryEvent) {
        Observation.createNotStarted("on-inventory-rollbacked-event-received", this.observationRegistry)
            .observe(() -> {
                log.info("Inventory rollbacked event received");
                service.invoiceRollbacked(inventoryEvent);
            });
    }

    @KafkaListener(topics = "inventoryRollbackFailedTopic")
    public void receiveInventoryRollbackFailedEvent(InventoryEvent inventoryEvent) {
        Observation.createNotStarted("on-inventory-rollback-failed-event-received", this.observationRegistry)
            .observe(() -> {
                log.info("Inventory rollback failed event received");
                service.invoiceRollbackFailed(inventoryEvent);
            });
    }
}
