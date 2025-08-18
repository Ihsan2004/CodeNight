package com.example.demo.controller;

import com.example.demo.entity.Trip;
import com.example.demo.repository.TripRepository;
import com.example.demo.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final TripRepository tripRepository;

    @PostMapping
    public Trip create(@RequestBody Trip trip) {
        return tripService.createTripWithDays(trip);
    }

    @GetMapping("/user/{userId}")
    public List<Trip> getByUser(@PathVariable Long userId) {
        return tripRepository.findByUserId(userId);
    }

    @GetMapping
    public List<Trip> all() {
        return tripRepository.findAll();
    }
}
