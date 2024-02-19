package com.taltech.ecommerce.inventoryservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.taltech.ecommerce.inventoryservice.dto.InventoryDto;
import com.taltech.ecommerce.inventoryservice.mapper.InventoryMapper;
import com.taltech.ecommerce.inventoryservice.model.Inventory;
import com.taltech.ecommerce.inventoryservice.service.InventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryMapper inventoryMapper;

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryDto> update(@RequestBody List<InventoryDto> inventoryDtos) {
        log.info("Received inventory update for: {}", inventoryDtos);

        List<Inventory> modelList = inventoryMapper.toModelList(inventoryDtos);
        List<Inventory> updatedList = inventoryService.update(modelList);
        return inventoryMapper.toDtoList(updatedList);
    }
}

