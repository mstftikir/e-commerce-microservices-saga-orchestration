package com.taltech.ecommerce.chartservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String inventoryCode;
    private Integer quantity;
    private BigDecimal price;
    private boolean active;
    private LocalDateTime insertDate;
    private LocalDateTime updateDate;
}
