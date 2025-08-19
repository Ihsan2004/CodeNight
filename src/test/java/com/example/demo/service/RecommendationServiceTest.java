package com.example.demo.service;

import com.example.demo.dto.RecommendationResponse;
import com.example.demo.dto.SimulationRequest;
import com.example.demo.dto.SimulationResponse;
import com.example.demo.entity.RoamingPack;
import com.example.demo.repository.RoamingPackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private SimulationService simulationService;

    @Mock
    private RoamingPackRepository roamingPackRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    private SimulationRequest testRequest;
    private SimulationResponse testSimulationResponse;
    private RoamingPack testPack;

    @BeforeEach
    void setUp() {
        testRequest = new SimulationRequest(
            1001L,
            Arrays.asList(
                new SimulationRequest.Leg("DE", java.time.LocalDate.of(2025, 8, 20), java.time.LocalDate.of(2025, 8, 25))
            ),
            new SimulationRequest.UsageProfileDto(600, 10, 2)
        );

        testPack = new RoamingPack(201L, "Avrupa 5GB", "Europe", "region", 5, 50, 50, 19.9, 7, "EUR");

        // Mock simulation response with different cost options
        var paygOption = new SimulationResponse.Option("payg", null, 0, 45.50, "EUR", true, true, null);
        var packOption1 = new SimulationResponse.Option("pack", 201L, 2, 39.80, "EUR", true, false, null);
        var packOption2 = new SimulationResponse.Option("pack", 202L, 1, 29.90, "EUR", true, true, null);

        testSimulationResponse = new SimulationResponse(
            new SimulationResponse.Summary(6, new SimulationResponse.Needs(3.5, 60, 12)),
            Arrays.asList(paygOption, packOption1, packOption2), // Sıralama: en ucuzdan en pahalıya
            Arrays.asList("Paket geçerliliği kısa")
        );
    }

    @Test
    void testRecommendTop3CostRanking() {
        // Given
        when(simulationService.simulate(any(SimulationRequest.class))).thenReturn(testSimulationResponse);
        when(roamingPackRepository.findById(201L)).thenReturn(Optional.of(testPack));
        when(roamingPackRepository.findById(202L)).thenReturn(Optional.of(testPack));

        // When
        RecommendationResponse response = recommendationService.recommendTop3(testRequest);

        // Then
        assertNotNull(response);
        assertNotNull(response.top3());
        assertEquals(3, response.top3().size(), "Top 3 öneri döndürülmeli");
        
        // Maliyet sıralaması kontrol et (en ucuzdan en pahalıya)
        var recommendations = response.top3();
        
        // 1. sıra: En ucuz paket (29.90€)
        assertEquals(29.90, recommendations.get(0).totalCost(), 0.01, "1. sıra en ucuz olmalı");
        assertTrue(recommendations.get(0).label().contains("Pack#202"), "1. sıra pack 202 olmalı");
        
        // 2. sıra: İkinci paket (39.80€)
        assertEquals(39.80, recommendations.get(1).totalCost(), 0.01, "2. sıra ikinci paket olmalı");
        assertTrue(recommendations.get(1).label().contains("Pack#201"), "2. sıra pack 201 olmalı");
        
        // 3. sıra: PAYG (45.50€)
        assertEquals(45.50, recommendations.get(2).totalCost(), 0.01, "3. sıra PAYG olmalı");
        assertTrue(recommendations.get(2).label().contains("PAYG"), "3. sıra PAYG olmalı");
    }

    @Test
    void testRecommendationExplanations() {
        // Given
        when(simulationService.simulate(any(SimulationRequest.class))).thenReturn(testSimulationResponse);
        when(roamingPackRepository.findById(anyLong())).thenReturn(Optional.of(testPack));

        // When
        RecommendationResponse response = recommendationService.recommendTop3(testRequest);

        // Then
        assertNotNull(response);
        var recommendations = response.top3();
        
        // PAYG açıklaması kontrol et
        var paygRecommendation = recommendations.stream()
            .filter(rec -> rec.label().contains("PAYG"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(paygRecommendation, "PAYG önerisi bulunmalı");
        assertEquals("PAYG (ülke bazlı tekil ücret)", paygRecommendation.label());
        assertEquals("Paket almadan kullanıma göre ücretlendirme.", paygRecommendation.explanation());
        
        // Paket açıklamaları kontrol et
        var packRecommendations = recommendations.stream()
            .filter(rec -> rec.label().contains("Pack#"))
            .toList();
        
        assertEquals(2, packRecommendations.size(), "2 paket önerisi olmalı");
        
        // Validity uyarısı kontrol et
        packRecommendations.forEach(rec -> {
            assertTrue(rec.explanation().contains("Validity") || rec.explanation().contains("kapsama"), 
                "Paket açıklaması validity veya kapsama bilgisi içermeli");
        });
    }

    @Test
    void testRecommendationWithMissingPack() {
        // Given - Eksik paket senaryosu
        when(simulationService.simulate(any(SimulationRequest.class))).thenReturn(testSimulationResponse);
        when(roamingPackRepository.findById(201L)).thenReturn(Optional.empty()); // Paket bulunamadı
        when(roamingPackRepository.findById(202L)).thenReturn(Optional.of(testPack));

        // When
        RecommendationResponse response = recommendationService.recommendTop3(testRequest);

        // Then
        assertNotNull(response);
        var recommendations = response.top3();
        
        // Eksik paket için fallback label kontrol et
        var missingPackRecommendation = recommendations.stream()
            .filter(rec -> rec.label().contains("Pack#201"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(missingPackRecommendation, "Eksik paket önerisi bulunmalı");
        assertEquals("Pack#201 x2 (EUR)", missingPackRecommendation.label(), 
            "Paket bulunamadığında fallback label kullanılmalı");
    }

    @Test
    void testRecommendationRationale() {
        // Given
        when(simulationService.simulate(any(SimulationRequest.class))).thenReturn(testSimulationResponse);
        when(roamingPackRepository.findById(anyLong())).thenReturn(Optional.of(testPack));

        // When
        RecommendationResponse response = recommendationService.recommendTop3(testRequest);

        // Then
        assertNotNull(response.rationale(), "Öneri gerekçesi bulunmalı");
        assertTrue(response.rationale().contains("Toplam maliyete göre sıralandı"), 
            "Gerekçe maliyet sıralamasından bahsetmeli");
        assertTrue(response.rationale().contains("kapsama/validity tercih edildi"), 
            "Gerekçe kapsama ve validity'den bahsetmeli");
    }

    @Test
    void testRecommendationWithHighUsageProfile() {
        // Given - Yüksek kullanım profili
        SimulationRequest highUsageRequest = new SimulationRequest(
            1001L,
            Arrays.asList(
                new SimulationRequest.Leg("DE", java.time.LocalDate.of(2025, 8, 20), java.time.LocalDate.of(2025, 8, 25))
            ),
            new SimulationRequest.UsageProfileDto(2000, 60, 20) // Yüksek kullanım
        );

        // Yüksek kullanım için daha pahalı seçenekler
        var paygOption = new SimulationResponse.Option("payg", null, 0, 120.50, "EUR", true, true, null);
        var packOption = new SimulationResponse.Option("pack", 201L, 3, 95.70, "EUR", true, false, null);

        SimulationResponse highUsageResponse = new SimulationResponse(
            new SimulationResponse.Summary(6, new SimulationResponse.Needs(11.7, 360, 120)),
            Arrays.asList(packOption, paygOption), // Paket daha ucuz
            Arrays.asList("Yüksek kullanım için paket önerilir")
        );

        when(simulationService.simulate(highUsageRequest)).thenReturn(highUsageResponse);
        when(roamingPackRepository.findById(201L)).thenReturn(Optional.of(testPack));

        // When
        RecommendationResponse response = recommendationService.recommendTop3(highUsageRequest);

        // Then
        assertNotNull(response);
        var recommendations = response.top3();
        
        // Yüksek kullanımda paket 1. sırada olmalı
        assertEquals(95.70, recommendations.get(0).totalCost(), 0.01, "Yüksek kullanımda paket 1. sırada olmalı");
        assertTrue(recommendations.get(0).label().contains("Pack#201"), "1. sıra paket olmalı");
        
        // PAYG 2. sırada olmalı
        assertEquals(120.50, recommendations.get(1).totalCost(), 0.01, "PAYG 2. sırada olmalı");
        assertTrue(recommendations.get(1).label().contains("PAYG"), "2. sıra PAYG olmalı");
    }
}
