package com.taltech.ecommerce.paymentservice.util;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.paymentservice.model.Payment;
import com.taltech.ecommerce.paymentservice.model.PaymentLineItem;
import com.taltech.ecommerce.paymentservice.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final PaymentRepository paymentRepository;
    @Override
    public void run(String... args) {
        if (paymentRepository.count() < 2) {
            Payment payment = new Payment();
            PaymentLineItem paymentLineItem = new PaymentLineItem();
            paymentLineItem.setPrice(BigDecimal.ONE);
            paymentLineItem.setQuantity(1);
            paymentLineItem.setSkuCode("sku-code-1");

            PaymentLineItem paymentLineItem2 = new PaymentLineItem();
            paymentLineItem2.setPrice(BigDecimal.TEN);
            paymentLineItem2.setQuantity(2);
            paymentLineItem2.setSkuCode("sku-code-2");
            payment.setPaymentLineItems(List.of(paymentLineItem, paymentLineItem2));
            paymentRepository.save(payment);

            Payment payment2 = new Payment();
            PaymentLineItem paymentLineItem3 = new PaymentLineItem();
            paymentLineItem3.setPrice(BigDecimal.ONE);
            paymentLineItem3.setQuantity(3);
            paymentLineItem3.setSkuCode("sku-code-3");

            PaymentLineItem paymentLineItem4 = new PaymentLineItem();
            paymentLineItem4.setPrice(BigDecimal.TEN);
            paymentLineItem4.setQuantity(4);
            paymentLineItem4.setSkuCode("sku-code-4");
            payment2.setPaymentLineItems(List.of(paymentLineItem3, paymentLineItem4));
            paymentRepository.save(payment2);
        }
    }
}
