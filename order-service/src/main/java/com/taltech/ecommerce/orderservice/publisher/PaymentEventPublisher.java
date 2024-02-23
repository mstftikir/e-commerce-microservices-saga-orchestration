package com.taltech.ecommerce.orderservice.publisher;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.orderservice.event.PaymentEvent;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {


    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private final ObservationRegistry observationRegistry;

    public void publishSavePayment(PaymentEvent event) {
        publishEvent("savePaymentTopic", "save-payment-sent", event);
    }

    public void publishRollbackPayment(PaymentEvent event) {
        publishEvent("rollbackPaymentTopic", "rollback-payment-sent", event);
    }

    private void publishEvent(String topic, String observationName, PaymentEvent event) {
        log.info("Publishing payment event to '{}'", topic);

        try {
            Observation.createNotStarted(observationName, this.observationRegistry).observe(() -> {
                CompletableFuture<SendResult<String, PaymentEvent>> future = kafkaTemplate.send(topic, event);
                return future.handle((result, throwable) -> CompletableFuture.completedFuture(result));
            });
        } catch (Exception exception) {
            log.error("Exception occurred while sending message to kafka, exception message: {}", exception.getMessage());
        }
    }
}
