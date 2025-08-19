package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
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

    // Default constructor for JPA
    public UsageProfile() {
    }

    // Constructor with ID for manual ID assignment
    public UsageProfile(Long userId) {
        this.userId = userId;
    }

    // Full constructor for all fields
    public UsageProfile(Long userId, Integer avgDailyMb, Integer avgDailyMin, Integer avgDailySms) {
        this.userId = userId;
        this.avgDailyMb = avgDailyMb;
        this.avgDailyMin = avgDailyMin;
        this.avgDailySms = avgDailySms;
    }
}
