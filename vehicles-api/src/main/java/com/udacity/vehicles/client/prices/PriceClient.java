package com.udacity.vehicles.client.prices;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

/**
 * Implements a class to interface with the Pricing Client for price data.
 */
@Component
public class PriceClient {
    private static final int DEFAULT_MAX_ENTRIES = 20;

    private static final Logger log = LoggerFactory.getLogger(PriceClient.class);
    private final WebClient client;
    private final Map<Long, String> priceCache = Collections.synchronizedMap(new PriceCache(DEFAULT_MAX_ENTRIES));

    /**
     * small cache implementation to hold the last 20 requested vehicle prices
     */
    private static class PriceCache extends LinkedHashMap<Long, String> {
        private final int maxEntries;

        public PriceCache(int maxEntries) {
            super(maxEntries);
            this.maxEntries = maxEntries;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return this.size() > this.maxEntries;
        }
    }

    public PriceClient(@Qualifier("pricing") WebClient pricing) {
        this.client = pricing;
    }

    // In a real-world application we'll want to add some resilience
    // to this method with retries/CB/failover capabilities
    // We may also want to cache the results so we don't need to
    // do a request every time

    /**
     * Gets a vehicle price from the pricing client, given vehicle ID.
     *
     * @param vehicleId ID number of the vehicle for which to get the price
     * @return Currency and price of the requested vehicle,
     * error message that the vehicle ID is invalid, or note that the
     * service is down.
     */
    public String getPrice(Long vehicleId) {
        if (this.priceCache.containsKey(vehicleId)) {
            return this.priceCache.get(vehicleId);
        }

        try {
            Price price = client
                    .get()
                    .uri(uriBuilder -> uriBuilder.path("services/price/" + vehicleId).build())
                    .retrieve().bodyToMono(Price.class).block();

            String formattedPrice = String.format("%s %s", price.getCurrency(), price.getPrice());

            this.priceCache.putIfAbsent(vehicleId, formattedPrice);

            return formattedPrice;
        } catch (Exception e) {
            log.error("Unexpected error retrieving price for vehicle {}", vehicleId, e);
        }

        return "(consult price)";
    }
}
