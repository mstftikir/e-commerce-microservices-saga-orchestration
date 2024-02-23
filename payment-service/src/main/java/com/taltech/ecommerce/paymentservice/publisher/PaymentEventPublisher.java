package com.taltech.ecommerce.paymentservice.publisher;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.paymentservice.event.PaymentEvent;

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

    public void publishPaymentSaved(PaymentEvent paymentEvent) {
        publishEvent("paymentSavedTopic", "payment-saved-sent", paymentEvent);
    }

    public void publishPaymentSaveFailed(PaymentEvent paymentEvent) {
        publishEvent("paymentSaveFailedTopic", "payment-save-failed-sent", paymentEvent);
    }

    public void publishPaymentRollbacked(PaymentEvent paymentEvent) {
        publishEvent("paymentRollbackedTopic", "payment-rollbacked-sent", paymentEvent);
    }

    public void publishPaymentRollbackFailed(PaymentEvent paymentEvent) {
        publishEvent("paymentRollbackFailedTopic", "payment-rollback-failed-sent", paymentEvent);
    }

    private void publishEvent(String topic, String observationName, PaymentEvent paymentEvent) {
        log.info("Publishing payment event to '{}'", topic);

        try {
            Observation.createNotStarted(observationName, this.observationRegistry).observe(() -> {
                CompletableFuture<SendResult<String, PaymentEvent>> future = kafkaTemplate.send(topic, paymentEvent);
                return future.handle((result, throwable) -> CompletableFuture.completedFuture(result));
            });
        } catch (Exception exception) {
            log.error("Exception occurred while sending message to kafka, exception message: {}", exception.getMessage());
        }
    }
}

