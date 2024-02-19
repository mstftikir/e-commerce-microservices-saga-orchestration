package com.taltech.ecommerce.chartservice.service;

import org.springframework.stereotype.Service;

import com.taltech.ecommerce.chartservice.exception.ChartDeleteException;
import com.taltech.ecommerce.chartservice.model.Chart;
import com.taltech.ecommerce.chartservice.repository.ChartRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChartService {

    private final ChartRepository repository;

    public void deleteByUserId(Long userId) {
        log.info("Checking chart by userId '{}'", userId);
        Chart chart = repository.findByUserId(userId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Chart with userId '%s' not found", userId)));

        log.info("Deleting chart by id '{}'", chart.getId());
        try {
            repository.deleteById(chart.getId());
        }
        catch (Exception exception) {
            throw new ChartDeleteException(String.format("Exception happened while deleting the chart with id '%s' and userId '%s'. Exception message: '%s'",
                chart.getId(),
                chart.getUserId(),
                exception.getMessage()));
        }
    }
}
