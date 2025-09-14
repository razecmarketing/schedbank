package com.bank.scheduler.infrastructure.performance;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Performance Metrics Collector following Jeff Dean's scalability principles.
 * 
 * Metrics Strategy:
 * - Latency percentiles (p50, p95, p99)
 * - Throughput counters
 * - System health indicators
 * - Business KPI tracking
 * 
 * Designed for horizontal scaling and production observability.
 */
@Component
public class PerformanceMetrics {

    private final MeterRegistry meterRegistry;
    private final Counter transfersScheduled;
    private final Counter transfersFailed;
    private final Counter policyApplications;
    private final AtomicLong totalTransferAmount;
    private final LongAdder totalFeeAmount;
    
    public PerformanceMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Business metrics
        this.transfersScheduled = Counter.builder("transfers.scheduled")
            .description("Total number of transfers scheduled")
            .register(meterRegistry);
            
        this.transfersFailed = Counter.builder("transfers.failed")
            .description("Total number of failed transfer attempts")
            .register(meterRegistry);
            
        this.policyApplications = Counter.builder("policies.applied")
            .description("Number of fee policy applications")
            .register(meterRegistry);
            
        // Financial metrics
        this.totalTransferAmount = new AtomicLong(0);
        this.totalFeeAmount = new LongAdder();
    }
    
    /**
     * Records successful transfer scheduling with timing.
     * Used for latency and throughput analysis.
     */
    @Timed(value = "transfers.schedule.time", description = "Time to schedule transfer")
    public void recordTransferScheduled(BigDecimal amount, BigDecimal fee) {
        transfersScheduled.increment();
        totalTransferAmount.addAndGet(amount.longValue());
        totalFeeAmount.add(fee.longValue());
    }
    
    /**
     * Records transfer scheduling failure for error rate monitoring.
     */
    public void recordTransferFailed(String errorType) {
        transfersFailed.increment();
    }
    
    /**
     * Records fee policy application for business analytics.
     */
    public void recordPolicyApplication(String policyType, int dayRange) {
        policyApplications.increment();
    }
    
    /**
     * Creates timer for custom performance measurement.
     * Useful for profiling specific operations.
     */
    public Timer.Sample startTimer(String name) {
        return Timer.start(meterRegistry);
    }
    
    /**
     * Records custom latency measurement.
     */
    public void recordLatency(String operation, long milliseconds) {
        Timer.builder("operation.latency")
            .description("Custom operation latency")
            .tag("operation", operation)
            .register(meterRegistry)
            .record(java.time.Duration.ofMillis(milliseconds));
    }
    
    /**
     * Performance health indicators for system monitoring.
     */
    public HealthMetrics getHealthMetrics() {
        return new HealthMetrics(
            transfersScheduled.count(),
            transfersFailed.count(),
            getErrorRate(),
            getThroughputPerSecond()
        );
    }
    
    private double getErrorRate() {
        double total = transfersScheduled.count() + transfersFailed.count();
        return total > 0 ? transfersFailed.count() / total : 0.0;
    }
    
    private double getThroughputPerSecond() {
        // Simplified throughput calculation
        // In production, would use time-windowed counters
        return transfersScheduled.count() / (System.currentTimeMillis() / 1000.0);
    }
    
    /**
     * Immutable health metrics snapshot for monitoring dashboards.
     */
    public static class HealthMetrics {
        private final double successCount;
        private final double failureCount;
        private final double errorRate;
        private final double throughput;
        
        public HealthMetrics(double successCount, double failureCount, 
                           double errorRate, double throughput) {
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.errorRate = errorRate;
            this.throughput = throughput;
        }
        
        // Getters
        public double getSuccessCount() { return successCount; }
        public double getFailureCount() { return failureCount; }
        public double getErrorRate() { return errorRate; }
        public double getThroughput() { return throughput; }
        
        public boolean isHealthy() {
            return errorRate < 0.01; // Less than 1% error rate
        }
    }
}