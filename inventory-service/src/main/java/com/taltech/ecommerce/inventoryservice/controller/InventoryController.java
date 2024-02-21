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

    private static final String RECEIVED_INVENTORY_UPDATE = "{} - Received inventory update for '{}' inventory items";

    private final InventoryService service;
    private final InventoryMapper mapper;

    @PutMapping("/prepare")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryDto> prepareUpdate(@RequestBody List<InventoryDto> inventoryDtos) {
        log.info(RECEIVED_INVENTORY_UPDATE, "Prepare", inventoryDtos.size());

        List<Inventory> modelList = mapper.toModelList(inventoryDtos);
        List<Inventory> updatedList = service.prepareUpdate(modelList);
        return mapper.toDtoList(updatedList);
    }

    @PutMapping("/commit")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryDto> commitUpdate(@RequestBody List<InventoryDto> inventoryDtos) {
        log.info(RECEIVED_INVENTORY_UPDATE, "Commit", inventoryDtos.size());

        List<Inventory> modelList = mapper.toModelList(inventoryDtos);
        List<Inventory> updatedList = service.commitUpdate(modelList);
        return mapper.toDtoList(updatedList);
    }

    @PutMapping("/rollback")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryDto> rollbackUpdate(@RequestBody List<InventoryDto> inventoryDtos) {
        log.info(RECEIVED_INVENTORY_UPDATE, "Rollback", inventoryDtos.size());

        List<Inventory> modelList = mapper.toModelList(inventoryDtos);
        List<Inventory> updatedList = service.rollbackUpdate(modelList);
        return mapper.toDtoList(updatedList);
    }
}

