package org.vendingmachine.service;

import org.vendingmachine.exception.*;
import org.vendingmachine.model.Product;
import org.springframework.stereotype.Service;
import org.vendingmachine.model.Sale;
import org.vendingmachine.repository.ProductRepository;
import org.vendingmachine.repository.SaleRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    public ProductService(ProductRepository productRepository, SaleRepository saleRepository) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
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
            productRepository.save(boughtProduct);
            registerSale(columnId, requiredAmount, "cash", cashAmount - requiredAmount);
        }

        if (cashAmount < requiredAmount) {
            throw new PaymentValidationException("Insufficient cash amount.", columnId);
        }
        if (cashAmount > requiredAmount) {
            throw new ExcessCashException("Excess cash amount.", columnId, (float) (cashAmount - requiredAmount));
        }
    }

    public void validateCardPayment(Product boughtProduct) {
        // Assume card payment always successful
        boughtProduct.decrementQuantity(1);
        productRepository.save(boughtProduct);
        registerSale(boughtProduct.getColumnId(), boughtProduct.getPrice(), "card", 0.0f);
    }

    public void registerSale(int columnId, float price, String method, float change) {
        Sale sale = new Sale(columnId, price, method, change, LocalDateTime.now());
        saleRepository.save(sale);
    }
}


