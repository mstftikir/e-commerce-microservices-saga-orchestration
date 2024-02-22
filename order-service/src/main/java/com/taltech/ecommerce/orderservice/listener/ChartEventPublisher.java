package com.taltech.ecommerce.orderservice.listener;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.orderservice.event.ChartEvent;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChartEventPublisher {


    private final KafkaTemplate<String, ChartEvent> kafkaTemplate;
    private final ObservationRegistry observationRegistry;

    public void publishEvent(String topic, ChartEvent event) {
        log.info("Publishing ChartEvent to chartTopic with userId {}", event.getUserId());

        try {
            Observation.createNotStarted("chart-topic", this.observationRegistry).observe(() -> {
                CompletableFuture<SendResult<String, ChartEvent>> future = kafkaTemplate.send(topic, event);
                return future.handle((result, throwable) -> CompletableFuture.completedFuture(result));
            });
        } catch (Exception exception) {
            log.error("Exception occurred while sending message to kafka, exception message: {}", exception.getMessage());
        }
    }
}
