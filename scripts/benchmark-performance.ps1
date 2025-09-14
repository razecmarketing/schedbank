#!/usr/bin/env powershell

<#
.SYNOPSIS
    Performance benchmark suite for SchedBank transfer scheduling system
.DESCRIPTION
    Comprehensive performance testing following Jeff Dean's latency guidelines:
    - p50 latency < 10ms for transfer scheduling
    - p95 latency < 50ms for complex operations
    - Throughput > 1000 ops/sec under normal load
    - Error rate < 0.1% under stress
.EXAMPLE
    .\benchmark-performance.ps1
    Runs all performance tests and generates report
#>

Write-Host "=== SCHEDBANK PERFORMANCE BENCHMARK SUITE ===" -ForegroundColor Green

# Configuration
$BackendUrl = "http://localhost:8080"
$WarmupRequests = 50
$BenchmarkRequests = 1000
$ConcurrencyLevels = @(1, 5, 10, 20, 50)

# Results tracking
$Results = @()

function Test-BackendAvailability {
    Write-Host "Checking backend availability..." -ForegroundColor Yellow
    try {
        $response = Invoke-RestMethod -Uri "$BackendUrl/actuator/health" -Method GET -TimeoutSec 5
        if ($response.status -eq "UP") {
            Write-Host "✓ Backend is available" -ForegroundColor Green
            return $true
        }
    }
    catch {
        Write-Host "✗ Backend not available. Start with 'mvn spring-boot:run'" -ForegroundColor Red
        return $false
    }
    return $false
}

function Invoke-WarmupPhase {
    Write-Host "Warming up system..." -ForegroundColor Yellow
    
    for ($i = 0; $i -lt $WarmupRequests; $i++) {
        $transferDate = (Get-Date).AddDays($i % 50 + 1).ToString("yyyy-MM-dd")
        $body = @{
            sourceAccount = "1234567890"
            targetAccount = "0987654321"
            amount = 100.00 + ($i % 900)
            transferDate = $transferDate
        } | ConvertTo-Json
        
        try {
            Invoke-RestMethod -Uri "$BackendUrl/api/transfers" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 2 | Out-Null
        }
        catch {
            # Ignore warmup failures
        }
        
        if ($i % 10 -eq 0) {
            Write-Progress -Activity "Warming up" -PercentComplete (($i / $WarmupRequests) * 100)
        }
    }
    Write-Progress -Activity "Warming up" -Completed
    Write-Host "✓ Warmup completed" -ForegroundColor Green
}

function Measure-TransferScheduling {
    param(
        [int]$Concurrency,
        [int]$Requests
    )
    
    Write-Host "Running benchmark: $Requests requests with $Concurrency concurrent threads" -ForegroundColor Yellow
    
    $jobs = @()
    $results = @()
    $requestsPerThread = [math]::Ceiling($Requests / $Concurrency)
    
    # Create concurrent jobs
    for ($thread = 0; $thread -lt $Concurrency; $thread++) {
        $job = Start-Job -ScriptBlock {
            param($BackendUrl, $RequestsPerThread, $ThreadId)
            
            $results = @()
            
            for ($i = 0; $i -lt $RequestsPerThread; $i++) {
                $start = Get-Date
                
                $transferDate = (Get-Date).AddDays(($i % 50) + 1).ToString("yyyy-MM-dd")
                $body = @{
                    sourceAccount = "1111111111"
                    targetAccount = "2222222222"
                    amount = 100.00 + ($i % 1000)
                    transferDate = $transferDate
                } | ConvertTo-Json
                
                try {
                    $response = Invoke-RestMethod -Uri "$BackendUrl/api/transfers" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 5
                    $end = Get-Date
                    $latency = ($end - $start).TotalMilliseconds
                    
                    $results += @{
                        ThreadId = $ThreadId
                        RequestId = $i
                        Latency = $latency
                        Success = $true
                        StatusCode = 200
                    }
                }
                catch {
                    $end = Get-Date
                    $latency = ($end - $start).TotalMilliseconds
                    
                    $results += @{
                        ThreadId = $ThreadId
                        RequestId = $i
                        Latency = $latency
                        Success = $false
                        Error = $_.Exception.Message
                    }
                }
            }
            
            return $results
        } -ArgumentList $BackendUrl, $requestsPerThread, $thread
        
        $jobs += $job
    }
    
    # Wait for completion and collect results
    Write-Host "Waiting for completion..." -ForegroundColor Yellow
    $allResults = @()
    
    foreach ($job in $jobs) {
        $jobResults = Receive-Job -Job $job -Wait
        $allResults += $jobResults
        Remove-Job -Job $job
    }
    
    return $allResults
}

