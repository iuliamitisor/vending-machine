package org.vendingmachine.service;

import org.vendingmachine.exception.*;
import org.vendingmachine.model.Product;
import org.springframework.stereotype.Service;
import org.vendingmachine.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product findByColumn(int columnId) {
        return productRepository.findByColumnId(columnId);
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
        // TODO update the row in the database
    }
}
