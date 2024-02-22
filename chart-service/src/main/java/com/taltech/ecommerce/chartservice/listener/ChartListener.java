package com.taltech.ecommerce.chartservice.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.chartservice.event.ChartEvent;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChartListener {

    private final ObservationRegistry observationRegistry;

    @KafkaListener(topics = "chartTopic")
    public void receiveEvent(ChartEvent chartEvent) {
        Observation.createNotStarted("on-chart-message-received", this.observationRegistry)
            .observe(() -> log.info("Got message with userId '{}'", chartEvent.getUserId()));
    }
}
