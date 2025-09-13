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

@Configuration
// @EnableCaching  // Temporarily disabled for testing
public class CacheConfiguration {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Cache for account validation - 5 minutes
        cacheConfigurations.put("accountValidation", defaultConfig
            .entryTtl(Duration.ofMinutes(5)));
        
        // Cache for fee calculations - 15 minutes
        cacheConfigurations.put("feeCalculations", defaultConfig
            .entryTtl(Duration.ofMinutes(15)));
        
        // Cache for exchange rates - 1 hour
        cacheConfigurations.put("exchangeRates", defaultConfig
            .entryTtl(Duration.ofHours(1)));
        
        // Cache for user permissions - 10 minutes
        cacheConfigurations.put("userPermissions", defaultConfig
            .entryTtl(Duration.ofMinutes(10)));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
}