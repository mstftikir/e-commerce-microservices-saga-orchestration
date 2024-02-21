package com.taltech.ecommerce.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.taltech.ecommerce.userservice.dto.UserDto;
import com.taltech.ecommerce.userservice.mapper.UserMapper;
import com.taltech.ecommerce.userservice.model.User;
import com.taltech.ecommerce.userservice.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findById(@PathVariable("id") Long id) {
        log.info("Received user find by id '{}' request", id);

        User foundUser = service.findById(id);
        return mapper.toDto(foundUser);
    }
}
