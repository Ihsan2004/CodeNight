package com.example.demo.repository;

import com.example.demo.entity.TripDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TripDayRepository extends JpaRepository<TripDay, Long> {
    // Bir trip’in tüm günleri
    List<TripDay> findByTrip_TripId(Long tripId);

    // Belirli tarihteki günler (çok ülkeli gün kontrolü vs.)
    List<TripDay> findByDate(LocalDate date);

    // Kullanışlı: belirli ülke koduna ait günler
    List<TripDay> findByCountryCode1(String countryCode1);
}

