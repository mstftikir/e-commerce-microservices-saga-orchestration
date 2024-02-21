package com.taltech.ecommerce.orderservice.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestEvent extends ApplicationEvent {

    private String data;

    public TestEvent(String data) {
        super(data);
        this.data = data;
    }
}
