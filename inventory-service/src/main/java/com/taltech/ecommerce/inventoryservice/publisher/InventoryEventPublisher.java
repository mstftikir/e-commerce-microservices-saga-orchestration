package com.taltech.ecommerce.inventoryservice.publisher;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.inventoryservice.dto.InventoryDto;
import com.taltech.ecommerce.inventoryservice.event.InventoryEvent;
import com.taltech.ecommerce.inventoryservice.mapper.InventoryMapper;
import com.taltech.ecommerce.inventoryservice.model.Inventory;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventPublisher {

    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;
    private final InventoryMapper mapper;
    private final ObservationRegistry observationRegistry;

    public void publishInventoryUpdated(List<Inventory> inventoryList) {
        publishEvent("inventoryUpdatedTopic", "inventory-updated-received", inventoryList);
    }

    public void publishInventoryUpdateFailed(List<Inventory> inventoryList) {
        publishEvent("inventoryUpdateFailedTopic", "inventory-update-failed-received", inventoryList);
    }

    public void publishInventoryRollbacked(List<Inventory> inventoryList) {
        publishEvent("inventoryRollbackedTopic", "inventory-rollbacked-received", inventoryList);
    }

    public void publishInventoryRollbackFailed(List<Inventory> inventoryList) {
        publishEvent("inventoryRollbackFailedTopic", "inventory-rollback-failed-received", inventoryList);
    }

    private void publishEvent(String topic, String observationName, List<Inventory> inventoryList) {
        log.info("Publishing inventory event to '{}'", topic);

        try {
            Observation.createNotStarted(observationName, this.observationRegistry).observe(() -> {
                List<InventoryDto> inventoryDtoList = mapper.toDtoList(inventoryList);
                InventoryEvent inventoryEvent = InventoryEvent.builder().inventoryList(inventoryDtoList).build();
                CompletableFuture<SendResult<String, InventoryEvent>> future = kafkaTemplate.send(topic, inventoryEvent);
                return future.handle((result, throwable) -> CompletableFuture.completedFuture(result));
            });
        } catch (Exception exception) {
            log.error("Exception occurred while sending message to kafka, exception message: {}", exception.getMessage());
        }
    }
}

