package com.taltech.ecommerce.inventoryservice.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.inventoryservice.event.InventoryEvent;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryListener {

    private final ObservationRegistry observationRegistry;

    @KafkaListener(topics = "inventoryTopic")
    public void receiveEvent(InventoryEvent inventoryEvent) {
        Observation.createNotStarted("on-inventory-message-received", this.observationRegistry)
            .observe(() -> log.info("Got message '{}'", inventoryEvent.getData()));
    }
}
