package com.example.demo.dto;

import java.util.List;

public record RecommendationResponse(
        List<Item> top3,
        String rationale
) {
    public record Item(String label, double totalCost, String explanation, Object details){}
}