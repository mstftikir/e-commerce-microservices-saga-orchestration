package com.taltech.ecommerce.paymentservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;

import com.taltech.ecommerce.paymentservice.exception.PaymentSaveException;
import com.taltech.ecommerce.paymentservice.model.Payment;
import com.taltech.ecommerce.paymentservice.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment save(Payment payment) {
        log.info("Saving the payment for userId '{}' and totalPrice '{}'",
            payment.getUserId(),
            payment.getTotalPrice());

        Payment calculatedPayment = calculateTotalPrice(payment);
        addDates(calculatedPayment);

        try {
            return paymentRepository.save(calculatedPayment);
        }
        catch (Exception exception) {
            throw new PaymentSaveException(String.format("Payment save failed for userId '%s' and totalPrice '%s'",
                payment.getUserId(),
                payment.getTotalPrice()));
        }
    }

    private Payment calculateTotalPrice(Payment payment) {
        AtomicReference<BigDecimal> totalPrice = new AtomicReference<>(BigDecimal.ZERO);
        payment.getPaymentItems().forEach(paymentItem -> totalPrice.updateAndGet(t -> t.add(paymentItem.getPrice().multiply(new BigDecimal(paymentItem.getQuantity())))));
        payment.setTotalPrice(totalPrice.get());
        return payment;
    }

    private void addDates(Payment payment) {
        payment.setInsertDate(LocalDateTime.now());
        payment.setUpdateDate(LocalDateTime.now());

        payment.getPaymentItems().forEach(paymentItem -> {
            paymentItem.setInsertDate(LocalDateTime.now());
            paymentItem.setUpdateDate(LocalDateTime.now());
        });
    }
}
