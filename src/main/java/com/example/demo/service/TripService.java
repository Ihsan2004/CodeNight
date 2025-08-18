package com.example.demo.service;


import com.example.demo.dto.SimulationRequest;
import com.example.demo.entity.Trip;
import com.example.demo.entity.TripDay;
import com.example.demo.repository.TripDayRepository;
import com.example.demo.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TripService{

    private final TripRepository tripRepo;
    private final TripDayRepository tripDayRepo;

    public int countDaysInclusive(SimulationRequest.Leg leg) {
        return (int)(ChronoUnit.DAYS.between(leg.startDate(), leg.endDate()) + 1);
    }

    @Transactional
    public Trip createTripWithDays(Trip trip) {
        Trip saved = tripRepo.save(trip);
        var days = new ArrayList<TripDay>();
        LocalDate d = saved.getStartDate();
        long cnt = ChronoUnit.DAYS.between(saved.getStartDate(), saved.getEndDate()) + 1;
        for (int i=0;i<cnt;i++) {
            var td = new TripDay();
            td.setTrip(saved);
            td.setDate(d);
            td.setCountryCode1(saved.getCountryCode1());
            td.setMultiCountry(false);
            days.add(td);
            d = d.plusDays(1);
        }
        tripDayRepo.saveAll(days);
        saved.setTripDays(days);
        return saved;
    }
}

