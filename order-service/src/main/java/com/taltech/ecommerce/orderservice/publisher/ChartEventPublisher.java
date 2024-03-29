package com.taltech.ecommerce.orderservice.publisher;

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

    public void publishDeleteChart(ChartEvent event) {
        publishEvent("deleteChartTopic", "delete-chart-sent", event);
    }

    public void publishRollbackChart(ChartEvent event) {
        publishEvent("rollbackChartTopic", "rollback-chart-sent", event);
    }

    private void publishEvent(String topic, String observationName, ChartEvent event) {
        log.info("Publishing chart event to '{}'", topic);

        try {
            Observation.createNotStarted(observationName, this.observationRegistry).observe(() -> {
                CompletableFuture<SendResult<String, ChartEvent>> future = kafkaTemplate.send(topic, event);
                return future.handle((result, throwable) -> CompletableFuture.completedFuture(result));
            });
        } catch (Exception exception) {
            log.error("Exception occurred while sending message to kafka, exception message: {}", exception.getMessage());
        }
    }
}
