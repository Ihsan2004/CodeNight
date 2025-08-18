package com.example.demo.repository;

import com.example.demo.entity.RoamingPack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoamingPackRepository extends JpaRepository<RoamingPack, Long> {
    List<RoamingPack> findByCoverageType(String coverageType);
    List<RoamingPack> findByCoverage(String coverage);
}
