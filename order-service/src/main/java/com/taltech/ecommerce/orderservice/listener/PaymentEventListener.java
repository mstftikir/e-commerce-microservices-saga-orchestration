package com.taltech.ecommerce.orderservice.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.orderservice.event.PaymentEvent;
import com.taltech.ecommerce.orderservice.service.OrderService;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final OrderService service;
    private final ObservationRegistry observationRegistry;

    @KafkaListener(topics = "paymentSavedTopic")
    public void receivePaymentSaved(PaymentEvent paymentEvent) {
        Observation.createNotStarted("payment-saved-received", this.observationRegistry)
            .observe(() -> {
                log.info("Payment saved event received");
                service.paymentSaved(paymentEvent);
            });
    }

    @KafkaListener(topics = "paymentSaveFailedTopic")
    public void receivePaymentSaveFailed(PaymentEvent paymentEvent) {
        Observation.createNotStarted("payment-save-failed-received", this.observationRegistry)
            .observe(() -> {
                log.info("Payment save failed event received");
                service.paymentSaveFailed(paymentEvent);
            });
    }

    @KafkaListener(topics = "paymentRollbackedTopic")
    public void receivePaymentRollbacked(PaymentEvent paymentEvent) {
        Observation.createNotStarted("payment-rollbacked-received", this.observationRegistry)
            .observe(() -> {
                log.info("Payment rollbacked event received");
                service.paymentRollbacked(paymentEvent);
            });
    }

    @KafkaListener(topics = "paymentRollbackFailedTopic")
    public void receivePaymentRollbackFailed(PaymentEvent paymentEvent) {
        Observation.createNotStarted("payment-rollback-failed-received", this.observationRegistry)
            .observe(() -> {
                log.info("Payment rollback failed event received");
                service.paymentRollbackFailed(paymentEvent);
            });
    }
}
