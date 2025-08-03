package org.vendingmachine.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.vendingmachine.exception.*;
import org.vendingmachine.model.Product;
import org.vendingmachine.service.ProductService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void productsPage_displaysProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attribute("iderror", false))
                .andExpect(model().attribute("stockerror", false))
                .andExpect(model().attribute("success", false));
    }

    @Test
    void paymentPage_allFlagsSet_setsAttributesCorrectly() throws Exception {
        mockMvc.perform(get("/payment")
                        .param("error", "true")
                        .param("amounterror", "true")
                        .param("givechange", "true")
                        .param("change", "5.0")
                        .param("columnId", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("payment"))
                .andExpect(model().attribute("error", true))
                .andExpect(model().attribute("amounterror", true))
                .andExpect(model().attribute("givechange", true))
                .andExpect(model().attribute("change", "5.0"))
                .andExpect(model().attribute("columnId", 2));
    }

    @Test
    void buyProduct_redirectsToPaymentPage() throws Exception {
        mockMvc.perform(post("/buy")
                        .param("columnId", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment?columnId=3"));

        verify(productService).buyProduct(3);
    }

    @Test
    void pay_withCash_callsCashValidationAndRedirects() throws Exception {
        var mockProduct = new Product(1, "Snickers", 2.5f, 10);
        when(productService.findByColumn(1)).thenReturn(mockProduct);

        mockMvc.perform(post("/pay")
                        .param("paymentMethod", "cash")
                        .param("cashAmount", "5.0")
                        .param("columnId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products?success=true"));

        verify(productService).validateCashPayment(mockProduct, 5.0f, 1);
    }

    @Test
    void pay_withCard_callsCardValidationAndRedirects() throws Exception {
        var mockProduct = new Product(2, "Coke", 3.0f, 5);
        when(productService.findByColumn(2)).thenReturn(mockProduct);

        mockMvc.perform(post("/pay")
                        .param("paymentMethod", "card")
                        .param("columnId", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products?success=true"));

        verify(productService).validateCardPayment(mockProduct);
    }

    @Test
    void pay_withInvalidMethod_redirectsToPaymentWithError() throws Exception {
        mockMvc.perform(post("/pay")
                        .param("paymentMethod", "balarii")
                        .param("columnId", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment?error=true"));
    }

    // Exception handlers
    @Test
    void handleColumnValidationException_redirectsToProductsWithIdError() throws Exception {
        doThrow(new ColumnValidationException("Invalid column"))
                .when(productService).buyProduct(-1);

        mockMvc.perform(post("/buy")
                        .param("columnId", "-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products?iderror=true"));
    }

    @Test
    void handleOutOfStock_redirectsToProductsWithStockError() throws Exception {
        doThrow(new InsufficientStockException("Product out of stock"))
                .when(productService).buyProduct(1);

        mockMvc.perform(post("/buy")
                        .param("columnId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products?stockerror=true"));
    }

    @Test
    void handleInvalidCashAmountException_redirectsToPaymentWithAmountError() throws Exception {
        doThrow(new InvalidCashAmountException("Invalid cash amount", 2))
                .when(productService).validateCashPayment(any(), anyFloat(), eq(2));

        mockMvc.perform(post("/pay")
                        .param("paymentMethod", "cash")
                        .param("cashAmount", "1.0")
                        .param("columnId", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment?amounterror=true&columnId=2"));
    }

    @Test
    void handlePaymentValidationException_redirectsToPaymentWithError() throws Exception {
        doThrow(new PaymentValidationException("Payment validation failed", 3))
                .when(productService).validateCardPayment(any());

        mockMvc.perform(post("/pay")
                        .param("paymentMethod", "card")
                        .param("columnId", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment?error=true&columnId=3"));
    }

    @Test
    void handleExcessCashException_redirectsToPaymentWithChange() throws Exception {
        doThrow(new ExcessCashException("Excess cash inserted", 4, 2.5f))
                .when(productService).validateCashPayment(any(), anyFloat(), eq(4));

        mockMvc.perform(post("/pay")
                        .param("paymentMethod", "cash")
                        .param("cashAmount", "10.0")
                        .param("columnId", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment?givechange=true&columnId=4&change=2.5"));
    }

}

