package com.taltech.ecommerce.productservice.util;

import com.taltech.ecommerce.productservice.model.Product;
import com.taltech.ecommerce.productservice.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() < 3) {
            Product product = new Product();
            product.setName("iPhone 13");
            product.setDescription("iPhone 13");
            product.setPrice(BigDecimal.valueOf(1000));

            productRepository.save(product);

            Product product2 = new Product();
            product2.setName("iPhone 15");
            product2.setDescription("iPhone 15");
            product2.setPrice(BigDecimal.valueOf(2000));

            productRepository.save(product2);

            Product product3 = new Product();
            product3.setName("Samsung A50");
            product3.setDescription("Samsung A50");
            product3.setPrice(BigDecimal.valueOf(1500));

            productRepository.save(product3);
        }
    }
}
