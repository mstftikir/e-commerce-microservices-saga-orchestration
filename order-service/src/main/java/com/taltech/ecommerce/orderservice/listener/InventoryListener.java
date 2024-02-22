package com.taltech.ecommerce.orderservice.listener;

import java.util.concurrent.CompletableFuture;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.orderservice.event.InventoryEvent;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryListener {


    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;
    private final ObservationRegistry observationRegistry;

    @EventListener
    public void publishEvent(InventoryEvent event) {
        log.info("Inventory Event Received, Sending InventoryEvent to inventoryTopic: {}", event.getData());

        try {
            Observation.createNotStarted("inventory-topic", this.observationRegistry).observe(() -> {
                CompletableFuture<SendResult<String, InventoryEvent>> future = kafkaTemplate.send("inventoryTopic", event);
                return future.handle((result, throwable) -> CompletableFuture.completedFuture(result));
            });
        } catch (Exception exception) {
            log.error("Exception occurred while sending message to kafka, exception message: {}", exception.getMessage());
        }
    }
}