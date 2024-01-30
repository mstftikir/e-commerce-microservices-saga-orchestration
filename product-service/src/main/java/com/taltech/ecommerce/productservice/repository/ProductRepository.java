package com.taltech.ecommerce.productservice.repository;

import com.taltech.ecommerce.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
