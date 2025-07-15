package org.vendingmachine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vendingmachine.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByColumnId(int columnId);
}

