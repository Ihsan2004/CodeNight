package com.example.demo.service;

import com.example.demo.dto.CatalogResponse;
import com.example.demo.entity.Country;
import com.example.demo.entity.RoamingPack;
import com.example.demo.entity.RoamingRate;
import com.example.demo.repository.CountryRepository;
import com.example.demo.repository.RoamingPackRepository;
import com.example.demo.repository.RoamingRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CountryRepository countryRepo;
    private final RoamingRateRepository rateRepo;
    private final RoamingPackRepository packRepo;

    private boolean cached = true;
    private CatalogResponse cache;

    public CatalogService withCache(boolean enabled) {
        this.cached = enabled;
        return this;
    }

    public CatalogResponse getCatalog() {
        if (cached && cache != null)
            return cache;
        List<Country> countries = countryRepo.findAll();
        List<RoamingRate> rates = rateRepo.findAll();
        List<RoamingPack> packs = packRepo.findAll();
        cache = new CatalogResponse(countries, rates, packs);
        return cache;
    }
}
