package org.vendingmachine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.vendingmachine.exception.ColumnValidationException;
import org.vendingmachine.exception.NoDataFoundException;
import org.vendingmachine.model.Product;
import org.vendingmachine.model.Sale;
import org.vendingmachine.repository.ProductRepository;
import org.vendingmachine.repository.SaleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SaleReportsServiceTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private SaleReportsService saleReportsService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createStockReport_withProducts_returnsStockReportMap() {
        // Arrange
        Product p1 = new Product(1, "Eugenia", 3.2f, 10);
        Product p2 = new Product(2, "Biscuitei", 6.9f, 5);
        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        // Act
        Map<String, Integer> result = saleReportsService.createStockReport();

        // Assert
        assertEquals(2, result.size());
        assertEquals(10, result.get("Eugenia"));
        assertEquals(5, result.get("Biscuitei"));
    }

    @Test
    void createStockReport_noProducts_throwsNoDataFoundException() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of());

        // Act & Assert
        assertThrows(NoDataFoundException.class, () -> saleReportsService.createStockReport());
    }

    @Test
    void createSalesReport_withSales_returnsSalesReportList() {
        // Arrange
        Sale s1 = new Sale(1, 3.2f, "cash", 1.8f, LocalDateTime.now());
        Sale s2 = new Sale(2, 6.9f, "card", 0f, LocalDateTime.now());
        when(saleRepository.findByTimestampBetween(any(), any())).thenReturn(List.of(s1, s2));

        // Act
        List<Sale> result = saleReportsService.createSalesReport(LocalDateTime.now().minusDays(1), LocalDateTime.now());

        // Assert
        assertEquals(2, result.size());
        assertEquals("cash", result.get(0).getPaymentMethod());
        assertEquals("card", result.get(1).getPaymentMethod());
    }

    @Test
    void createSalesReport_noSales_throwsNoDataFoundException() {
        // Arrange
        when(saleRepository.findByTimestampBetween(any(), any())).thenReturn(List.of());

        // Act & Assert
        assertThrows(NoDataFoundException.class, () ->
                saleReportsService.createSalesReport(LocalDateTime.now().minusDays(1), LocalDateTime.now()));
    }

    @Test
    void createVolumeReport_withSales_returnsVolumeReportMap() {
        // Arrange
        Sale s1 = new Sale(1, 3.2f, "cash", 1.8f, LocalDateTime.now());
        Sale s2 = new Sale(2, 6.9f, "card", 0f, LocalDateTime.now());
        Sale s3 = new Sale(1, 3.2f, "card", 0f, LocalDateTime.now());

        Product p1 = new Product(1, "Eugenia", 3.2f, 10);
        Product p2 = new Product(2, "Biscuitei", 6.9f, 5);

        when(saleRepository.findByTimestampBetween(any(), any())).thenReturn(List.of(s1, s2, s3));
        when(productRepository.findByColumnId(1)).thenReturn(p1);
        when(productRepository.findByColumnId(2)).thenReturn(p2);

        // Act
        Map<String, Long> result = saleReportsService.createVolumeReport(LocalDateTime.now().minusDays(1), LocalDateTime.now());

        // Assert
        assertEquals(2, result.size());
        assertEquals(2L, result.get("Eugenia"));
        assertEquals(1L, result.get("Biscuitei"));
    }

    @Test
    void createVolumeReport_noSales_throwsNoDataFoundException() {
        // Arrange
        when(saleRepository.findByTimestampBetween(any(), any())).thenReturn(List.of());

        // Act & Assert
        assertThrows(NoDataFoundException.class, () ->
                saleReportsService.createVolumeReport(LocalDateTime.now().minusDays(1), LocalDateTime.now()));
    }

    @Test
    void createVolumeReport_invalidColumn_throwsColumnValidationException() {
        // Arrange
        Sale sale = new Sale(99999, 6.9f, "card", 0f, LocalDateTime.now());

        when(saleRepository.findByTimestampBetween(any(), any())).thenReturn(List.of(sale));
        when(productRepository.findByColumnId(99999)).thenReturn(null);

        // Act & Assert
        assertThrows(ColumnValidationException.class, () ->
                saleReportsService.createVolumeReport(LocalDateTime.now().minusDays(1), LocalDateTime.now()));
    }
}
