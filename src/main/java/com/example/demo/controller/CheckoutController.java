package com.example.demo.controller;

import com.example.demo.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody CheckoutRequest request) {
        try {
            log.info("Processing checkout request for user: {}", request.getUserId());

            String orderId = checkoutService.processCheckout(
                    request.getUserId(),
                    request.getSelectedOption(),
                    request.getTotalCost(),
                    request.getCurrency());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "ok");
            response.put("order_id", orderId);
            response.put("user_id", request.getUserId());
            response.put("selected_option", request.getSelectedOption());
            response.put("total_cost", request.getTotalCost());
            response.put("currency", request.getCurrency());

            log.info("Checkout successful for user: {}, order ID: {}", request.getUserId(), orderId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Checkout failed for user: {}", request.getUserId(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Checkout processing failed: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // DTO for checkout request
    public static class CheckoutRequest {
        private Long userId;
        private String selectedOption;
        private BigDecimal totalCost;
        private String currency;

        // Getters and Setters
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getSelectedOption() {
            return selectedOption;
        }

        public void setSelectedOption(String selectedOption) {
            this.selectedOption = selectedOption;
        }

        public BigDecimal getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(BigDecimal totalCost) {
            this.totalCost = totalCost;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }
}
