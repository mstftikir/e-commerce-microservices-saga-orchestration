package com.taltech.ecommerce.orderservice.listener;

import java.util.concurrent.CompletableFuture;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.orderservice.event.TestEvent;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestListener {


    private final KafkaTemplate<String, TestEvent> kafkaTemplate;
    private final ObservationRegistry observationRegistry;

    @EventListener
    public void publishEvent(TestEvent event) {
        log.info("Test Event Received, Sending TestEvent to testTopic: {}", event.getData());

        try {
            Observation.createNotStarted("test-topic", this.observationRegistry).observe(() -> {
                CompletableFuture<SendResult<String, TestEvent>> future = kafkaTemplate.send("testTopic",
                    new TestEvent(event.getData()));
                return future.handle((result, throwable) -> CompletableFuture.completedFuture(result));
            });
        } catch (Exception exception) {
            log.error("Exception occurred while sending message to kafka, exception message: {}", exception.getMessage());
        }
    }
}
