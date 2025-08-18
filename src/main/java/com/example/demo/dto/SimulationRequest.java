package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

public record SimulationRequest(
        Long userId,
        List<Leg> trips, // çok ülke desteği
        UsageProfileDto profile) {
    public record Leg(String countryCode, LocalDate startDate, LocalDate endDate) {
    }

    public record UsageProfileDto(Integer avgDailyMb, Integer avgDailyMin, Integer avgDailySms) {
    }
}
