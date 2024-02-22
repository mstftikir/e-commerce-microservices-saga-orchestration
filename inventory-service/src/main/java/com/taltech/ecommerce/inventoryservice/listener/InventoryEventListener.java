package com.taltech.ecommerce.inventoryservice.listener;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.inventoryservice.event.InventoryEvent;
import com.taltech.ecommerce.inventoryservice.mapper.InventoryMapper;
import com.taltech.ecommerce.inventoryservice.model.Inventory;
import com.taltech.ecommerce.inventoryservice.service.InventoryService;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

    private final InventoryService service;
    private final InventoryMapper mapper;
    private final ObservationRegistry observationRegistry;

    @KafkaListener(topics = "updateInventoryTopic")
    public void receiveUpdateInventoryEvent(InventoryEvent inventoryEvent) {
        Observation.createNotStarted("on-update-inventory-event-received", this.observationRegistry)
            .observe(() -> {
                log.info("Update inventory event received");
                List<Inventory> inventoryList = mapper.toModelList(inventoryEvent.getInventoryList());
                service.commitUpdate(inventoryList);
            });
    }

    @KafkaListener(topics = "rollbackInventoryTopic")
    public void receiveRollbackInventoryEvent(InventoryEvent inventoryEvent) {
        Observation.createNotStarted("on-rollback-inventory-event-received", this.observationRegistry)
            .observe(() -> {
                log.info("Rollback inventory event received");
                List<Inventory> inventoryList = mapper.toModelList(inventoryEvent.getInventoryList());
                service.rollbackUpdate(inventoryList);
            });
    }
}
