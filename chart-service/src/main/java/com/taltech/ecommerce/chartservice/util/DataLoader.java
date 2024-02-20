package com.taltech.ecommerce.chartservice.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.chartservice.model.Chart;
import com.taltech.ecommerce.chartservice.model.ChartItem;
import com.taltech.ecommerce.chartservice.repository.ChartRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final ChartRepository chartRepository;
    @Override
    public void run(String... args) {
        if (chartRepository.count() < 1) {
            ChartItem chartItem = new ChartItem();
            chartItem.setInventoryCode("iphone_13");
            chartItem.setPrice(new BigDecimal(1000));
            chartItem.setQuantity(1);
            chartItem.setActive(true);
            chartItem.setInsertDate(LocalDateTime.now());
            chartItem.setUpdateDate(LocalDateTime.now());

            ChartItem chartItem2 = new ChartItem();
            chartItem2.setInventoryCode("samsung_a12");
            chartItem2.setPrice(new BigDecimal(800));
            chartItem2.setQuantity(1);
            chartItem2.setActive(true);
            chartItem2.setInsertDate(LocalDateTime.now());
            chartItem2.setUpdateDate(LocalDateTime.now());

            Chart chart = new Chart();
            chart.setUserId(12345L);
            chart.setChartItems(List.of(chartItem, chartItem2));
            chart.setTotalPrice(new BigDecimal(1800));
            chart.setActive(true);
            chart.setInsertDate(LocalDateTime.now());
            chart.setUpdateDate(LocalDateTime.now());
            chartRepository.save(chart);
        }
    }
}
