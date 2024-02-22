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

    @KafkaListener(topics = "deleteChartTopic")
    public void receiveDeleteChartEvent(ChartEvent chartEvent) {
        Observation.createNotStarted("on-delete-chart-message-received", this.observationRegistry)
            .observe(() -> log.info("Delete chart with userId '{}'", chartEvent.getUserId()));
    }

    @KafkaListener(topics = "rollbackChartTopic")
    public void receiveRollbackChartEvent(ChartEvent chartEvent) {
        Observation.createNotStarted("on-rollback-chart-message-received", this.observationRegistry)
            .observe(() -> log.info("Rollback chart with userId '{}'", chartEvent.getUserId()));
    }
}
