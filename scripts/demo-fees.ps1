# Demo de cálculo de taxas por faixas de dias
param(
    [string]$BaseUrl = "http://localhost:8080"
)

Write-Host "Verificando saúde da API..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "$BaseUrl/actuator/health" -Method Get
    Write-Host "API OK: $($health.status)" -ForegroundColor Green
} catch {
    Write-Host "API indisponível em $BaseUrl" -ForegroundColor Red
    exit 1
}

$src = "1234567890"
$dst = "0987654321"
$amount = 1000.00
$offsets = @(0, 5, 15, 25, 35, 45, 55)

foreach ($d in $offsets) {
    $date = (Get-Date).AddDays($d).ToString('yyyy-MM-dd')
    $body = @{ sourceAccount = $src; targetAccount = $dst; amount = $amount; transferDate = $date } | ConvertTo-Json
    Write-Host "\nAgendando em $date (D+$d) ..." -ForegroundColor Cyan
    try {
        $resp = Invoke-RestMethod -Uri "$BaseUrl/api/transfers" -Method Post -Body $body -ContentType 'application/json'
        Write-Host ("ID={0} | amount={1} | fee={2} | transferDate={3}" -f $resp.id, $resp.amount, $resp.fee, $resp.transferDate)
    } catch {
        Write-Host "Falhou (provavelmente > 50 dias): $($_.Exception.Message)" -ForegroundColor Yellow
    }
}

Write-Host "\nListando agendamentos:" -ForegroundColor Yellow
try {
    $list = Invoke-RestMethod -Uri "$BaseUrl/api/transfers" -Method Get
    Write-Host ("Total: {0}" -f $list.Count)
} catch {
    Write-Host "Erro listando: $($_.Exception.Message)" -ForegroundColor Red
}
