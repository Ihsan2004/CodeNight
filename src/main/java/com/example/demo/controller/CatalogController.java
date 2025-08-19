package com.example.demo.controller;

import com.example.demo.dto.CatalogResponse;
import com.example.demo.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
@CrossOrigin
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Catalog controller is accessible");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping
    public CatalogResponse getCatalog() {
        return catalogService.getCatalog();
    }
}
