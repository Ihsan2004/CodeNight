package com.example.demo.service;


import com.example.demo.dto.SimulationRequest;
import com.example.demo.dto.SimulationResponse;
import com.example.demo.entity.Country;
import com.example.demo.entity.RoamingPack;
import com.example.demo.entity.RoamingRate;
import com.example.demo.repository.CountryRepository;
import com.example.demo.repository.RoamingPackRepository;
import com.example.demo.repository.RoamingRateRepository;
import com.example.demo.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.ceil;
import static java.lang.Math.max;

@Service
@RequiredArgsConstructor
public class SimulationService{

    private final TripService tripService;
    private final CountryRepository countryRepo;
    private final RoamingRateRepository rateRepo;
    private final RoamingPackRepository packRepo;

    public SimulationResponse simulate(SimulationRequest req) {
        // 1) Gün ve ülke dağılımı
        int totalDays = req.trips().stream().mapToInt(tripService::countDaysInclusive).sum();

        // Ülke → gün adedi haritası
        Map<String,Integer> countryDayMap = new HashMap<>();
        req.trips().forEach(l -> {
            int d = tripService.countDaysInclusive(l);
            countryDayMap.merge(l.countryCode(), d, Integer::sum);
        });

        // 2) Toplam ihtiyaç
        double needGb  = (req.profile().avgDailyMb() * totalDays) / 1024.0;
        int needMin    = req.profile().avgDailyMin() * totalDays;
        int needSms    = req.profile().avgDailySms() * totalDays;

        var needs = new SimulationResponse.Needs(needGb, needMin, needSms);

        // meta: countries, rates, packs
        Map<String, Country> countries = countryRepo.findAll().stream()
                .collect(Collectors.toMap(Country::getCountryCode, c -> c));
        Map<String, RoamingRate> rates = rateRepo.findAll().stream()
                .collect(Collectors.toMap(RoamingRate::getCountryCode, r -> r));
        List<RoamingPack> packs = packRepo.findAll();

        List<String> warnings = new ArrayList<>();

        // 3) PAYG maliyeti (ülke ağırlıklarına göre)
        double paygCost = 0.0; String paygCurr = null;
        for (var e: countryDayMap.entrySet()) {
            String cc = e.getKey(); int days = e.getValue();
            double share = days / (double) totalDays;
            RoamingRate rr = rates.get(cc);
            if (rr == null) continue;
            paygCurr = rr.getCurrency(); // basit varsayım: tek para birimi
            paygCost += (needGb*1024*rr.getDataPerMb()*share)  // GB→MB
                    + (needMin*rr.getVoicePerMin()*share)
                    + (needSms*rr.getSmsPerMsg()*share);
        }
        var paygOption = new SimulationResponse.Option("payg", null, 0, round2(paygCost), paygCurr, true, true, null);

        // 4) Paket seçenekleri
        List<SimulationResponse.Option> packOptions = new ArrayList<>();
        for (RoamingPack p : packs) {
            // kapsama kontrolü (çok ülke varsa en az biri kapsam dışıysa uyarı, maliyeti oransal hesaplarız)
            boolean anyCovered = false;
            int coveredDays = 0;
            for (var e: countryDayMap.entrySet()) {
                var cc = e.getKey();
                var c = countries.get(cc);
                boolean covers = switch (p.getCoverageType()) {
                    case "region" -> c != null && p.getCoverage().equalsIgnoreCase(c.getRegion());
                    case "country" -> p.getCoverage().equalsIgnoreCase(cc);
                    default -> false;
                };
                if (covers) { anyCovered = true; coveredDays += e.getValue(); }
            }
            if (!anyCovered) continue;

            boolean validityOk = totalDays <= p.getValidityDays();
            int nPacks = (int) ceil(totalDays / (double) p.getValidityDays());

            // base fiyat
            double base = p.getPrice() * nPacks;

            // aşım (kapsanan gün oranına göre veri/ses/sms katkısı)
            double coveredShare = coveredDays / (double) totalDays;
            // basitleştirme: aşımı PAYG oranlarıyla hesapla (case kuralı)
            // çok ülke → ortalama payg oranı (gün-gewicht)
            double avgDataPerMb = 0, avgVoicePerMin = 0, avgSmsPerMsg = 0;
            for (var e: countryDayMap.entrySet()) {
                double s = e.getValue() / (double) totalDays;
                var rr = rates.get(e.getKey());
                if (rr == null) continue;
                avgDataPerMb  += rr.getDataPerMb()*s;
                avgVoicePerMin+= rr.getVoicePerMin()*s;
                avgSmsPerMsg  += rr.getSmsPerMsg()*s;
            }

            double overGb  = max(0.0, needGb  - nPacks * p.getDataGb());
            double overMin = max(0.0, needMin - nPacks * p.getVoiceMin());
            double overSms = max(0.0, needSms - nPacks * p.getSms());

            // kapsanmayan günlerin (coveredShare dışı) payg maliyeti → ihtiyaç da aynı oranda
            double uncoveredShare = 1.0 - coveredShare;
            double uncoveredCost = (needGb*1024*avgDataPerMb*uncoveredShare)
                    + (needMin*avgVoicePerMin*uncoveredShare)
                    + (needSms*avgSmsPerMsg*uncoveredShare);

            double overCost = (overGb*1024*avgDataPerMb) + (overMin*avgVoicePerMin) + (overSms*avgSmsPerMsg);
            double total = base + overCost + uncoveredCost;

            if (!validityOk) warnings.add("Paketin geçerliliği trip süresinden kısa: " + p.getName());
            if (uncoveredShare > 0) warnings.add("Kapsama dışında gün(ler) var: " + p.getName());

            packOptions.add(new SimulationResponse.Option(
                    "pack",
                    p.getPackId(),
                    nPacks,
                    round2(total),
                    p.getCurrency(),
                    true,
                    validityOk,
                    new SimulationResponse.Option.Overflow(round2(overGb*1024*avgDataPerMb), round2(overMin*avgVoicePerMin), round2(overSms*avgSmsPerMsg))
            ));
        }

        // 5) Sonuçlar: PAYG + paketler (fiyata göre sırala)
        List<SimulationResponse.Option> all = new ArrayList<>(packOptions);
        all.add(paygOption);
        all.sort(Comparator.comparingDouble(SimulationResponse.Option::totalCost));

        var summary = new SimulationResponse.Summary(totalDays, needs);
        return new SimulationResponse(summary, all, warnings);
    }

    private static double round2(double v){ return Math.round(v*100.0)/100.0; }
}

