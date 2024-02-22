package com.taltech.ecommerce.paymentservice.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.paymentservice.event.PaymentEvent;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final ObservationRegistry observationRegistry;

    @KafkaListener(topics = "savePaymentTopic")
    public void receiveSavePaymentEvent(PaymentEvent paymentEvent) {
        Observation.createNotStarted("on-save-payment-event-received", this.observationRegistry)
            .observe(() -> log.info("Save payment with payment code '{}'", paymentEvent.getPaymentDto().getCode()));
    }

    @KafkaListener(topics = "rollbackPaymentTopic")
    public void receiveRollbackPaymentEvent(PaymentEvent paymentEvent) {
        Observation.createNotStarted("on-rollback-payment-event-received", this.observationRegistry)
            .observe(() -> log.info("Rollback payment with payment code '{}'", paymentEvent.getPaymentDto().getCode()));
    }
}
