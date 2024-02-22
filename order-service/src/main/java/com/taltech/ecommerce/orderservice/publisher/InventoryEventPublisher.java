package com.taltech.ecommerce.orderservice.publisher;

import java.util.concurrent.CompletableFuture;

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
public class InventoryEventPublisher {

    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;
    private final ObservationRegistry observationRegistry;

    public void publishEvent(String topic, InventoryEvent event) {
        log.info("Publishing inventory event to '{}'", topic);

        try {
            Observation.createNotStarted(topic, this.observationRegistry).observe(() -> {
                CompletableFuture<SendResult<String, InventoryEvent>> future = kafkaTemplate.send(topic, event);
                return future.handle((result, throwable) -> CompletableFuture.completedFuture(result));
            });
        } catch (Exception exception) {
            log.error("Exception occurred while sending message to kafka, exception message: {}", exception.getMessage());
        }
    }
}
