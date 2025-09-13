# Validação Exata dos Agendamentos Conforme Requisitos
# Data: 3 de setembro de 2025

param(
    [string]$BaseUrl = "http://localhost:8080"
)

Write-Host "=== VALIDAÇÃO MINUCIOSA DOS AGENDAMENTOS ===" -ForegroundColor Cyan
Write-Host "Requisitos: Sistema de agendamento de transferências financeiras" -ForegroundColor White
Write-Host "Tabela de taxas EXATA do pedido:" -ForegroundColor White
Write-Host "0 dias    : R$ 3,00 + 2,5%" -ForegroundColor Gray
Write-Host "1-10 dias : R$ 12,00 + 0,0%" -ForegroundColor Gray  
Write-Host "11-20 dias: R$ 0,00 + 8,2%" -ForegroundColor Gray
Write-Host "21-30 dias: R$ 0,00 + 6,9%" -ForegroundColor Gray
Write-Host "31-40 dias: R$ 0,00 + 4,7%" -ForegroundColor Gray
Write-Host "41-50 dias: R$ 0,00 + 1,7%" -ForegroundColor Gray
Write-Host "> 50 dias : ERRO - não permitir" -ForegroundColor Red
Write-Host ""

# Verificar API
try {
    $health = Invoke-RestMethod -Uri "$BaseUrl/actuator/health" -Method Get
    Write-Host "API funcionando: $($health.status)" -ForegroundColor Green
} catch {
    Write-Host "API indisponível em $BaseUrl" -ForegroundColor Red
    Write-Host "Execute primeiro: ./run-spring-boot.ps1" -ForegroundColor Yellow
    exit 1
}

$valor = 1000.00
$contaOrigem = "1234567890"
$contaDestino = "0987654321"

Write-Host "=== TESTANDO CADA FAIXA EXATA ===" -ForegroundColor Cyan
Write-Host "Valor base: R$ $valor" -ForegroundColor White
Write-Host ""

# Função para calcular taxa esperada
function Get-TaxaEsperada($dias, $valor) {
    switch ($dias) {
        0 { return 3.00 + ($valor * 0.025) }           # R$ 3,00 + 2,5%
        {$_ -ge 1 -and $_ -le 10} { return 12.00 }     # R$ 12,00
        {$_ -ge 11 -and $_ -le 20} { return $valor * 0.082 }  # 8,2%
        {$_ -ge 21 -and $_ -le 30} { return $valor * 0.069 }  # 6,9%
        {$_ -ge 31 -and $_ -le 40} { return $valor * 0.047 }  # 4,7%
        {$_ -ge 41 -and $_ -le 50} { return $valor * 0.017 }  # 1,7%
        default { return -1 }  # Erro
    }
}

# Testar cada faixa
$faixasTeste = @(
    @{dias=0; descricao="Mesmo dia"},
    @{dias=1; descricao="Início faixa 1-10"},
    @{dias=5; descricao="Meio faixa 1-10"},
    @{dias=10; descricao="Fim faixa 1-10"},
    @{dias=11; descricao="Início faixa 11-20"},
    @{dias=15; descricao="Meio faixa 11-20"},
    @{dias=20; descricao="Fim faixa 11-20"},
    @{dias=21; descricao="Início faixa 21-30"},
    @{dias=25; descricao="Meio faixa 21-30"},
    @{dias=30; descricao="Fim faixa 21-30"},
    @{dias=31; descricao="Início faixa 31-40"},
    @{dias=35; descricao="Meio faixa 31-40"},
    @{dias=40; descricao="Fim faixa 31-40"},
    @{dias=41; descricao="Início faixa 41-50"},
    @{dias=45; descricao="Meio faixa 41-50"},
    @{dias=50; descricao="Fim faixa 41-50"},
    @{dias=51; descricao="ERRO > 50 dias"}
)

$agendamentosOk = 0
$totalTestes = $faixasTeste.Count

foreach ($teste in $faixasTeste) {
    $dias = $teste.dias
    $desc = $teste.descricao
    $dataTransf = (Get-Date).AddDays($dias).ToString('yyyy-MM-dd')
    $taxaEsperada = Get-TaxaEsperada $dias $valor
    
    $body = @{
        sourceAccount = $contaOrigem
        targetAccount = $contaDestino  
        amount = $valor
        transferDate = $dataTransf
    } | ConvertTo-Json
    
    Write-Host "Testando D+$dias ($desc) - Data: $dataTransf" -ForegroundColor Yellow
    Write-Host "  Taxa esperada: R$ $($taxaEsperada.ToString('F2'))" -ForegroundColor Gray
    
    try {
        $resp = Invoke-RestMethod -Uri "$BaseUrl/api/transfers" -Method Post -Body $body -ContentType 'application/json'
        
        if ($taxaEsperada -eq -1) {
            Write-Host "  ERRO: Deveria ter falhado mas foi aceito!" -ForegroundColor Red
        } else {
            $taxaReal = [decimal]$resp.fee
            $diferenca = [Math]::Abs($taxaReal - $taxaEsperada)
            
            if ($diferenca -lt 0.01) {
                Write-Host "  Taxa correta: R$ $($taxaReal.ToString('F2'))" -ForegroundColor Green
                $agendamentosOk++
            } else {
                Write-Host "  Taxa incorreta: R$ $($taxaReal.ToString('F2')) (esperada: R$ $($taxaEsperada.ToString('F2')))" -ForegroundColor Red
            }
            
            Write-Host "  ID: $($resp.id)" -ForegroundColor Gray
        }
    } catch {
        if ($taxaEsperada -eq -1) {
            Write-Host "  Rejeitado corretamente (> 50 dias)" -ForegroundColor Green
            $agendamentosOk++
        } else {
            Write-Host "  Falhou inesperadamente: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
    Write-Host ""
}

Write-Host "=== EXTRATO DE AGENDAMENTOS ===" -ForegroundColor Cyan
try {
    $lista = Invoke-RestMethod -Uri "$BaseUrl/api/transfers" -Method Get
    Write-Host "Total de agendamentos: $($lista.Count)" -ForegroundColor White
    
    foreach ($t in $lista) {
        $dias = [Math]::Round(([DateTime]$t.transferDate - [DateTime]$t.scheduleDate).TotalDays)
        Write-Host "  $($t.sourceAccount) → $($t.targetAccount): R$ $($t.amount) (Taxa: R$ $($t.fee)) - D+$dias" -ForegroundColor Gray
    }
} catch {
    Write-Host "Erro ao listar: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== RESULTADO FINAL ===" -ForegroundColor Cyan
Write-Host "Testes corretos: $agendamentosOk/$totalTestes" -ForegroundColor White

if ($agendamentosOk -eq $totalTestes) {
    Write-Host "TODOS OS REQUISITOS ATENDIDOS!" -ForegroundColor Green
} else {
    Write-Host "Há discrepâncias nos cálculos de taxa" -ForegroundColor Red
}
