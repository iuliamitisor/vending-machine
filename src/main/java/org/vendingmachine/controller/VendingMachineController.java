package org.vendingmachine.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.vendingmachine.model.Product;
import org.vendingmachine.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    @Bean
    CommandLineRunner runner(ProductRepository repository) {
        return args -> {
            repository.save(new Product(1, "Eugenia", 3.2f, 10));
            repository.save(new Product(2, "Pepsi", 7.5f, 8));
            repository.save(new Product(3, "Chio Chips", 12.0f, 5));
            repository.save(new Product(4, "Snickers", 2.5f, 15));
            repository.save(new Product(5, "Coca Cola", 7.0f, 20));
            repository.save(new Product(6, "Fanta", 7.0f, 12));
            repository.save(new Product(7, "Mars", 3.0f, 10));
            repository.save(new Product(8, "Twix", 4.0f, 7));
            repository.save(new Product(9, "Red Bull", 8.0f, 6));
        };
    }

}
