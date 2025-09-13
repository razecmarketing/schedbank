# Test script to verify Bank Transfer Scheduler API
Write-Host "Testing Bank Transfer Scheduler API..." -ForegroundColor Green

# Test 1: Check if application is running
Write-Host "1. Checking if application is running on port 8080..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method Get
    Write-Host "Application is running: $($response.status)" -ForegroundColor Green
} catch {
    Write-Host "Application not responding on port 8080" -ForegroundColor Red
    exit 1
}

# Test 2: Create a transfer
Write-Host "2. Creating a test transfer..." -ForegroundColor Yellow
$transferRequest = @{
    sourceAccount = "1234567890"
    targetAccount = "0987654321"
    amount = 1000.00
    transferDate = "2025-09-03"
} | ConvertTo-Json

try {
    $transfer = Invoke-RestMethod -Uri "http://localhost:8080/api/transfers" -Method Post -Body $transferRequest -ContentType "application/json"
    Write-Host "Transfer created successfully:" -ForegroundColor Green
    Write-Host "   ID: $($transfer.id)"
    Write-Host "   Amount: R$ $($transfer.amount)"
    Write-Host "   Fee: R$ $($transfer.fee)"
    Write-Host "   Transfer Date: $($transfer.transferDate)"
} catch {
    Write-Host "Failed to create transfer: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: List transfers
Write-Host "3. Listing all transfers..." -ForegroundColor Yellow
try {
    $transfers = Invoke-RestMethod -Uri "http://localhost:8080/api/transfers" -Method Get
    Write-Host "Found $($transfers.Count) transfer(s):" -ForegroundColor Green
    foreach ($t in $transfers) {
        Write-Host "   - $($t.sourceAccount) → $($t.targetAccount): R$ $($t.amount) (Fee: R$ $($t.fee))"
    }
} catch {
    Write-Host "Failed to list transfers: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "Test completed." -ForegroundColor Green
