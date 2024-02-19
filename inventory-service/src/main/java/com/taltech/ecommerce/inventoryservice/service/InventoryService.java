package com.taltech.ecommerce.inventoryservice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taltech.ecommerce.inventoryservice.exception.InventoryLimitException;
import com.taltech.ecommerce.inventoryservice.model.Inventory;
import com.taltech.ecommerce.inventoryservice.repository.InventoryRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository repository;

    @Transactional
    public List<Inventory> update(List<Inventory> inventoryList) {
        List<Inventory> foundInventoryList = findByCode(inventoryList);

        isInStock(foundInventoryList);

        foundInventoryList.forEach(foundInventory -> {
            Integer receivedQuantity = inventoryList.stream().filter(inventory -> inventory.getCode().equals(foundInventory.getCode()))
                .map(Inventory::getQuantity)
                .findAny()
                .orElseThrow(EntityNotFoundException::new);
            Integer foundQuantity = foundInventory.getQuantity();
            int calculatedQuantity = foundQuantity - receivedQuantity;

            if (calculatedQuantity < 0) {
                throw new InventoryLimitException(String.format("Received inventory quantity '%d' is more than found inventory quantity '%d'", receivedQuantity, foundQuantity));
            }

            foundInventory.setQuantity(calculatedQuantity);
        });

        return repository.saveAll(foundInventoryList);
    }

    private List<Inventory> findByCode(List<Inventory> inventoryList) {
        log.info("Checking inventory for {}", inventoryList);
        List<String> codes = inventoryList.stream().map(Inventory::getCode).toList();
        List<Inventory> foundInventoryList = repository.findByCodeIn(codes);
        if(foundInventoryList.size() != inventoryList.size()) {
            throw new EntityNotFoundException(String.format("Received inventory size '%s' and found inventory size '%s' is not equal",
                foundInventoryList.size(),
                inventoryList.size()));
        }
        return foundInventoryList;
    }

    private void isInStock(List<Inventory> inventoryList) {
        inventoryList.forEach(inventory -> {
            if(inventory.getQuantity() != null && inventory.getQuantity() <= 0) {
                throw new EntityNotFoundException(String.format("Not enough quantity '%s' for inventory item code '%s'",
                    inventory.getQuantity(),
                    inventory.getCode()));
            }
        });
    }
}
