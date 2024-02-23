package com.taltech.ecommerce.chartservice.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.chartservice.event.ChartEvent;
import com.taltech.ecommerce.chartservice.service.ChartService;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChartEventListener {

    private final ChartService service;
    private final ObservationRegistry observationRegistry;

    @KafkaListener(topics = "deleteChartTopic")
    public void receiveDeleteChart(ChartEvent chartEvent) {
        Observation.createNotStarted("delete-chart-received", this.observationRegistry)
            .observe(() -> {
                log.info("Delete chart event received");
                service.commitDeleteByUserId(chartEvent.getUserId());
            });
    }

    @KafkaListener(topics = "rollbackChartTopic")
    public void receiveRollbackChart(ChartEvent chartEvent) {
        Observation.createNotStarted("rollback-chart-received", this.observationRegistry)
            .observe(() -> {
                log.info("Rollback chart event received");
                service.rollbackDeleteByUserId(chartEvent.getUserId());
            });
    }
}
