package com.taltech.ecommerce.inventoryservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taltech.ecommerce.inventoryservice.dto.InventoryDto;
import com.taltech.ecommerce.inventoryservice.event.InventoryEvent;
import com.taltech.ecommerce.inventoryservice.exception.InventoryLimitException;
import com.taltech.ecommerce.inventoryservice.mapper.InventoryMapper;
import com.taltech.ecommerce.inventoryservice.model.Inventory;
import com.taltech.ecommerce.inventoryservice.publisher.InventoryEventPublisher;
import com.taltech.ecommerce.inventoryservice.repository.InventoryRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InventoryService {

    private final InventoryRepository repository;
    private final InventoryMapper mapper;
    private final InventoryEventPublisher eventPublisher;

    public void commitUpdate(InventoryEvent inventoryEvent) {
        List<Inventory> inventoryList = mapper.toModelList(inventoryEvent.getInventoryList());
        try {
            List<Inventory> updatedInventoryList = updateInventories("Commit", inventoryList);
            List<InventoryDto> updatedInventoryDtoList = mapper.toDtoList(updatedInventoryList);
            inventoryEvent.setInventoryList(updatedInventoryDtoList);
            eventPublisher.publishInventoryUpdated(inventoryEvent);
        }
        catch (Exception exception) {
            log.error("Updating inventory failed with exception message: {}", exception.getMessage());
            eventPublisher.publishInventoryUpdateFailed(inventoryEvent);
        }
    }

    public void rollbackUpdate(InventoryEvent inventoryEvent) {
        List<Inventory> inventoryList = mapper.toModelList(inventoryEvent.getInventoryList());
        try {
            List<Inventory> updatedInventoryList = updateInventories("Rollback", inventoryList);
            List<InventoryDto> updatedInventoryDtoList = mapper.toDtoList(updatedInventoryList);
            inventoryEvent.setInventoryList(updatedInventoryDtoList);
            eventPublisher.publishInventoryRollbacked(inventoryEvent);
        }
        catch (Exception exception) {
            log.error("Rollbacking inventory failed with exception message: {}", exception.getMessage());
            eventPublisher.publishInventoryRollbackFailed(inventoryEvent);
        }
    }

    private List<Inventory> updateInventories(String action, List<Inventory> inventoryList) {
        List<Inventory> foundInventoryList = findByCode(action, inventoryList);

        isInStock(action, foundInventoryList);

        foundInventoryList.forEach(foundInventory -> {
            BigDecimal receivedQuantity = inventoryList.stream().filter(inventory -> inventory.getCode().equals(foundInventory.getCode()))
                .map(Inventory::getQuantity)
                .findAny()
                .orElseThrow(EntityNotFoundException::new);
            BigDecimal foundQuantity = foundInventory.getQuantity();
            BigDecimal calculatedQuantity;

            if(action.equals("Rollback")){
                calculatedQuantity = foundQuantity.add(receivedQuantity);
            }
            else {
                calculatedQuantity = foundQuantity.subtract(receivedQuantity);
                if (calculatedQuantity.compareTo(BigDecimal.ZERO) < 0) {
                    throw new InventoryLimitException(String.format("%s - Received inventory quantity '%s' is more than found inventory quantity '%s'",
                        action,
                        receivedQuantity,
                        foundQuantity));
                }
            }

            foundInventory.setQuantity(calculatedQuantity);
            foundInventory.setUpdateDate(LocalDateTime.now());
        });

        return repository.saveAllAndFlush(foundInventoryList);
    }

    private List<Inventory> findByCode(String action, List<Inventory> receivedInventoryList) {
        log.info("{} - Checking inventory for '{}' inventory items", action, receivedInventoryList.size());
        List<String> codes = receivedInventoryList.stream().map(Inventory::getCode).toList();
        List<Inventory> foundInventoryList = repository.findByCodeIn(codes);
        if(foundInventoryList.size() != receivedInventoryList.size()) {
            throw new EntityNotFoundException(String.format("%s - Received inventory size '%s' and found inventory size '%s' is not equal",
                action,
                receivedInventoryList.size(),
                foundInventoryList.size()));
        }
        return foundInventoryList;
    }

    private void isInStock(String action, List<Inventory> inventoryList) {
        inventoryList.forEach(inventory -> {
            if(inventory.getQuantity() != null && inventory.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new EntityNotFoundException(String.format("%s - Not enough quantity '%s' for inventory item code '%s'",
                    action,
                    inventory.getQuantity(),
                    inventory.getCode()));
            }
        });
    }
}
