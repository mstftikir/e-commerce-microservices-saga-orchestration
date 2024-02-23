package com.taltech.ecommerce.paymentservice.publisher;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.paymentservice.dto.PaymentDto;
import com.taltech.ecommerce.paymentservice.event.PaymentEvent;
import com.taltech.ecommerce.paymentservice.mapper.PaymentMapper;
import com.taltech.ecommerce.paymentservice.model.Payment;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private final PaymentMapper mapper;
    private final ObservationRegistry observationRegistry;

    public void publishPaymentSaved(Payment payment) {
        publishEvent("paymentSavedTopic", "payment-saved-sent", payment);
    }

    public void publishPaymentSaveFailed(Payment payment) {
        publishEvent("paymentSaveFailedTopic", "payment-save-failed-sent", payment);
    }

    public void publishPaymentRollbacked(Payment payment) {
        publishEvent("paymentRollbackedTopic", "payment-rollbacked-sent", payment);
    }

    public void publishPaymentRollbackFailed(Payment payment) {
        publishEvent("paymentRollbackFailedTopic", "payment-rollback-failed-sent", payment);
    }

    private void publishEvent(String topic, String observationName, Payment payment) {
        log.info("Publishing payment event to '{}'", topic);

        try {
            Observation.createNotStarted(observationName, this.observationRegistry).observe(() -> {
                PaymentDto paymentDto = mapper.toDto(payment);
                PaymentEvent paymentEvent = PaymentEvent.builder().payment(paymentDto).build();
                CompletableFuture<SendResult<String, PaymentEvent>> future = kafkaTemplate.send(topic, paymentEvent);
                return future.handle((result, throwable) -> CompletableFuture.completedFuture(result));
            });
        } catch (Exception exception) {
            log.error("Exception occurred while sending message to kafka, exception message: {}", exception.getMessage());
        }
    }
}

