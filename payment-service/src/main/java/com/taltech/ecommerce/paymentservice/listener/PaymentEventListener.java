package com.taltech.ecommerce.paymentservice.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.paymentservice.event.PaymentEvent;
import com.taltech.ecommerce.paymentservice.mapper.PaymentMapper;
import com.taltech.ecommerce.paymentservice.model.Payment;
import com.taltech.ecommerce.paymentservice.service.PaymentService;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final PaymentService service;
    private final PaymentMapper mapper;
    private final ObservationRegistry observationRegistry;

    @KafkaListener(topics = "savePaymentTopic")
    public void receiveSavePayment(PaymentEvent paymentEvent) {
        Observation.createNotStarted("save-payment-received", this.observationRegistry)
            .observe(() -> {
                log.info("Save payment event received with payment code '{}'", paymentEvent.getPayment().getCode());
                Payment payment = mapper.toModel(paymentEvent.getPayment());
                service.commitSave(payment);
            });
    }

    @KafkaListener(topics = "rollbackPaymentTopic")
    public void receiveRollbackPayment(PaymentEvent paymentEvent) {
        Observation.createNotStarted("rollback-payment-received", this.observationRegistry)
            .observe(() -> {
                log.info("Rollback payment event received with payment code '{}'", paymentEvent.getPayment().getCode());
                Payment payment = mapper.toModel(paymentEvent.getPayment());
                service.rollbackSave(payment);
            });
    }
}
