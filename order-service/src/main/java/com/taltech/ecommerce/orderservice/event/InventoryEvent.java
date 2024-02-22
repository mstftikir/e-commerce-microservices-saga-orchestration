package com.taltech.ecommerce.orderservice.event;

import java.util.List;

import com.taltech.ecommerce.orderservice.dto.inventory.InventoryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEvent {

    private List<InventoryDto> inventoryList;
}
