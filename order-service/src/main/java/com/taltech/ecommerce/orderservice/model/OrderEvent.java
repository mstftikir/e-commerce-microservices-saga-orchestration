package com.taltech.ecommerce.orderservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    @Id
    private String id;
    private Long inventoryStatus;
    private Long chartStatus;
    private Long paymentStatus;
}
