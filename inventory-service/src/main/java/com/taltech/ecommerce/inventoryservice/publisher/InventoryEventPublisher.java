package com.taltech.ecommerce.inventoryservice.publisher;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.inventoryservice.event.InventoryEvent;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventPublisher {

    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;
    private final ObservationRegistry observationRegistry;

    public void publishInventoryUpdated(InventoryEvent inventoryEvent) {
        publishEvent("inventoryUpdatedTopic", "inventory-updated-sent", inventoryEvent);
    }

    public void publishInventoryUpdateFailed(InventoryEvent inventoryEvent) {
        publishEvent("inventoryUpdateFailedTopic", "inventory-update-failed-sent", inventoryEvent);
    }

    public void publishInventoryRollbacked(InventoryEvent inventoryEvent) {
        publishEvent("inventoryRollbackedTopic", "inventory-rollbacked-sent", inventoryEvent);
    }

    public void publishInventoryRollbackFailed(InventoryEvent inventoryEvent) {
        publishEvent("inventoryRollbackFailedTopic", "inventory-rollback-failed-sent", inventoryEvent);
    }

    private void publishEvent(String topic, String observationName, InventoryEvent inventoryEvent) {
        log.info("Publishing inventory event to '{}'", topic);

        try {
            Observation.createNotStarted(observationName, this.observationRegistry).observe(() -> {
                CompletableFuture<SendResult<String, InventoryEvent>> future = kafkaTemplate.send(topic, inventoryEvent);
                return future.handle((result, throwable) -> CompletableFuture.completedFuture(result));
            });
        } catch (Exception exception) {
            log.error("Exception occurred while sending message to kafka, exception message: {}", exception.getMessage());
        }
    }
}

