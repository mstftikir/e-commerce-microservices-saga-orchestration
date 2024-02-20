package com.taltech.ecommerce.chartservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @OneToMany(cascade = CascadeType.ALL)
    private List<ChartItem> chartItems;
    private BigDecimal totalPrice;
    private boolean active;
    private LocalDateTime insertDate;
    private LocalDateTime updateDate;
}
