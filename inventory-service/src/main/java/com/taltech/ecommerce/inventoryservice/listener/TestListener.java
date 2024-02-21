package com.taltech.ecommerce.inventoryservice.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.inventoryservice.event.TestEvent;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class TestListener {

    private final ObservationRegistry observationRegistry;

    @KafkaListener(topics = "testTopic")
    public void receiveEvent(TestEvent testEvent) {
        Observation.createNotStarted("on-test-message-received", this.observationRegistry)
            .observe(() -> log.info("Got message '{}'", testEvent));
    }
}
