package com.taltech.ecommerce.userservice.service;

import org.springframework.stereotype.Service;

import com.taltech.ecommerce.userservice.model.User;
import com.taltech.ecommerce.userservice.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository repository;

    public User findById(Long id) {
        log.info("Finding user by id '{}'", id);
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(String.format("User with id '%s' not found", id)));
    }
}
