package org.vendingmachine.service;

import org.vendingmachine.exception.*;
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

    public Product findByColumn(int columnId) {
        for (Product p : products) {
            if (p.getColumnId() == columnId) {
                return p;
            }
        }
        return null;
    }

    public void buyProduct(int columnId) throws RuntimeException {
        Product requestedProduct = findByColumn(columnId);
        if (requestedProduct == null) {
            throw new ColumnValidationException("Invalid column number.");
        }
        if (requestedProduct.getQuantity() <= 0) {
            throw new InsufficientStockException("Insufficient stock.");
        }
    }

    public void validateCashPayment(Product boughtProduct, float cashAmount, int columnId) {
        if (cashAmount < 0) {
            throw new InvalidCashAmountException("Invalid cash amount.", columnId);
        }
        float requiredAmount = boughtProduct.getPrice();
        if (cashAmount >= requiredAmount) {
            boughtProduct.decrementQuantity(1);
        }

        if (cashAmount < requiredAmount) {
            throw new PaymentValidationException("Insufficient cash amount.", columnId);
        }
        if (cashAmount > requiredAmount) {
            throw new ExcessCashException("Excess cash amount.", columnId, (float)(cashAmount - requiredAmount));
        }
    }

    public void validateCardPayment(Product boughtProduct) {
        // Assume card payment always successful
        boughtProduct.decrementQuantity(1);
    }
}
