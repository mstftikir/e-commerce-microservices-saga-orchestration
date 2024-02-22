package com.taltech.ecommerce.orderservice.event;

import com.taltech.ecommerce.orderservice.dto.inventory.InventoryDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventoryEvent {

    private InventoryDto inventoryDto;
}
