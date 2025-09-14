package com.bank.scheduler.infrastructure.cache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache Configuration - High Performance Data Access
 * 
 * Caching Strategy:
 * - Multi-tier caching for different data access patterns
 * - TTL optimization based on business requirements  
 * - Horizontal scaling support via Redis clustering
 * - Graceful degradation on cache failures
 * 
 * Performance Design:
 * - Account validation: High frequency, short TTL (5min)
 * - Fee calculations: Medium frequency, longer TTL (15min) 
 * - Exchange rates: Low frequency, extended TTL (1hour)
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

    /**
     * Redis Cache Manager with performance-optimized configuration
     * 
     * Features:
     * - Separate cache regions for different access patterns
     * - Reasonable defaults with business-specific overrides
     * - JSON serialization for cross-platform compatibility
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))  // Sensible default
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues(); // Prevent cache poisoning
        
        // Different cache strategies for different data types
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // High-frequency, short-lived: Account validation results
        cacheConfigurations.put("accountValidation", defaultConfig
            .entryTtl(Duration.ofMinutes(5)));
        
        // Medium-frequency, business-critical: Fee calculation results  
        cacheConfigurations.put("feeCalculations", defaultConfig
            .entryTtl(Duration.ofMinutes(15)));
        
        // Low-frequency, stable: Exchange rate data
        cacheConfigurations.put("exchangeRates", defaultConfig
            .entryTtl(Duration.ofHours(1)));
        
        // User perms cache - 10 minutes is plenty
        cacheConfigurations.put("userPermissions", defaultConfig
            .entryTtl(Duration.ofMinutes(10)));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
}