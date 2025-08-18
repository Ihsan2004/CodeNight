package com.example.demo.service;

import com.example.demo.dto.RecommendationResponse;
import com.example.demo.dto.SimulationRequest;
import com.example.demo.dto.SimulationResponse;
import com.example.demo.entity.RoamingPack;
import com.example.demo.repository.RoamingPackRepository;
import com.example.demo.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService{

    private final SimulationService simulationService;
    private final RoamingPackRepository packRepo;

    public RecommendationResponse recommendTop3(SimulationRequest req) {
        SimulationResponse sim = simulationService.simulate(req);
        var top = new LinkedList<RecommendationResponse.Item>();

        sim.options().stream().limit(3).forEach(opt -> {
            String label;
            String explanation;

            if ("payg".equals(opt.kind())) {
                label = "PAYG (ülke bazlı tekil ücret)";
                explanation = "Paket almadan kullanıma göre ücretlendirme.";
            } else {
                var pack = packRepo.findById(opt.packId()).orElse(null);
                String pName = (pack != null ? pack.getName() : ("Pack#" + opt.packId()));
                label = pName + " x" + opt.nPacks() + " (" + opt.currency() + ")";
                explanation = (opt.validityOk() ? "Validity uygun" : "Validity kısa, çoklu paket önerildi")
                        + (opt.coverageHit() ? ", kapsama uygun" : ", kapsama kısmi");
            }

            top.add(new RecommendationResponse.Item(
                    label,
                    opt.totalCost(),
                    explanation,
                    opt
            ));
        });

        return new RecommendationResponse(top, "Toplam maliyete göre sıralandı; eşitlikte kapsama/validity tercih edildi.");
    }
}
