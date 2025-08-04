package org.vendingmachine.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.vendingmachine.model.Sale;
import org.vendingmachine.service.ProductService;
import org.vendingmachine.service.SaleReportsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private SaleReportsService saleReportsService;

    @Test
    void adminPanel_returnsAdminPanel() throws Exception {
        mockMvc.perform(get("/adminpanel"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminpanel"));
    }

    @Test
    void submitLogin_correctPassword_redirectsToAdminPanel() throws Exception {
        mockMvc.perform(post("/admin").param("password", "passadmin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/adminpanel"));
    }

    @Test
    void submitLogin_wrongPassword_redirectsToLoginError() throws Exception {
        mockMvc.perform(post("/admin").param("password", "balarii"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void adminProducts_displaysProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of());
        mockMvc.perform(get("/adminpanel/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("adminflag", true));
    }

    @Test
    void submitLogout_redirectsToHomePage() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void getStockReport_returnsJsonSuccessfully() throws Exception {
        when(saleReportsService.createStockReport())
                .thenReturn(Map.of("Eugenia", 10));

        mockMvc.perform(get("/adminpanel/stockreport"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"stockreport.json\""))
                .andExpect(jsonPath("$.Eugenia").value(10));
    }

    @Test
    void getStockReport_handlesException() throws Exception {
        when(saleReportsService.createStockReport())
                .thenThrow(new RuntimeException("ERROR"));

        mockMvc.perform(get("/adminpanel/stockreport"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred while generating the stock report: ERROR"));
    }

    @Test
    void getSalesReport_returnsSalesSuccessfully() throws Exception {
        Sale newSale = new Sale(1, 2.5f, "cash", 1, LocalDateTime.now());
        when(saleReportsService.createSalesReport(any(), any()))
                .thenReturn(List.of(newSale));

        mockMvc.perform(get("/adminpanel/salesreport")
                        .param("start", "2025-01-01")
                        .param("end", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"salesreport.json\""))
                .andExpect(jsonPath("$[0].columnId").value(1))
                .andExpect(jsonPath("$[0].price").value(2.5))
                .andExpect(jsonPath("$[0].paymentMethod").value("cash"));
    }

    @Test
    void getSalesReport_handlesException() throws Exception {
        when(saleReportsService.createSalesReport(any(), any()))
                .thenThrow(new RuntimeException("ERROR"));

        mockMvc.perform(get("/adminpanel/salesreport")
                        .param("start", "2025-01-01")
                        .param("end", "2025-12-31"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred while generating the sales report: ERROR"));
    }

    @Test
    void getVolumeReport_returnsVolumeSuccessfully() throws Exception {
        when(saleReportsService.createVolumeReport(any(), any()))
                .thenReturn(Map.of("Eugenia", 7L));

        mockMvc.perform(get("/adminpanel/volumereport")
                        .param("start", "2025-01-01")
                        .param("end", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"volumereport.json\""))
                .andExpect(jsonPath("$.Eugenia").value(7));
    }

    @Test
    void getVolumeReport_handlesException() throws Exception {
        when(saleReportsService.createVolumeReport(any(), any()))
                .thenThrow(new RuntimeException("ERROR"));

        mockMvc.perform(get("/adminpanel/volumereport")
                        .param("start", "2025-01-01")
                        .param("end", "2025-12-31"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred while generating the volume report: ERROR"));
    }
}
