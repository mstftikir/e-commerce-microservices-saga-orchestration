package com.taltech.ecommerce.chartservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.taltech.ecommerce.chartservice.service.ChartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/chart")
@RequiredArgsConstructor
@Slf4j
public class ChartController {

    private static final String RECEIVED_INFO_LOG = "{} - Received request by userId: '{}'";

    private final ChartService service;

    @DeleteMapping("/prepare/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void prepareDeleteByUserId(@PathVariable("userId") Long userId) {
        log.info(RECEIVED_INFO_LOG, "Prepare", userId);
        service.prepareDeleteByUserId(userId);
    }

    @DeleteMapping("/commit/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void commitDeleteByUserId(@PathVariable("userId") Long userId) {
        log.info(RECEIVED_INFO_LOG, "Commit", userId);
        service.commitDeleteByUserId(userId);
    }

    @DeleteMapping("/rollback/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void rollbackDeleteByUserId(@PathVariable("userId") Long userId) {
        log.info(RECEIVED_INFO_LOG, "Rollback", userId);
        service.rollbackDeleteByUserId(userId);
    }
}

