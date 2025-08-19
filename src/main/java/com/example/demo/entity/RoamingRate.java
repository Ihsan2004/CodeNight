package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "roaming_rates")
public class RoamingRate {
    @Id
    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "data_per_mb", nullable = false)
    private Double dataPerMb;

    @Column(name = "voice_per_min", nullable = false)
    private Double voicePerMin;

    @Column(name = "sms_per_msg", nullable = false)
    private Double smsPerMsg;

    @Column(name = "currency", nullable = false)
    private String currency;

    // Default constructor for JPA
    public RoamingRate() {
    }

    // Constructor with ID for manual ID assignment
    public RoamingRate(String countryCode) {
        this.countryCode = countryCode;
    }

    // Full constructor for all fields
    public RoamingRate(String countryCode, Double dataPerMb, Double voicePerMin,
            Double smsPerMsg, String currency) {
        this.countryCode = countryCode;
        this.dataPerMb = dataPerMb;
        this.voicePerMin = voicePerMin;
        this.smsPerMsg = smsPerMsg;
        this.currency = currency;
    }
}
