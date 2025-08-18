package com.example.demo.dto;

import java.util.List;

public record SimulationResponse(
        Summary summary,
        List<Option> options,
        List<String> warnings
) {
    public record Summary(int days, Needs totalNeed) {}
    public record Needs(double gb, int min, int sms) {}
    public record Option(
            String kind, // "pack" | "payg"
            Long packId,
            int nPacks,
            double totalCost,
            String currency,
            boolean coverageHit,
            boolean validityOk,
            Overflow overflow // null olabilir
    ) {
        public record Overflow(double overMbCost, double overMinCost, double overSmsCost){}
    }
}
