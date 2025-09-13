package com.bank.scheduler.infrastructure.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class MetricsCollector {

    private final Counter transfersCreatedCounter;
    private final Counter transfersProcessedCounter;
    private final Counter transfersFailedCounter;
    private final Timer transferProcessingTimer;
    private final Timer feeCalculationTimer;
    private final Counter securityViolationsCounter;

    public MetricsCollector(MeterRegistry meterRegistry) {
        this.transfersCreatedCounter = Counter.builder("transfers.created.total")
            .description("Total number of transfers created")
            .tag("service", "transfer-scheduler")
            .register(meterRegistry);

        this.transfersProcessedCounter = Counter.builder("transfers.processed.total")
            .description("Total number of transfers processed successfully")
            .tag("service", "transfer-scheduler")
            .register(meterRegistry);

        this.transfersFailedCounter = Counter.builder("transfers.failed.total")
            .description("Total number of failed transfers")
            .tag("service", "transfer-scheduler")
            .register(meterRegistry);

        this.transferProcessingTimer = Timer.builder("transfer.processing.duration")
            .description("Time taken to process a transfer")
            .tag("service", "transfer-scheduler")
            .register(meterRegistry);

        this.feeCalculationTimer = Timer.builder("fee.calculation.duration")
            .description("Time taken to calculate transfer fees")
            .tag("service", "transfer-scheduler")
            .register(meterRegistry);

        this.securityViolationsCounter = Counter.builder("security.violations.total")
            .description("Total number of security violations detected")
            .tag("service", "transfer-scheduler")
            .register(meterRegistry);
    }

    public void incrementTransfersCreated() {
        transfersCreatedCounter.increment();
    }

    public void incrementTransfersProcessed() {
        transfersProcessedCounter.increment();
    }

    public void incrementTransfersFailed(String reason) {
        transfersFailedCounter.increment(
            io.micrometer.core.instrument.Tags.of("reason", reason)
        );
    }

    public Timer.Sample startTransferProcessingTimer() {
        return Timer.start(transferProcessingTimer);
    }

    public Timer.Sample startFeeCalculationTimer() {
        return Timer.start(feeCalculationTimer);
    }

    public void incrementSecurityViolations(String violationType) {
        securityViolationsCounter.increment(
            io.micrometer.core.instrument.Tags.of("type", violationType)
        );
    }

    public void recordCustomMetric(String metricName, double value, String... tags) {
        // Custom metric recording for business-specific metrics
    }
}