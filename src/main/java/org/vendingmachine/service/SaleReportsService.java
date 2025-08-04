package org.vendingmachine.service;

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
        return productRepository.findAll().stream()
                .collect(Collectors.toMap(Product::getName, Product::getQuantity));
    }

    // Sales Report (date, product, price, payment-method)
    public List<Sale> createSalesReport(LocalDateTime start, LocalDateTime end) {
        return saleRepository.findByTimestampBetween(start, end);
    }

    // Volume Report (product, total quantity); number of sales per product basically
    public Map<String, Long> createVolumeReport(LocalDateTime start, LocalDateTime end) {
        return saleRepository.findByTimestampBetween(start, end).stream()
                .collect(Collectors.groupingBy(
                        sale -> productRepository.findByColumnId(sale.getColumnId()).getName(),
                        Collectors.counting()
                ));
    }
}
