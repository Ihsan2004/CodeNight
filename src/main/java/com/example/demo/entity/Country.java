package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "countries")
public class Country {
    @Id
    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "country_name", nullable = false)
    private String countryName;

    @Column(name = "region", nullable = false)
    private String region;

    // Default constructor for JPA
    public Country() {
    }

    // Constructor with ID for manual ID assignment
    public Country(String countryCode) {
        this.countryCode = countryCode;
    }

    // Full constructor for all fields
    public Country(String countryCode, String countryName, String region) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.region = region;
    }
}
