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

    private final ChartService chartService;

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public boolean deleteById(@PathVariable("id") Long id) {
        log.info("Received chart deletion request by id: {}", id);
        return chartService.deleteById(id);
    }
}

