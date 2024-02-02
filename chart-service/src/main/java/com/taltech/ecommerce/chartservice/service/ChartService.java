package com.taltech.ecommerce.chartservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taltech.ecommerce.chartservice.repository.ChartRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChartService {

    private final ChartRepository chartRepository;

    public boolean deleteById(Long id) {
        try{
            chartRepository.deleteById(id);
            return true;
        }
        catch (Exception ex) {
            log.error("Error deleting chart by id: {}, exception: {}", id, ex.getMessage());
            return false;
        }
    }
}
