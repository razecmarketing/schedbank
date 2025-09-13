package com.bank.scheduler.infrastructure.configuration;

import com.bank.scheduler.domain.policies.ElevenToTwentyDaysFeePolicy;
import com.bank.scheduler.domain.policies.FeePolicy;
import com.bank.scheduler.domain.policies.FortyOneToFiftyDaysFeePolicy;
import com.bank.scheduler.domain.policies.SameDayFeePolicy;
import com.bank.scheduler.domain.policies.TenDaysFeePolicy;
import com.bank.scheduler.domain.policies.ThirtyOneToFortyDaysFeePolicy;
import com.bank.scheduler.domain.policies.TwentyOneToThirtyDaysFeePolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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

    @Bean
    public List<FeePolicy> feePolicies(
            SameDayFeePolicy sameDayFeePolicy,
            TenDaysFeePolicy tenDaysFeePolicy,
            ElevenToTwentyDaysFeePolicy elevenToTwentyDaysFeePolicy,
            TwentyOneToThirtyDaysFeePolicy twentyOneToThirtyDaysFeePolicy,
            ThirtyOneToFortyDaysFeePolicy thirtyOneToFortyDaysFeePolicy,
            FortyOneToFiftyDaysFeePolicy fortyOneToFiftyDaysFeePolicy) {
        
        return List.of(
                sameDayFeePolicy,
                tenDaysFeePolicy,
                elevenToTwentyDaysFeePolicy,
                twentyOneToThirtyDaysFeePolicy,
                thirtyOneToFortyDaysFeePolicy,
                fortyOneToFiftyDaysFeePolicy
        );
    }
}