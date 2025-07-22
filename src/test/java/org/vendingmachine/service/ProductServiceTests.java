package org.vendingmachine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.vendingmachine.exception.*;
import org.vendingmachine.model.Product;
import org.vendingmachine.repository.ProductRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void buyProduct_validRequest() {
        // Arrange
        Product product = new Product(1, "Biscuitei", 6.9f, 5);
        when(productRepository.findByColumnId(1)).thenReturn(product);

        // Act
        productService.buyProduct(1);

        // Assert
        assertEquals(5, product.getQuantity()); // quantity should not change until payment
        verify(productRepository, never()).save(any()); // no save until after payment validation
    }

    @Test
    void buyProduct_invalidColumn_throwsException() {
        // Arrange
        when(productRepository.findByColumnId(1)).thenReturn(null);

        // Act & Assert
        assertThrows(ColumnValidationException.class, () -> productService.buyProduct(1));
        verify(productRepository, never()).save(any());
    }

    @Test
    void buyProduct_insufficientStock_throwsException() {
        // Arrange
        Product product = new Product(1, "Biscuitei", 6.9f, 0);
        when(productRepository.findByColumnId(1)).thenReturn(product);

        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> productService.buyProduct(1));
        verify(productRepository, never()).save(any());
    }

}