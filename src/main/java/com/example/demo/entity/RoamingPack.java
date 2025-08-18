package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "roaming_packs")

public class RoamingPack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    // Getters & Setters
}

