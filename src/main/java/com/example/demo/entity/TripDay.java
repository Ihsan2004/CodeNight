package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "trip_days")
public class TripDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Hangi trip’e ait olduğunu göstermek için foreign key
    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    // Eğer sadece tek ülke varsa bu kolon dolacak
    @Column(name = "country_code1", nullable = false)
    private String countryCode1;

    // Eğer aynı gün ikinci ülkeye geçildiyse
    @Column(name = "country_code2")
    private String countryCode2;

    // O gün birden fazla ülke mi gezildi?
    @Column(name = "multi_country", nullable = false)
    private boolean multiCountry;

    // Default constructor for JPA
    public TripDay() {
    }

    // Constructor with ID for manual ID assignment
    public TripDay(Long id) {
        this.id = id;
    }

    // Full constructor for all fields
    public TripDay(Long id, Trip trip, LocalDate date, String countryCode1,
            String countryCode2, boolean multiCountry) {
        this.id = id;
        this.trip = trip;
        this.date = date;
        this.countryCode1 = countryCode1;
        this.countryCode2 = countryCode2;
        this.multiCountry = multiCountry;
    }
}
