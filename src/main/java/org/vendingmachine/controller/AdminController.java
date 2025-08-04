package org.vendingmachine.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.vendingmachine.model.Sale;
import org.vendingmachine.service.ProductService;
import org.vendingmachine.service.SaleReportsService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AdminController {

    private final ProductService productService;
    private final SaleReportsService saleReportsService;

    public AdminController(ProductService productService, SaleReportsService saleReportsService) {
        this.productService = productService;
        this.saleReportsService = saleReportsService;
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

    @GetMapping("/adminpanel/stockreport")
    public ResponseEntity<Object> getStockReport() {
        try {
            var stock = saleReportsService.createStockReport();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"stockreport.json\"")
                    .body(stock);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while generating the stock report: " + e.getMessage());
        }
    }

    @GetMapping("/adminpanel/salesreport")
    public ResponseEntity<Object> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        try {
            LocalDateTime startDateTime = start.atStartOfDay();
            LocalDateTime endDateTime = end.atTime(23, 59, 59);

            List<Sale> sales = saleReportsService.createSalesReport(startDateTime, endDateTime);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"salesreport.json\"")
                    .body(sales);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while generating the sales report: " + e.getMessage());
        }
    }

    @GetMapping("/adminpanel/volumereport")
    public ResponseEntity<Object> getVolumeReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        try {
            LocalDateTime startDateTime = start.atStartOfDay();
            LocalDateTime endDateTime = end.atTime(23, 59, 59);

            var volume = saleReportsService.createVolumeReport(startDateTime, endDateTime);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"volumereport.json\"")
                    .body(volume);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while generating the volume report: " + e.getMessage());
        }
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

}