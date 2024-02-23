package com.taltech.ecommerce.chartservice.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taltech.ecommerce.chartservice.event.ChartEvent;
import com.taltech.ecommerce.chartservice.exception.ChartDeleteException;
import com.taltech.ecommerce.chartservice.model.Chart;
import com.taltech.ecommerce.chartservice.publisher.ChartEventPublisher;
import com.taltech.ecommerce.chartservice.repository.ChartRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChartService {

    private final ChartRepository repository;
    private final ChartEventPublisher eventPublisher;

    public void commitDelete(ChartEvent chartEvent) {
        try {
            updateChart("Commit", chartEvent.getUserId());
            eventPublisher.publishChartDeleted(chartEvent);

        } catch (Exception exception) {
            log.error("Deleting chart failed with exception message: {}", exception.getMessage());
            eventPublisher.publishChartDeleteFailed(chartEvent);
        }
    }

    public void rollbackDelete(ChartEvent chartEvent) {
        try {
            updateChart("Rollback", chartEvent.getUserId());
            eventPublisher.publishChartRollbacked(chartEvent);

        } catch (Exception exception) {
            log.error("Rollbacking chart failed with exception message: {}", exception.getMessage());
            eventPublisher.publishChartRollbackFailed(chartEvent);
        }
    }

    private void updateChart(String action, Long userId) {
        log.info("{} - Checking chart by userId '{}'", action, userId);
        Chart chart = repository.findByUserId(userId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("%s - Chart with userId '%s' not found",
                action, userId)));

        boolean rollback = action.equals("Rollback");
        if(!rollback && !chart.isActive()) {
            throw new ChartDeleteException(String.format("%s - Chart for userId '%s' is not active",
                action,
                userId));
        }

        log.info("{} - Updating chart by id '{}'", action, chart.getId());
        try {
            setChartActive(chart, rollback);
            setChartUpdateDate(chart);
            repository.save(chart);
        }
        catch (Exception exception) {
            throw new ChartDeleteException(String.format("%s - Exception happened while updating the chart with id '%s' and userId '%s'. Exception message: '%s'",
                action,
                chart.getId(),
                chart.getUserId(),
                exception.getMessage()));
        }
    }

    private static void setChartActive(Chart chart, boolean active) {
        chart.setActive(active);
        chart.getChartItems().forEach(chartItem -> chartItem.setActive(active));
    }

    private static void setChartUpdateDate(Chart chart) {
        chart.setUpdateDate(LocalDateTime.now());
        chart.getChartItems().forEach(chartItem -> chartItem.setUpdateDate(LocalDateTime.now()));
    }
}
