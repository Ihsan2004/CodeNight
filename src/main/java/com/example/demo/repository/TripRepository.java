package com.example.demo.repository;

import com.example.demo.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUserId(Long userId);
    List<Trip> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDate endInclusive, LocalDate startInclusive);
}

