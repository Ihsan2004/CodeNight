package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "usage_profile")
public class UsageProfile {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "avg_daily_mb", nullable = false)
    private Integer avgDailyMb;

    @Column(name = "avg_daily_min", nullable = false)
    private Integer avgDailyMin;

    @Column(name = "avg_daily_sms", nullable = false)
    private Integer avgDailySms;

    // Getters & Setters
}

