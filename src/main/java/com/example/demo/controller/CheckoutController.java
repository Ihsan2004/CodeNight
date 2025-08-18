package com.example.demo.controller;

import com.example.demo.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public Map<String, Object> checkout(@RequestParam Long userId,
                                        @RequestBody Object selection) {
        return checkoutService.checkout(userId, selection);
    }
}
