package com.example.demo.dto;

import com.example.demo.entity.Country;
import com.example.demo.entity.RoamingPack;
import com.example.demo.entity.RoamingRate;
import lombok.Data;

import java.util.List;
@Data
public class CatalogResponse {

    private List<Country> countries;
    private List<RoamingRate> rates;
    private List<RoamingPack> packs;

    public CatalogResponse(List<Country> countries, List<RoamingRate> rates, List<RoamingPack> packs) {
        this.countries = countries;
        this.rates = rates;
        this.packs = packs;
    }
}
