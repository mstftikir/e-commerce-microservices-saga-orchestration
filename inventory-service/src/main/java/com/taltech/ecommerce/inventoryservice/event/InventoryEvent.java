package com.taltech.ecommerce.inventoryservice.event;

import com.taltech.ecommerce.inventoryservice.dto.InventoryDto;

import lombok.Data;

@Data
public class InventoryEvent {

    private InventoryDto inventoryDto;
}
