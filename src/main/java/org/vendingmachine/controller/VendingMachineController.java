package org.vendingmachine.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.vendingmachine.model.Product;
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
    public String payment() {
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
        return "redirect:/payment";
    }

    @PostMapping("/pay")
    public String pay(@RequestParam String paymentMethod, Model model) {
        if ("cash".equals(paymentMethod)) {
            model.addAttribute("success", true);
            return "redirect:/products?success=true";
        } else if ("card".equals(paymentMethod)) {
            model.addAttribute("success", true);
            return "redirect:/products?success=true";
        }
        return "redirect:/payment";
    }


    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        if (ex.getMessage().equals("Invalid column number.")) {
            model.addAttribute("iderror", true);
            return "redirect:/products?iderror=true";
        } else if (ex.getMessage().equals("Insufficient stock.")) {
            model.addAttribute("stockerror", true);
            return "redirect:/products?stockerror=true";
        }
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}
