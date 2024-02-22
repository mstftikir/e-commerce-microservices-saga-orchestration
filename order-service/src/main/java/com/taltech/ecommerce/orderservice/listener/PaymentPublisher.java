package com.taltech.ecommerce.orderservice.listener;

import java.util.concurrent.CompletableFuture;

import org.springframework.context.event.EventListener;
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
public class PaymentPublisher {


    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private final ObservationRegistry observationRegistry;

    @EventListener
    public void publishEvent(PaymentEvent event) {
        log.info("Publishing ChartEvent to chartTopic with code {}", event.getPaymentDto().getCode());

        try {
            Observation.createNotStarted("payment-topic", this.observationRegistry).observe(() -> {
                CompletableFuture<SendResult<String, PaymentEvent>> future = kafkaTemplate.send("paymentTopic", event);
                return future.handle((result, throwable) -> CompletableFuture.completedFuture(result));
            });
        } catch (Exception exception) {
            log.error("Exception occurred while sending message to kafka, exception message: {}", exception.getMessage());
        }
    }
}
