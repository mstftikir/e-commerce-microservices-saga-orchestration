package com.taltech.ecommerce.chartservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taltech.ecommerce.chartservice.model.Chart;

public interface ChartRepository extends JpaRepository<Chart, Long> {
}
