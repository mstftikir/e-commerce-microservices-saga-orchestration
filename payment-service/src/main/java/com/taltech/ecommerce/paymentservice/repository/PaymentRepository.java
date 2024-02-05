package com.taltech.ecommerce.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taltech.ecommerce.paymentservice.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
