package com.spx.inventory_service.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.MDC;

@Profile("dev")
@Slf4j
@Component
public class CacheStatsMonitor {

    // Autowired alternative
    private final CacheManager cacheManager;

    public CacheStatsMonitor(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    // Run logStats every 10 seconds
    @Scheduled(fixedRate = 10_000)
    public void logStats() {

        // Loop through the cache names
        for (String cacheName : cacheManager.getCacheNames()) {

            // Key-value couple for logging
            MDC.put("cacheName", cacheName);

            try {
                var cache = cacheManager.getCache(cacheName);
                if (cache instanceof CaffeineCache caffeineCache) {

                    // Get cache statistics
                    var stats = caffeineCache.getNativeCache().stats();

                    log.info(
                            "requests={} | hitRate={} | hits={} | misses={} | evictions={} | avgLoad={}ms",
                            stats.requestCount(), round(stats.hitRate()), stats.hitCount(), stats.missCount(),
                            stats.evictionCount(), nanosToMillis(stats.averageLoadPenalty())
                    );
                }
            } finally {
                MDC.remove("cacheName");
            }

        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private long nanosToMillis(double nanos) {
        return Math.round(nanos / 1_000_000);
    }
}

