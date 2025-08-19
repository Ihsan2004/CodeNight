package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "roaming_packs")
public class RoamingPack {
    @Id
    @Column(name = "pack_id")
    private Long packId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "coverage", nullable = false)
    private String coverage;

    @Column(name = "coverage_type", nullable = false)
    private String coverageType;

    @Column(name = "data_gb", nullable = false)
    private Integer dataGb;

    @Column(name = "voice_min", nullable = false)
    private Integer voiceMin;

    @Column(name = "sms", nullable = false)
    private Integer sms;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "validity_days", nullable = false)
    private Integer validityDays;

    @Column(name = "currency", nullable = false)
    private String currency;

    // Default constructor for JPA
    public RoamingPack() {
    }

    // Constructor with ID for manual ID assignment
    public RoamingPack(Long packId) {
        this.packId = packId;
    }

    // Full constructor for all fields
    public RoamingPack(Long packId, String name, String coverage, String coverageType,
            Integer dataGb, Integer voiceMin, Integer sms, Double price,
            Integer validityDays, String currency) {
        this.packId = packId;
        this.name = name;
        this.coverage = coverage;
        this.coverageType = coverageType;
        this.dataGb = dataGb;
        this.voiceMin = voiceMin;
        this.sms = sms;
        this.price = price;
        this.validityDays = validityDays;
        this.currency = currency;
    }
}
