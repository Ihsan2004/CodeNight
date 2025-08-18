package com.example.demo.controller;

import com.example.demo.dto.CatalogResponse;
import com.example.demo.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping
    public CatalogResponse getCatalog() {
        return catalogService.getCatalog();
    }
}
