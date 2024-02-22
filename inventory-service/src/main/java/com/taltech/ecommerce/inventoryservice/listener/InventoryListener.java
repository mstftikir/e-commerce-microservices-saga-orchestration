package com.taltech.ecommerce.inventoryservice.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.inventoryservice.event.InventoryEvent;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryListener {

    private final ObservationRegistry observationRegistry;

    @KafkaListener(topics = "updateInventoryTopic")
    public void receiveUpdateInventoryEvent(InventoryEvent inventoryEvent) {
        Observation.createNotStarted("on-update-inventory-message-received", this.observationRegistry)
            .observe(() -> log.info("Update inventory with inventory code '{}'", inventoryEvent.getInventoryDto().getCode()));
    }

    @KafkaListener(topics = "rollbackInventoryTopic")
    public void receiveRollbackInventoryEvent(InventoryEvent inventoryEvent) {
        Observation.createNotStarted("on-rollback-inventory-message-received", this.observationRegistry)
            .observe(() -> log.info("Rollback inventory with inventory code '{}'", inventoryEvent.getInventoryDto().getCode()));
    }
}
