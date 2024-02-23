package com.taltech.ecommerce.inventoryservice.event;

import java.util.List;

import com.taltech.ecommerce.inventoryservice.dto.InventoryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEvent {

    private String eventId;
    private List<InventoryDto> inventoryList;
}
