package org.vendingmachine.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.vendingmachine.repository.ProductRepository;
import org.vendingmachine.service.ProductService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(AdminController.class)
class AdminControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private ProductService productService;

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

}
