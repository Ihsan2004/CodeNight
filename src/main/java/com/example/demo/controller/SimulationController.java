package com.example.demo.controller;

import com.example.demo.dto.SimulationRequest;
import com.example.demo.dto.SimulationResponse;
import com.example.demo.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/simulate")
@RequiredArgsConstructor
@CrossOrigin
public class SimulationController {

    private final SimulationService simulationService;

    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Simulation controller is accessible");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @PostMapping
    public SimulationResponse simulate(@RequestBody SimulationRequest req) {
        return simulationService.simulate(req);
    }
}
