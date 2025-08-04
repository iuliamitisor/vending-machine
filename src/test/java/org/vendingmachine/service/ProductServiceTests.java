package org.vendingmachine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.vendingmachine.exception.*;
import org.vendingmachine.model.Product;
import org.vendingmachine.repository.ProductRepository;
import org.vendingmachine.repository.SaleRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SaleRepository saleRepository;

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

    @Test
    void validateCashPayment_neededAmount_decrementsStock_andSaves() {
        // Arrange
        Product product = new Product(1, "Biscuitei", 6.9f, 3);

        // Act
        productService.validateCashPayment(product, 6.9f, 1);

        // Assert
        assertEquals(2, product.getQuantity());
        verify(productRepository).save(product);
    }

    @Test
    void validateCashPayment_insufficientAmount_throwsPaymentValidation() {
        // Arrange
        Product product = new Product(1, "Biscuitei", 6.9f, 3);

        // Act & Assert
        assertThrows(PaymentValidationException.class, () -> productService.validateCashPayment(product, 1.0f, 1));
        verify(productRepository, never()).save(any());
    }

    @Test
    void validateCashPayment_negativeAmount_throwsInvalidCashAmount() {
        // Arrange
        Product product = new Product(1, "Biscuitei", 6.9f, 3);

        // Act & Assert
        assertThrows(InvalidCashAmountException.class, () -> productService.validateCashPayment(product, -1.0f, 1));
        verify(productRepository, never()).save(any());
    }

    @Test
    void validateCashPayment_excessAmount_throwsExcessCashException() {
        // Arrange
        Product product = new Product(1, "Biscuitei", 6.9f, 3);

        // Act & Assert
        ExcessCashException ex = assertThrows(ExcessCashException.class, () -> productService.validateCashPayment(product, 7.0f, 1));
        assertEquals(0.1f, ex.getChangeAmount(), 0.0001f);
        assertEquals(1, ex.getColumnId());
        verify(productRepository).save(product);
    }

    @Test
    void validateCardPayment_validPayment_decrementsStock_andSaves() {
        // Arrange
        Product product = new Product(1, "Biscuitei", 6.9f, 3);

        // Act
        productService.validateCardPayment(product);

        // Assert
        assertEquals(2, product.getQuantity());
        verify(productRepository).save(product);
    }

}