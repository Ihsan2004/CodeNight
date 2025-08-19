package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "trips")
public class Trip {
    @Id
    @Column(name = "trip_id")
    private Long tripId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // Eğer sadece tek ülke seçilmişse
    @Column(name = "country_code1", nullable = false)
    private String countryCode1;

    // İkinci ülke (opsiyonel)
    @Column(name = "country_code2")
    private String countryCode2;

    @Column(name = "multi_country", nullable = false)
    private boolean multiCountry;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // Trip ↔ TripDay ilişki (bir Trip’in birden fazla günü olabilir)
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripDay> tripDays;

    // Default constructor for JPA
    public Trip() {
    }

    // Constructor with ID for manual ID assignment
    public Trip(Long tripId) {
        this.tripId = tripId;
    }

    // Full constructor for all fields
    public Trip(Long tripId, Long userId, String countryCode1, String countryCode2,
            boolean multiCountry, LocalDate startDate, LocalDate endDate) {
        this.tripId = tripId;
        this.userId = userId;
        this.countryCode1 = countryCode1;
        this.countryCode2 = countryCode2;
        this.multiCountry = multiCountry;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
