package org.vendingmachine.service;

import org.vendingmachine.exception.ColumnValidationException;
import org.vendingmachine.exception.NoDataFoundException;
import org.vendingmachine.model.Product;
import org.springframework.stereotype.Service;
import org.vendingmachine.model.Sale;
import org.vendingmachine.repository.ProductRepository;
import org.vendingmachine.repository.SaleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SaleReportsService {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    public SaleReportsService(ProductRepository productRepository, SaleRepository saleRepository) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
    }

    // Stock Report (product, current quantity)
    public Map<String, Integer> createStockReport() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new NoDataFoundException("No products found for stock report.");
        }
        return products.stream()
                .collect(Collectors.toMap(Product::getName, Product::getQuantity));
    }

    // Sales Report (date, product, price, payment-method)
    public List<Sale> createSalesReport(LocalDateTime start, LocalDateTime end) {
        List<Sale> sales = saleRepository.findByTimestampBetween(start, end);
        if (sales.isEmpty()) {
            throw new NoDataFoundException("No sales found for the given date range.");
        }
        return sales;
    }

    // Volume Report (product, total quantity); number of sales per product basically
    public Map<String, Long> createVolumeReport(LocalDateTime start, LocalDateTime end) {
        List<Sale> sales = saleRepository.findByTimestampBetween(start, end);
        if (sales.isEmpty()) {
            throw new NoDataFoundException("No sales found for the given date range.");
        }
        return sales.stream()
                .collect(Collectors.groupingBy(
                        sale -> {
                            Product product = productRepository.findByColumnId(sale.getColumnId());
                            if (product == null) {
                                throw new ColumnValidationException("Product not found for column ID: " + sale.getColumnId());
                            }
                            return product.getName();
                        },
                        Collectors.counting()
                ));
    }
}
