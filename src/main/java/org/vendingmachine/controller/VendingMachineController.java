package org.vendingmachine.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.vendingmachine.exception.ColumnValidationException;
import org.vendingmachine.exception.InsufficientStockException;
import org.vendingmachine.exception.InvalidCashAmountException;
import org.vendingmachine.exception.PaymentValidationException;
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
                          @RequestParam int columnId,
                          Model model) {
        model.addAttribute("error", error != null);
        model.addAttribute("amounterror", amounterror != null); // Fix this line
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
                      @RequestParam(required = false) Double cashAmount,
                      @RequestParam int columnId,
                      Model model) {
        if ("cash".equals(paymentMethod)) {
            productService.validateCashPayment(productService.findByColumn(columnId), cashAmount, columnId);
            model.addAttribute("success", true);
            return "redirect:/products?success=true";
        } else if ("card".equals(paymentMethod)) {
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

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}
