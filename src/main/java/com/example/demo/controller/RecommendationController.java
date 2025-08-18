package com.example.demo.controller;

import com.example.demo.dto.RecommendationResponse;
import com.example.demo.dto.SimulationRequest;
import com.example.demo.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    public RecommendationResponse recommend(@RequestBody SimulationRequest req) {
        return recommendationService.recommendTop3(req);
    }
}
