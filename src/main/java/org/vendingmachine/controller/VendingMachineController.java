package org.vendingmachine.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.vendingmachine.model.Product;
import org.vendingmachine.model.Sale;
import org.vendingmachine.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.vendingmachine.repository.SaleRepository;

import java.time.LocalDateTime;

@Controller
public class VendingMachineController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

}