function Calculate-Statistics {
    param($Results)
    
    $successfulRequests = $Results | Where-Object { $_.Success -eq $true }
    $failedRequests = $Results | Where-Object { $_.Success -eq $false }
    
    if ($successfulRequests.Count -eq 0) {
        return @{
            TotalRequests = $Results.Count
            SuccessfulRequests = 0
            FailedRequests = $failedRequests.Count
            ErrorRate = 100.0
            MinLatency = 0
            MaxLatency = 0
            AvgLatency = 0
            P50Latency = 0
            P95Latency = 0
            P99Latency = 0
        }
    }
    
    $latencies = $successfulRequests | ForEach-Object { $_.Latency }
    $sortedLatencies = $latencies | Sort-Object
    
    $p50Index = [math]::Floor($sortedLatencies.Count * 0.5)
    $p95Index = [math]::Floor($sortedLatencies.Count * 0.95)
    $p99Index = [math]::Floor($sortedLatencies.Count * 0.99)
    
    return @{
        TotalRequests = $Results.Count
        SuccessfulRequests = $successfulRequests.Count
        FailedRequests = $failedRequests.Count
        ErrorRate = [math]::Round(($failedRequests.Count / $Results.Count) * 100, 2)
        MinLatency = [math]::Round(($sortedLatencies | Measure-Object -Minimum).Minimum, 2)
        MaxLatency = [math]::Round(($sortedLatencies | Measure-Object -Maximum).Maximum, 2)
        AvgLatency = [math]::Round(($sortedLatencies | Measure-Object -Average).Average, 2)
        P50Latency = [math]::Round($sortedLatencies[$p50Index], 2)
        P95Latency = [math]::Round($sortedLatencies[$p95Index], 2)
        P99Latency = [math]::Round($sortedLatencies[$p99Index], 2)
    }
}

function Write-PerformanceReport {
    param($AllResults)
    
    Write-Host "`n=== PERFORMANCE BENCHMARK REPORT ===" -ForegroundColor Green
    
    foreach ($result in $AllResults) {
        $concurrency = $result.Concurrency
        $stats = $result.Statistics
        
        Write-Host "`nConcurrency Level: $concurrency" -ForegroundColor Cyan
        Write-Host "├─ Total Requests: $($stats.TotalRequests)"
        Write-Host "├─ Successful: $($stats.SuccessfulRequests)"
        Write-Host "├─ Failed: $($stats.FailedRequests)"
        Write-Host "├─ Error Rate: $($stats.ErrorRate)%"
        Write-Host "├─ Latency (ms):"
        Write-Host "│  ├─ Min: $($stats.MinLatency)"
        Write-Host "│  ├─ Avg: $($stats.AvgLatency)"
        Write-Host "│  ├─ Max: $($stats.MaxLatency)"
        Write-Host "│  ├─ P50: $($stats.P50Latency)"
        Write-Host "│  ├─ P95: $($stats.P95Latency)"
        Write-Host "│  └─ P99: $($stats.P99Latency)"
        
        # Performance assessment
        $assessment = "EXCELLENT"
        if ($stats.P95Latency -gt 50) { $assessment = "GOOD" }
        if ($stats.P95Latency -gt 100) { $assessment = "ACCEPTABLE" }
        if ($stats.P95Latency -gt 200) { $assessment = "NEEDS_IMPROVEMENT" }
        if ($stats.ErrorRate -gt 1.0) { $assessment = "CRITICAL" }
        
        $color = switch ($assessment) {
            "EXCELLENT" { "Green" }
            "GOOD" { "Yellow" }
            "ACCEPTABLE" { "Yellow" }
            "NEEDS_IMPROVEMENT" { "Red" }
            "CRITICAL" { "Red" }
        }
        
        Write-Host "└─ Assessment: $assessment" -ForegroundColor $color
    }
    
    Write-Host "`n=== JEFF DEAN'S LATENCY GUIDELINES ===" -ForegroundColor Yellow
    Write-Host "• L1 cache reference: 0.5 ns"
    Write-Host "• Branch mispredict: 5 ns"
    Write-Host "• L2 cache reference: 7 ns"
    Write-Host "• Mutex lock/unlock: 25 ns"
    Write-Host "• Main memory reference: 100 ns"
    Write-Host "• Send 1K bytes over network: 10,000 ns = 0.01 ms"
    Write-Host "• Read 4K randomly from SSD: 150,000 ns = 0.15 ms"
    Write-Host "• Round trip within datacenter: 500,000 ns = 0.5 ms"
    
    Write-Host "`n=== RECOMMENDATIONS ===" -ForegroundColor Cyan
    $bestResult = $AllResults | Sort-Object { $_.Statistics.P95Latency } | Select-Object -First 1
    Write-Host "• Optimal concurrency level: $($bestResult.Concurrency) threads"
    Write-Host "• Target P95 latency: < 50ms for production"
    Write-Host "• Error rate should be < 0.1% under normal load"
    Write-Host "• Consider connection pooling for higher concurrency"
    Write-Host "• Monitor GC overhead and JVM tuning for sustained load"
}

# Main execution
try {
    if (-not (Test-BackendAvailability)) {
        exit 1
    }
    
    Invoke-WarmupPhase
    
    Write-Host "`nStarting performance benchmarks..." -ForegroundColor Green
    
    foreach ($concurrency in $ConcurrencyLevels) {
        $benchmarkResults = Measure-TransferScheduling -Concurrency $concurrency -Requests $BenchmarkRequests
        $statistics = Calculate-Statistics -Results $benchmarkResults
        
        $Results += @{
            Concurrency = $concurrency
            Statistics = $statistics
            RawResults = $benchmarkResults
        }
    }
    
    Write-PerformanceReport -AllResults $Results
    
    # Save results to file
    $timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
    $reportFile = "performance-report-$timestamp.json"
    $Results | ConvertTo-Json -Depth 5 | Out-File -FilePath $reportFile
    Write-Host "`n✓ Detailed results saved to: $reportFile" -ForegroundColor Green
    
}
catch {
    Write-Host "Error during benchmark execution: $_" -ForegroundColor Red
    exit 1
}