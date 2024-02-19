package com.taltech.ecommerce.inventoryservice.exception;

public class InventoryLimitException extends RuntimeException {
    public InventoryLimitException(String message) {
        super(message);
    }
}
