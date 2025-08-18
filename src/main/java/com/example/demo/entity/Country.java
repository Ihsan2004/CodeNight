package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "countries")
public class Country {
    @Id
    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "country_name", nullable = false)
    private String countryName;

    @Column(name = "region", nullable = false)
    private String region;

    // Getters & Setters
}
