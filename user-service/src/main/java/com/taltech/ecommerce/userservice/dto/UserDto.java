package com.taltech.ecommerce.userservice.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private String name;
    private String surname;
    private String emailAddress;
    private List<UserAddressDto> userAddresses;
    private LocalDateTime insertDate;
    private LocalDateTime updateDate;
}
