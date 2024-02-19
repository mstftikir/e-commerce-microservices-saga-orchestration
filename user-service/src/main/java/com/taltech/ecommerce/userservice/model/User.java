package com.taltech.ecommerce.userservice.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private Long id;
    private String name;
    private String surname;
    private String emailAddress;
    @OneToMany(cascade = CascadeType.ALL)
    private List<UserAddress> userAddresses;
    private LocalDateTime insertDate;
    private LocalDateTime updateDate;
}

