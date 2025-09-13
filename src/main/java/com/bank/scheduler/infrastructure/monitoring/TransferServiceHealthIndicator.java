package com.bank.scheduler.infrastructure.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("transferServiceHealth")
public class TransferServiceHealthIndicator implements HealthIndicator {

    private final MeterRegistry meterRegistry;
    private final Timer healthCheckTimer;

    public TransferServiceHealthIndicator(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.healthCheckTimer = Timer.builder("transfer.service.health.check")
            .description("Transfer service health check execution time")
            .register(meterRegistry);
    }

    @Override
    public Health health() {
        return healthCheckTimer.recordCallable(() -> {
            try {
                // Check database connectivity
                boolean databaseHealthy = checkDatabaseConnectivity();
                
                // Check external services
                boolean externalServicesHealthy = checkExternalServices();
                
                // Check system resources
                boolean resourcesHealthy = checkSystemResources();
                
                if (databaseHealthy && externalServicesHealthy && resourcesHealthy) {
                    return Health.up()
                        .withDetail("database", "Connected")
                        .withDetail("externalServices", "Available")
                        .withDetail("systemResources", "Optimal")
                        .withDetail("version", "1.0.0")
                        .build();
                } else {
                    return Health.down()
                        .withDetail("database", databaseHealthy ? "Connected" : "Disconnected")
                        .withDetail("externalServices", externalServicesHealthy ? "Available" : "Unavailable")
                        .withDetail("systemResources", resourcesHealthy ? "Optimal" : "Limited")
                        .build();
                }
            } catch (Exception e) {
                return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
            }
        });
    }

    private boolean checkDatabaseConnectivity() {
        // Implement database health check
        return true;
    }

    private boolean checkExternalServices() {
        // Implement external services health check
        return true;
    }

    private boolean checkSystemResources() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        
        double memoryUsage = (double) (totalMemory - freeMemory) / maxMemory;
        
        // Alert if memory usage > 85%
        return memoryUsage < 0.85;
    }
}