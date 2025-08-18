package com.example.demo.repository;

import com.example.demo.entity.UsageProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsageProfileRepository extends JpaRepository<UsageProfile, Long> {
    default UsageProfile getOrThrow(Long userId) {
        return findById(userId).orElseThrow(() -> new IllegalArgumentException("UsageProfile not found: " + userId));
    }
}
