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
public class PaymentListener {

    private final ObservationRegistry observationRegistry;

    @KafkaListener(topics = "paymentTopic")
    public void receiveEvent(PaymentEvent paymentEvent) {
        Observation.createNotStarted("on-chart-message-received", this.observationRegistry)
            .observe(() -> log.info("Got message with payment code '{}'", paymentEvent.getPaymentDto().getCode()));
    }
}
