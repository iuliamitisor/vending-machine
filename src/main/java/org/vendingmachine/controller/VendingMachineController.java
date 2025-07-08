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
        Product requestedProduct = productService.findByColumn(columnId);
        if (requestedProduct == null) {
            return "redirect:/products?iderror=true";
        }
        if (requestedProduct.getQuantity() <= 0) {
            return "redirect:/products?stockerror=true";
        }
        requestedProduct.decrementQuantity(1);
        return "redirect:/products?success=true";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}
