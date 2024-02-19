package com.taltech.ecommerce.inventoryservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.taltech.ecommerce.inventoryservice.dto.InventoryDto;
import com.taltech.ecommerce.inventoryservice.model.Inventory;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    Inventory toModel(InventoryDto dto);
    InventoryDto toDto(Inventory model);
    List<Inventory> toModelList(List<InventoryDto> dtoList);
    List<InventoryDto> toDtoList(List<Inventory> modelList);
}
