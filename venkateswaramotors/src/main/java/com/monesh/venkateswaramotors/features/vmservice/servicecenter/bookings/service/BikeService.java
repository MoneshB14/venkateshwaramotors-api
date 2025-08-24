package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.service;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto.BikeListResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto.BikeResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto.BikeSuggestionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BikeService {

    private final List<BikeResponse> allBikes;

    public BikeService() {
        this.allBikes = initializeBikeData();
    }

    /**
     * Get bikes filtered by manufacturer
     */
    public BikeListResponse getBikesByManufacturer(String manufacturer) {
        try {
            log.info("Getting bikes for manufacturer: {}", manufacturer);

            if (manufacturer == null || manufacturer.trim().isEmpty()) {
                return BikeListResponse.builder()
                        .success(false)
                        .message("Manufacturer parameter is required")
                        .build();
            }

            List<BikeResponse> filteredBikes = allBikes.stream()
                    .filter(bike -> bike.getManufacturer().toLowerCase()
                            .contains(manufacturer.toLowerCase().trim()))
                    .collect(Collectors.toList());

            if (filteredBikes.isEmpty()) {
                return BikeListResponse.builder()
                        .success(true)
                        .message("No bikes found for manufacturer: " + manufacturer)
                        .manufacturer(manufacturer)
                        .bikes(new ArrayList<>())
                        .totalBikes(0)
                        .build();
            }

            return BikeListResponse.builder()
                    .success(true)
                    .message("Bikes retrieved successfully")
                    .manufacturer(manufacturer)
                    .bikes(filteredBikes)
                    .totalBikes(filteredBikes.size())
                    .build();

        } catch (Exception e) {
            log.error("Error getting bikes for manufacturer: {}, Error: {}", manufacturer, e.getMessage());
            return BikeListResponse.builder()
                    .success(false)
                    .message("An error occurred while retrieving bikes.")
                    .build();
        }
    }

    /**
     * Get all bikes
     */
    public BikeListResponse getAllBikes() {
        try {
            log.info("Getting all bikes");

            return BikeListResponse.builder()
                    .success(true)
                    .message("All bikes retrieved successfully")
                    .manufacturer("All")
                    .bikes(allBikes)
                    .totalBikes(allBikes.size())
                    .build();

        } catch (Exception e) {
            log.error("Error getting all bikes, Error: {}", e.getMessage());
            return BikeListResponse.builder()
                    .success(false)
                    .message("An error occurred while retrieving bikes.")
                    .build();
        }
    }

    /**
     * Get bike name suggestions based on partial input
     */
    public BikeSuggestionResponse getBikeSuggestions(String query) {
        try {
            log.info("Getting bike suggestions for query: {}", query);

            if (query == null || query.trim().isEmpty()) {
                return BikeSuggestionResponse.builder()
                        .success(false)
                        .message("Query parameter is required")
                        .query(query)
                        .suggestions(new ArrayList<>())
                        .totalSuggestions(0)
                        .build();
            }

            String lowerQuery = query.toLowerCase().trim();
            List<String> suggestions = new ArrayList<>();

            // Search in manufacturer names and bike models
            for (BikeResponse bike : allBikes) {
                String bikeFullName = bike.getManufacturer() + " " + bike.getModel();
                String manufacturerLower = bike.getManufacturer().toLowerCase();
                String modelLower = bike.getModel().toLowerCase();
                String fullNameLower = bikeFullName.toLowerCase();

                // Check if query matches manufacturer, model, or full name
                if (manufacturerLower.contains(lowerQuery) || 
                    modelLower.contains(lowerQuery) || 
                    fullNameLower.contains(lowerQuery)) {
                    
                    // Add the full bike name (manufacturer + model) to suggestions
                    if (!suggestions.contains(bikeFullName)) {
                        suggestions.add(bikeFullName);
                    }
                }
            }

            // Limit suggestions to 10 for better UX
            if (suggestions.size() > 10) {
                suggestions = suggestions.subList(0, 10);
            }

            String message = suggestions.isEmpty() ? 
                "No bike suggestions found for: " + query : 
                "Bike suggestions retrieved successfully";

            return BikeSuggestionResponse.builder()
                    .success(true)
                    .message(message)
                    .query(query)
                    .suggestions(suggestions)
                    .totalSuggestions(suggestions.size())
                    .build();

        } catch (Exception e) {
            log.error("Error getting bike suggestions for query: {}, Error: {}", query, e.getMessage());
            return BikeSuggestionResponse.builder()
                    .success(false)
                    .message("An error occurred while retrieving bike suggestions.")
                    .query(query)
                    .suggestions(new ArrayList<>())
                    .totalSuggestions(0)
                    .build();
        }
    }

    /**
     * Initialize bike data with popular Indian motorcycle brands and models
     */
    private List<BikeResponse> initializeBikeData() {
        List<BikeResponse> bikes = new ArrayList<>();

        // Honda bikes
        bikes.add(BikeResponse.builder()
                .manufacturer("Honda")
                .model("CB Shine")
                .category("Commuter")
                .engineCapacity("125cc")
                .fuelType("Petrol")
                .description("Popular commuter motorcycle with excellent fuel efficiency")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Honda")
                .model("Activa 6G")
                .category("Scooter")
                .engineCapacity("110cc")
                .fuelType("Petrol")
                .description("India's best-selling scooter with reliable performance")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Honda")
                .model("CB Hornet 2.0")
                .category("Street")
                .engineCapacity("184cc")
                .fuelType("Petrol")
                .description("Sporty street motorcycle with aggressive styling")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Honda")
                .model("Unicorn")
                .category("Commuter")
                .engineCapacity("150cc")
                .fuelType("Petrol")
                .description("Premium commuter bike with refined engine")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Honda")
                .model("Dio")
                .category("Scooter")
                .engineCapacity("110cc")
                .fuelType("Petrol")
                .description("Stylish scooter popular among youth")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Honda")
                .model("X-Blade")
                .category("Street")
                .engineCapacity("160cc")
                .fuelType("Petrol")
                .description("Sharp and edgy design motorcycle")
                .build());

        // TVS bikes
        bikes.add(BikeResponse.builder()
                .manufacturer("TVS")
                .model("Jupiter")
                .category("Scooter")
                .engineCapacity("110cc")
                .fuelType("Petrol")
                .description("Family scooter with excellent mileage")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("TVS")
                .model("Apache RTR 160")
                .category("Sport")
                .engineCapacity("160cc")
                .fuelType("Petrol")
                .description("Performance-oriented sports motorcycle")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("TVS")
                .model("Apache RTR 200")
                .category("Sport")
                .engineCapacity("197cc")
                .fuelType("Petrol")
                .description("High-performance sports bike with racing DNA")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("TVS")
                .model("Star City Plus")
                .category("Commuter")
                .engineCapacity("110cc")
                .fuelType("Petrol")
                .description("Affordable commuter motorcycle")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("TVS")
                .model("Ntorq 125")
                .category("Scooter")
                .engineCapacity("125cc")
                .fuelType("Petrol")
                .description("Feature-rich performance scooter")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("TVS")
                .model("Radeon")
                .category("Commuter")
                .engineCapacity("110cc")
                .fuelType("Petrol")
                .description("Comfortable commuter with retro styling")
                .build());

        // Bajaj bikes
        bikes.add(BikeResponse.builder()
                .manufacturer("Bajaj")
                .model("Pulsar 150")
                .category("Sport")
                .engineCapacity("149cc")
                .fuelType("Petrol")
                .description("Iconic sports motorcycle with proven performance")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Bajaj")
                .model("Pulsar NS200")
                .category("Sport")
                .engineCapacity("199cc")
                .fuelType("Petrol")
                .description("Naked sports bike with aggressive performance")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Bajaj")
                .model("Avenger Cruise 220")
                .category("Cruiser")
                .engineCapacity("220cc")
                .fuelType("Petrol")
                .description("Comfortable cruiser for long rides")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Bajaj")
                .model("CT 110")
                .category("Commuter")
                .engineCapacity("115cc")
                .fuelType("Petrol")
                .description("Rugged commuter motorcycle")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Bajaj")
                .model("Chetak")
                .category("Electric Scooter")
                .engineCapacity("Electric")
                .fuelType("Electric")
                .description("Premium electric scooter with retro design")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Bajaj")
                .model("Dominar 400")
                .category("Adventure")
                .engineCapacity("373cc")
                .fuelType("Petrol")
                .description("Adventure touring motorcycle")
                .build());

        // Hero bikes
        bikes.add(BikeResponse.builder()
                .manufacturer("Hero")
                .model("Splendor Plus")
                .category("Commuter")
                .engineCapacity("97cc")
                .fuelType("Petrol")
                .description("India's largest-selling motorcycle")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Hero")
                .model("HF Deluxe")
                .category("Commuter")
                .engineCapacity("97cc")
                .fuelType("Petrol")
                .description("Economical and reliable commuter bike")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Hero")
                .model("Xtreme 160R")
                .category("Sport")
                .engineCapacity("163cc")
                .fuelType("Petrol")
                .description("Premium sports motorcycle with modern features")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Hero")
                .model("Passion Pro")
                .category("Commuter")
                .engineCapacity("113cc")
                .fuelType("Petrol")
                .description("Stylish commuter with excellent fuel economy")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Hero")
                .model("Pleasure Plus")
                .category("Scooter")
                .engineCapacity("110cc")
                .fuelType("Petrol")
                .description("Lightweight scooter designed for women")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Hero")
                .model("Maestro Edge 125")
                .category("Scooter")
                .engineCapacity("125cc")
                .fuelType("Petrol")
                .description("Premium scooter with advanced features")
                .build());

        // Yamaha bikes
        bikes.add(BikeResponse.builder()
                .manufacturer("Yamaha")
                .model("FZ-S FI")
                .category("Street")
                .engineCapacity("149cc")
                .fuelType("Petrol")
                .description("Stylish street motorcycle with fuel injection")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Yamaha")
                .model("MT-15")
                .category("Street")
                .engineCapacity("155cc")
                .fuelType("Petrol")
                .description("Naked sports bike with R15 DNA")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Yamaha")
                .model("R15 V4")
                .category("Sport")
                .engineCapacity("155cc")
                .fuelType("Petrol")
                .description("Fully-faired sports bike with track-ready performance")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Yamaha")
                .model("Fascino 125")
                .category("Scooter")
                .engineCapacity("125cc")
                .fuelType("Petrol")
                .description("Retro-styled premium scooter")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Yamaha")
                .model("Ray ZR 125")
                .category("Scooter")
                .engineCapacity("125cc")
                .fuelType("Petrol")
                .description("Sporty scooter with street fighter looks")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Yamaha")
                .model("FZ X")
                .category("Street")
                .engineCapacity("149cc")
                .fuelType("Petrol")
                .description("Neo-retro styled street motorcycle")
                .build());

        // Royal Enfield bikes
        bikes.add(BikeResponse.builder()
                .manufacturer("Royal Enfield")
                .model("Classic 350")
                .category("Cruiser")
                .engineCapacity("349cc")
                .fuelType("Petrol")
                .description("Vintage-styled cruiser motorcycle")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Royal Enfield")
                .model("Bullet 350")
                .category("Cruiser")
                .engineCapacity("346cc")
                .fuelType("Petrol")
                .description("Iconic motorcycle with timeless design")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Royal Enfield")
                .model("Himalayan")
                .category("Adventure")
                .engineCapacity("411cc")
                .fuelType("Petrol")
                .description("Purpose-built adventure touring motorcycle")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Royal Enfield")
                .model("Interceptor 650")
                .category("Cruiser")
                .engineCapacity("648cc")
                .fuelType("Petrol")
                .description("Modern classic with parallel-twin engine")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("Royal Enfield")
                .model("Continental GT 650")
                .category("Cafe Racer")
                .engineCapacity("648cc")
                .fuelType("Petrol")
                .description("Cafe racer with vintage racing spirit")
                .build());

        // KTM bikes
        bikes.add(BikeResponse.builder()
                .manufacturer("KTM")
                .model("Duke 200")
                .category("Street")
                .engineCapacity("199cc")
                .fuelType("Petrol")
                .description("Ready to race naked bike")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("KTM")
                .model("Duke 250")
                .category("Street")
                .engineCapacity("248cc")
                .fuelType("Petrol")
                .description("More powerful naked bike with aggressive styling")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("KTM")
                .model("RC 200")
                .category("Sport")
                .engineCapacity("199cc")
                .fuelType("Petrol")
                .description("Track-focused sports bike")
                .build());

        bikes.add(BikeResponse.builder()
                .manufacturer("KTM")
                .model("Adventure 250")
                .category("Adventure")
                .engineCapacity("248cc")
                .fuelType("Petrol")
                .description("Adventure bike for touring and off-road")
                .build());

        return bikes;
    }
}
