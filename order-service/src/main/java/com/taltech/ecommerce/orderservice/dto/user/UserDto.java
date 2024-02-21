package com.taltech.ecommerce.orderservice.dto.user;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class UserDto {

    private String name;
    private String surname;
    private String emailAddress;
    private List<UserAddressDto> userAddresses;
    private LocalDateTime insertDate;
    private LocalDateTime updateDate;
}
