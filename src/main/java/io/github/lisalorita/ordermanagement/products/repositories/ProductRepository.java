package io.github.lisalorita.ordermanagement.products.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.lisalorita.ordermanagement.products.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
