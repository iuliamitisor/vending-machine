package org.vendingmachine.service;

import org.vendingmachine.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    // Used a list to store products for now
    private final List<Product> products = new ArrayList<>();

    public ProductService() {
        products.add(new Product(1, "Eugenia", 3.2f, 10));
        products.add(new Product(2, "Pepsi", 7.5f, 8));
        products.add(new Product(3, "Chio Chips", 12.0f, 5));
    }

    public List<Product> getAllProducts() {
        return products;
    }
}
