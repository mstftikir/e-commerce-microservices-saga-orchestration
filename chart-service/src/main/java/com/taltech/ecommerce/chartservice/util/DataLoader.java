package com.taltech.ecommerce.chartservice.util;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.taltech.ecommerce.chartservice.model.Chart;
import com.taltech.ecommerce.chartservice.model.ChartLineItem;
import com.taltech.ecommerce.chartservice.repository.ChartRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final ChartRepository chartRepository;
    @Override
    public void run(String... args) {
        if (chartRepository.count() < 2) {
            Chart chart = new Chart();
            ChartLineItem chartLineItem = new ChartLineItem();
            chartLineItem.setPrice(BigDecimal.ONE);
            chartLineItem.setQuantity(1);
            chartLineItem.setSkuCode("sku-code-1");

            ChartLineItem chartLineItem2 = new ChartLineItem();
            chartLineItem2.setPrice(BigDecimal.TEN);
            chartLineItem2.setQuantity(2);
            chartLineItem2.setSkuCode("sku-code-2");
            chart.setChartLineItems(List.of(chartLineItem, chartLineItem2));
            chartRepository.save(chart);

            Chart chart2 = new Chart();
            ChartLineItem chartLineItem3 = new ChartLineItem();
            chartLineItem3.setPrice(BigDecimal.ONE);
            chartLineItem3.setQuantity(3);
            chartLineItem3.setSkuCode("sku-code-3");

            ChartLineItem chartLineItem4 = new ChartLineItem();
            chartLineItem4.setPrice(BigDecimal.TEN);
            chartLineItem4.setQuantity(4);
            chartLineItem4.setSkuCode("sku-code-4");
            chart2.setChartLineItems(List.of(chartLineItem3, chartLineItem4));
            chartRepository.save(chart2);
        }
    }
}
