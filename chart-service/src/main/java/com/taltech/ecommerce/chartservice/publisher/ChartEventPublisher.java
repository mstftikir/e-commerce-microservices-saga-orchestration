package com.taltech.ecommerce.chartservice.publisher;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.chartservice.event.ChartEvent;

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

    public void publishChartDeleted(Long userId) {
        publishEvent("chartDeletedTopic", "chart-deleted-sent", userId);
    }

    public void publishChartDeleteFailed(Long userId) {
        publishEvent("chartDeleteFailedTopic", "chart-delete-failed-sent", userId);
    }

    public void publishChartRollbacked(Long userId) {
        publishEvent("chartRollbackedTopic", "chart-rollbacked-sent", userId);
    }

    public void publishChartRollbackFailed(Long userId) {
        publishEvent("chartRollbackFailedTopic", "chart-rollback-failed-sent", userId);
    }

    private void publishEvent(String topic, String observationName, Long userId) {
        log.info("Publishing chart event to '{}'", topic);

        try {
            Observation.createNotStarted(observationName, this.observationRegistry).observe(() -> {
                ChartEvent chartEvent = ChartEvent.builder().userId(userId).build();
                CompletableFuture<SendResult<String, ChartEvent>> future = kafkaTemplate.send(topic, chartEvent);
                return future.handle((result, throwable) -> CompletableFuture.completedFuture(result));
            });
        } catch (Exception exception) {
            log.error("Exception occurred while sending message to kafka, exception message: {}", exception.getMessage());
        }
    }
}
