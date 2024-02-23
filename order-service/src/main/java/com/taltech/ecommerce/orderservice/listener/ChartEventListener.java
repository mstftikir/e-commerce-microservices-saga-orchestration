package com.taltech.ecommerce.orderservice.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.orderservice.event.ChartEvent;
import com.taltech.ecommerce.orderservice.service.OrderEventService;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChartEventListener {

    private final OrderEventService service;
    private final ObservationRegistry observationRegistry;

    @KafkaListener(topics = "chartDeletedTopic")
    public void receiveChartDeleted(ChartEvent chartEvent) {
        Observation.createNotStarted("chart-deleted-received", this.observationRegistry)
            .observe(() -> {
                log.info("Chart updated event received");
                service.chartDeleted(chartEvent);
            });
    }

    @KafkaListener(topics = "chartDeleteFailedTopic")
    public void receiveChartDeleteFailed(ChartEvent chartEvent) {
        Observation.createNotStarted("chart-delete-failed-received", this.observationRegistry)
            .observe(() -> {
                log.info("Chart delete failed event received");
                service.chartDeleteFailed(chartEvent);
            });
    }

    @KafkaListener(topics = "chartRollbackedTopic")
    public void receiveChartRollbacked(ChartEvent chartEvent) {
        Observation.createNotStarted("chart-rollbacked-received", this.observationRegistry)
            .observe(() -> {
                log.info("Chart rollbacked event received");
                service.chartRollbacked(chartEvent);
            });
    }

    @KafkaListener(topics = "chartRollbackFailedTopic")
    public void chartRollbackFailedTopic(ChartEvent chartEvent) {
        Observation.createNotStarted("chart-rollback-failed-received", this.observationRegistry)
            .observe(() -> {
                log.info("Chart rollback failed event received");
                service.chartRollbackFailed(chartEvent);
            });
    }
}
