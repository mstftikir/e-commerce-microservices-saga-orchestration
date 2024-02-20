package com.taltech.ecommerce.paymentservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.taltech.ecommerce.paymentservice.dto.PaymentDto;
import com.taltech.ecommerce.paymentservice.mapper.PaymentMapper;
import com.taltech.ecommerce.paymentservice.model.Payment;
import com.taltech.ecommerce.paymentservice.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private static final String PAYMENT_RECEIVED_MESSAGE = "{} - Received payment with userId '{}' and code '{}'";
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @GetMapping("/prepare")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto prepareSave(@RequestBody PaymentDto paymentDto) {
        log.info(PAYMENT_RECEIVED_MESSAGE, "Prepare", paymentDto.getUserId(), paymentDto.getCode());
        Payment paymentModel = paymentMapper.toModel(paymentDto);
        Payment savedPayment = new Payment();
        try {
            savedPayment = paymentService.prepareSave(paymentModel);
        }
        catch (UnexpectedRollbackException rollbackException) {
            log.info("Prepare rollback for payment code '{}'", paymentDto.getCode());
        }
        return paymentMapper.toDto(savedPayment);
    }

    @PostMapping("/commit")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDto commitSave(@RequestBody PaymentDto paymentDto) {
        log.info(PAYMENT_RECEIVED_MESSAGE, "Commit", paymentDto.getUserId(), paymentDto.getCode());

        Payment paymentModel = paymentMapper.toModel(paymentDto);
        Payment savedPayment = paymentService.commitSave(paymentModel);
        return paymentMapper.toDto(savedPayment);
    }

    @PutMapping("/rollback")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto rollbackSave(@RequestBody PaymentDto paymentDto) {
        log.info(PAYMENT_RECEIVED_MESSAGE, "Rollback", paymentDto.getUserId(), paymentDto.getCode());

        Payment paymentModel = paymentMapper.toModel(paymentDto);
        Payment savedPayment = paymentService.rollbackSave(paymentModel);
        return paymentMapper.toDto(savedPayment);
    }
}

