package com.bank.scheduler.infrastructure.configuration;

import com.bank.scheduler.domain.policies.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * Fee Policy Configuration - Spring Bean Registration
 * 
 * Responsibilities:
 * - Register all fee policies as Spring beans
 * - Provide a complete set of policies for dependency injection
 * - Ensure all policies are available for TransferSchedulerService
 * 
 * Design benefits:
 * - Centralized policy registration
 * - Easy to add new policies without changing service code
 * - Spring manages policy lifecycle and dependencies
 */
@Configuration
public class FeePolicyConfiguration {

    @Bean
    public SameDayFeePolicy sameDayFeePolicy() {
        return new SameDayFeePolicy();
    }

    @Bean
    public TenDaysFeePolicy tenDaysFeePolicy() {
        return new TenDaysFeePolicy();
    }

    @Bean
    public ElevenToTwentyDaysFeePolicy elevenToTwentyDaysFeePolicy() {
        return new ElevenToTwentyDaysFeePolicy();
    }

    @Bean
    public TwentyOneToThirtyDaysFeePolicy twentyOneToThirtyDaysFeePolicy() {
        return new TwentyOneToThirtyDaysFeePolicy();
    }

    @Bean
    public ThirtyOneToFortyDaysFeePolicy thirtyOneToFortyDaysFeePolicy() {
        return new ThirtyOneToFortyDaysFeePolicy();
    }

    @Bean
    public FortyOneToFiftyDaysFeePolicy fortyOneToFiftyDaysFeePolicy() {
        return new FortyOneToFiftyDaysFeePolicy();
    }

    /**
     * Provides a complete set of all fee policies for dependency injection.
     * This Set will be automatically injected into TransferSchedulerService.
     */
    @Bean
    public Set<FeePolicy> feePolicies(
            SameDayFeePolicy sameDayFeePolicy,
            TenDaysFeePolicy tenDaysFeePolicy,
            ElevenToTwentyDaysFeePolicy elevenToTwentyDaysFeePolicy,
            TwentyOneToThirtyDaysFeePolicy twentyOneToThirtyDaysFeePolicy,
            ThirtyOneToFortyDaysFeePolicy thirtyOneToFortyDaysFeePolicy,
            FortyOneToFiftyDaysFeePolicy fortyOneToFiftyDaysFeePolicy) {
        
        return Set.of(
            sameDayFeePolicy,
            tenDaysFeePolicy,
            elevenToTwentyDaysFeePolicy,
            twentyOneToThirtyDaysFeePolicy,
            thirtyOneToFortyDaysFeePolicy,
            fortyOneToFiftyDaysFeePolicy
        );
    }
}