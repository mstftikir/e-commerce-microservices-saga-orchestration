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
    public void receiveInventoryUpdated(InventoryEvent inventoryEvent) {
        Observation.createNotStarted("inventory-updated-received", this.observationRegistry)
            .observe(() -> {
                log.info("Inventory updated event received");
                service.inventoryUpdated(inventoryEvent);
            });
    }

    @KafkaListener(topics = "inventoryUpdateFailedTopic")
    public void receiveInventoryUpdateFailed(InventoryEvent inventoryEvent) {
        Observation.createNotStarted("inventory-update-failed-received", this.observationRegistry)
            .observe(() -> {
                log.info("Inventory update failed event received");
                service.inventoryUpdateFailed(inventoryEvent);
            });
    }

    @KafkaListener(topics = "inventoryRollbackedTopic")
    public void receiveInventoryRollbacked(InventoryEvent inventoryEvent) {
        Observation.createNotStarted("inventory-rollbacked-received", this.observationRegistry)
            .observe(() -> {
                log.info("Inventory rollbacked event received");
                service.inventoryRollbacked(inventoryEvent);
            });
    }

    @KafkaListener(topics = "inventoryRollbackFailedTopic")
    public void receiveInventoryRollbackFailed(InventoryEvent inventoryEvent) {
        Observation.createNotStarted("inventory-rollback-failed-received", this.observationRegistry)
            .observe(() -> {
                log.info("Inventory rollback failed event received");
                service.inventoryRollbackFailed(inventoryEvent);
            });
    }
}
