package org.vendingmachine.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.vendingmachine.exception.*;
import org.vendingmachine.model.Product;
import org.vendingmachine.repository.ProductRepository;
import org.vendingmachine.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VendingMachineController {
    private final ProductService productService;

    public VendingMachineController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/products")
    public String products(@RequestParam(required = false) String iderror,
                           @RequestParam(required = false) String stockerror,
                           @RequestParam(required = false) String success,
                           Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("adminflag", false);
        model.addAttribute("iderror", iderror != null);
        model.addAttribute("stockerror", stockerror != null);
        model.addAttribute("success", success != null);
        return "products";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, Model model) {
        model.addAttribute("error", error != null);
        return "login";
    }

    @GetMapping("/adminpanel")
    public String adminPanel() {
        return "adminpanel";
    }

    @GetMapping("/adminpanel/products")
    public String adminProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("adminflag", true);
        return "products";
    }

    @GetMapping("/payment")
    public String payment(@RequestParam(required = false) String error,
                          @RequestParam(required = false) String amounterror,
                          @RequestParam(required = false) String givechange,
                          @RequestParam(required = false) String change,
                          @RequestParam int columnId,
                          Model model) {
        model.addAttribute("error", error != null);
        model.addAttribute("amounterror", amounterror != null);
        model.addAttribute("givechange", givechange != null);
        model.addAttribute("change", change);
        model.addAttribute("columnId", columnId);
        return "payment";
    }


    @PostMapping("/admin")
    public String submitLogin(@RequestParam String password) {
        if ("passadmin".equals(password)) {
            return "redirect:/adminpanel";
        }
        return "redirect:/login?error=true";
    }

    @PostMapping("/logout")
    public String submitLogout() {
        return "redirect:/";
    }

    @PostMapping("/buy")
    public String buyProduct(@RequestParam int columnId) {
        productService.buyProduct(columnId);
        return "redirect:/payment?columnId=" + columnId;
    }

    @PostMapping("/pay")
    public String pay(@RequestParam String paymentMethod,
                      @RequestParam(required = false) Float cashAmount,
                      @RequestParam int columnId,
                      Model model) {
        if ("cash".equals(paymentMethod)) {
            productService.validateCashPayment(productService.findByColumn(columnId), cashAmount, columnId);
            model.addAttribute("success", true);
            return "redirect:/products?success=true";
        } else if ("card".equals(paymentMethod)) {
            productService.validateCardPayment(productService.findByColumn(columnId));
            model.addAttribute("success", true);
            return "redirect:/products?success=true";
        }
        return "redirect:/payment?error=true";
    }


    @ExceptionHandler(ColumnValidationException.class)
    public String handleProductNotFound(ColumnValidationException ex) {
        return "redirect:/products?iderror=true";
    }

    @ExceptionHandler(InsufficientStockException.class)
    public String handleOutOfStock(InsufficientStockException ex) {
        return "redirect:/products?stockerror=true";
    }

    @ExceptionHandler(InvalidCashAmountException.class)
    public String handleInvalidCashAmountException(InvalidCashAmountException ex) {
        return "redirect:/payment?amounterror=true&columnId=" + ex.getColumnId();
    }

    @ExceptionHandler(PaymentValidationException.class)
    public String handlePaymentValidationException(PaymentValidationException ex) {
        return "redirect:/payment?error=true&columnId=" + ex.getColumnId();
    }

    @ExceptionHandler(ExcessCashException.class)
    public String handleExcessCashException(ExcessCashException ex) {
        return "redirect:/payment?givechange=true&columnId=" + ex.getColumnId() + "&change=" + ex.getChangeAmount();
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
