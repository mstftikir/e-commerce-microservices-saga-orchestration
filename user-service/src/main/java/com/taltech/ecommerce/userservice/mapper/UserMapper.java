package com.taltech.ecommerce.userservice.mapper;

import org.mapstruct.Mapper;

import com.taltech.ecommerce.userservice.dto.UserDto;
import com.taltech.ecommerce.userservice.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User model);
}
