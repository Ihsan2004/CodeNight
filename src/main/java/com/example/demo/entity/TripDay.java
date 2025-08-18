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
}
