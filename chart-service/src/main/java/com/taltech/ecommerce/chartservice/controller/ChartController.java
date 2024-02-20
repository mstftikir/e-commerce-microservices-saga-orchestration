package com.taltech.ecommerce.chartservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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

    private static final String RECEIVED_INFO_LOG = "{} - Received request by userId: {}";

    private final ChartService service;

    @GetMapping("/{userId}/prepare")
    @ResponseStatus(HttpStatus.OK)
    public void prepareDeleteByUserId(@PathVariable("userId") Long userId) {
        log.info(RECEIVED_INFO_LOG, "Prepare", userId);
        service.prepareDeleteByUserId(userId);
    }

    @DeleteMapping("/{userId}/commit")
    @ResponseStatus(HttpStatus.OK)
    public void commitDeleteByUserId(@PathVariable("userId") Long userId) {
        log.info(RECEIVED_INFO_LOG, "Commit", userId);
        service.commitDeleteByUserId(userId);
    }

    @PutMapping("/{userId}/rollback")
    @ResponseStatus(HttpStatus.OK)
    public void rollbackDeleteByUserId(@PathVariable("userId") Long userId) {
        log.info(RECEIVED_INFO_LOG, "Rollback", userId);
        service.rollbackDeleteByUserId(userId);
    }
}

