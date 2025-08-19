package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "home_plan", nullable = false)
    private String homePlan;

    // Default constructor for JPA
    public User() {
    }

    // Constructor with ID for manual ID assignment
    public User(Long userId) {
        this.userId = userId;
    }

    // Full constructor for all fields
    public User(Long userId, String name, String homePlan) {
        this.userId = userId;
        this.name = name;
        this.homePlan = homePlan;
    }
}
