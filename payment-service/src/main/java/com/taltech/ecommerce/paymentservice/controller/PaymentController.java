package com.taltech.ecommerce.paymentservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.taltech.ecommerce.paymentservice.dto.PaymentDto;
import com.taltech.ecommerce.paymentservice.mapper.PaymentMapper;
import com.taltech.ecommerce.paymentservice.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDto save(@RequestBody PaymentDto paymentDto) {
        log.info("Received payment dto fo saving: {}", paymentDto);
        return paymentMapper.toDto(paymentService.save(paymentMapper.toModel(paymentDto)));
    }
}

