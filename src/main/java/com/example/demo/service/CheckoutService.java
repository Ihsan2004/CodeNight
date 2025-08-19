
package com.example.demo.service;

import com.example.demo.entity.Country;
import com.example.demo.entity.RoamingPack;
import com.example.demo.entity.RoamingRate;
import com.example.demo.entity.User;
import com.example.demo.entity.UsageProfile;
import com.example.demo.repository.CountryRepository;
import com.example.demo.repository.RoamingPackRepository;
import com.example.demo.repository.RoamingRateRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UsageProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutService {

    private final CountryRepository countryRepository;
    private final RoamingPackRepository roamingPackRepository;
    private final RoamingRateRepository roamingRateRepository;
    private final UserRepository userRepository;
    private final UsageProfileRepository usageProfileRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @PostConstruct
    public void initializeData() {
        log.info("Initializing sample data from CSV files...");
        loadSampleData();
    }

    @Scheduled(fixedRate = 300000) // 5 minutes = 300,000 milliseconds
    public void refreshData() {
        log.info("Refreshing sample data from CSV files...");
        loadSampleData();
    }

    private void loadSampleData() {
        try {
            loadCountries();
            loadRoamingRates();
            loadRoamingPacks();
            loadUsers();
            loadUsageProfiles();
            log.info("Sample data loaded successfully");
        } catch (Exception e) {
            log.error("Error loading sample data: {}", e.getMessage(), e);
        }
    }

    private void loadCountries() {
        try (InputStream is = getClass().getResourceAsStream("/data/countries.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            // Skip header
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    Country country = new Country();
                    country.setCountryCode(parts[0].trim());
                    country.setCountryName(parts[1].trim());
                    country.setRegion(parts[2].trim());

                    // Check if country already exists
                    if (!countryRepository.existsById(country.getCountryCode())) {
                        countryRepository.save(country);
                        log.debug("Added country: {}", country.getCountryCode());
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error loading countries: {}", e.getMessage());
        }
    }

    private void loadRoamingRates() {
        try (InputStream is = getClass().getResourceAsStream("/data/roaming_rates.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            // Skip header
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    RoamingRate rate = new RoamingRate();
                    rate.setCountryCode(parts[0].trim());
                    rate.setDataPerMb(Double.valueOf(parts[1].trim()));
                    rate.setVoicePerMin(Double.valueOf(parts[2].trim()));
                    rate.setSmsPerMsg(Double.valueOf(parts[3].trim()));
                    rate.setCurrency(parts[4].trim());

                    // Check if rate already exists
                    if (!roamingRateRepository.existsById(rate.getCountryCode())) {
                        roamingRateRepository.save(rate);
                        log.debug("Added roaming rate for country: {}", rate.getCountryCode());
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error loading roaming rates: {}", e.getMessage());
        }
    }

    private void loadRoamingPacks() {
        try (InputStream is = getClass().getResourceAsStream("/data/roaming_packs.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            // Skip header
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 10) {
                    RoamingPack pack = new RoamingPack();
                    pack.setPackId(Long.valueOf(parts[0].trim()));
                    pack.setName(parts[1].trim());
                    pack.setCoverage(parts[2].trim());
                    pack.setCoverageType(parts[3].trim());
                    pack.setDataGb(Integer.valueOf(parts[4].trim()));
                    pack.setVoiceMin(Integer.valueOf(parts[5].trim()));
                    pack.setSms(Integer.valueOf(parts[6].trim()));
                    pack.setPrice(Double.valueOf(parts[7].trim()));
                    pack.setValidityDays(Integer.valueOf(parts[8].trim()));
                    pack.setCurrency(parts[9].trim());

                    // Check if pack already exists
                    if (!roamingPackRepository.existsById(pack.getPackId())) {
                        roamingPackRepository.save(pack);
                        log.debug("Added roaming pack: {}", pack.getName());
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error loading roaming packs: {}", e.getMessage());
        }
    }

    private void loadUsers() {
        try (InputStream is = getClass().getResourceAsStream("/data/users.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            // Skip header
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    User user = new User();
                    user.setUserId(Long.valueOf(parts[0].trim()));
                    user.setName(parts[1].trim());
                    user.setHomePlan(parts[2].trim());

                    // Check if user already exists
                    if (!userRepository.existsById(user.getUserId())) {
                        userRepository.save(user);
                        log.debug("Added user: {}", user.getName());
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error loading users: {}", e.getMessage());
        }
    }

    private void loadUsageProfiles() {
        try (InputStream is = getClass().getResourceAsStream("/data/usage_profile.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            // Skip header
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    UsageProfile profile = new UsageProfile();
                    profile.setUserId(Long.valueOf(parts[0].trim()));
                    profile.setAvgDailyMb(Integer.valueOf(parts[1].trim()));
                    profile.setAvgDailyMin(Integer.valueOf(parts[2].trim()));
                    profile.setAvgDailySms(Integer.valueOf(parts[3].trim()));

                    // Check if profile already exists
                    if (!usageProfileRepository.existsById(profile.getUserId())) {
                        usageProfileRepository.save(profile);
                        log.debug("Added usage profile for user: {}", profile.getUserId());
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error loading usage profiles: {}", e.getMessage());
        }
    }

    public String processCheckout(Long userId, String selectedOption, BigDecimal totalCost, String currency) {
        try {
            // Generate a mock order ID
            String orderId = "ORDER-" + System.currentTimeMillis();

            log.info("Checkout processed for user {}: {} - {} {}",
                    userId, selectedOption, totalCost, currency);

            return orderId;
        } catch (Exception e) {
            log.error("Error processing checkout: {}", e.getMessage(), e);
            throw new RuntimeException("Checkout processing failed", e);
        }
    }

    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    public List<RoamingPack> getAllRoamingPacks() {
        return roamingPackRepository.findAll();
    }

    public List<RoamingRate> getAllRoamingRates() {
        return roamingRateRepository.findAll();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<UsageProfile> getAllUsageProfiles() {
        return usageProfileRepository.findAll();
    }
}
