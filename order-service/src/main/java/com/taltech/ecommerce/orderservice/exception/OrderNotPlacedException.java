package com.taltech.ecommerce.orderservice.exception;

public class OrderNotPlacedException extends RuntimeException {

    public OrderNotPlacedException(String message) {
        super(message);
    }
}
