# GUIA DE COMANDOS - Sistema de Agendamento de Transferências Financeiras

## VERIFICAÇÃO DA API

### Verificar se o Spring Boot está rodando:
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method Get
```

### Iniciar o Spring Boot (se não estiver rodando):
```powershell
./run-spring-boot.ps1
```

## COMANDOS PARA AGENDAR TRANSFERÊNCIAS

### 1. TRANSFERÊNCIA BÁSICA (Hoje - Taxa: R$ 3,00 + 2,5%)
```powershell
$transferencia = @{
    sourceAccount = "1234567890"
    targetAccount = "0987654321"
    amount = 1000.00
    transferDate = (Get-Date).ToString('yyyy-MM-dd')
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/transfers" -Method Post -Body $transferencia -ContentType "application/json"
```

### 2. TRANSFERÊNCIAS POR FAIXA DE TAXA

#### D+0 (Mesmo Dia - R$ 3,00 + 2,5% = R$ 28,00 para R$ 1.000)
```powershell
$req = @{ sourceAccount = "1111111111"; targetAccount = "2222222222"; amount = 500.00; transferDate = (Get-Date).ToString('yyyy-MM-dd') } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/transfers" -Method Post -Body $req -ContentType "application/json"
```

#### D+5 (1-10 dias - R$ 12,00 fixo)
```powershell
$req = @{ sourceAccount = "3333333333"; targetAccount = "4444444444"; amount = 800.00; transferDate = (Get-Date).AddDays(5).ToString('yyyy-MM-dd') } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/transfers" -Method Post -Body $req -ContentType "application/json"
```

#### D+15 (11-20 dias - 8,2% = R$ 82,00 para R$ 1.000)
```powershell
$req = @{ sourceAccount = "5555555555"; targetAccount = "6666666666"; amount = 1200.00; transferDate = (Get-Date).AddDays(15).ToString('yyyy-MM-dd') } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/transfers" -Method Post -Body $req -ContentType "application/json"
```

#### D+25 (21-30 dias - 6,9% = R$ 69,00 para R$ 1.000)
```powershell
$req = @{ sourceAccount = "7777777777"; targetAccount = "8888888888"; amount = 2000.00; transferDate = (Get-Date).AddDays(25).ToString('yyyy-MM-dd') } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/transfers" -Method Post -Body $req -ContentType "application/json"
```

#### D+35 (31-40 dias - 4,7% = R$ 47,00 para R$ 1.000)
```powershell
$req = @{ sourceAccount = "9999999999"; targetAccount = "1010101010"; amount = 1500.00; transferDate = (Get-Date).AddDays(35).ToString('yyyy-MM-dd') } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/transfers" -Method Post -Body $req -ContentType "application/json"
```

#### D+45 (41-50 dias - 1,7% = R$ 17,00 para R$ 1.000)
```powershell
$req = @{ sourceAccount = "2020202020"; targetAccount = "3030303030"; amount = 3000.00; transferDate = (Get-Date).AddDays(45).ToString('yyyy-MM-dd') } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/transfers" -Method Post -Body $req -ContentType "application/json"
```

### 3. FUNÇÃO HELPER PARA FACILITAR
```powershell
function New-Transfer($origem, $destino, $valor, $dias) {
    $data = (Get-Date).AddDays($dias).ToString('yyyy-MM-dd')
    $body = @{ sourceAccount = $origem; targetAccount = $destino; amount = $valor; transferDate = $data } | ConvertTo-Json
    Invoke-RestMethod -Uri "http://localhost:8080/api/transfers" -Method Post -Body $body -ContentType "application/json"
}

# EXEMPLOS DE USO:
New-Transfer "1234567890" "0987654321" 1000.00 0   # Hoje
New-Transfer "1111111111" "2222222222" 500.00 5    # D+5
New-Transfer "3333333333" "4444444444" 800.00 15   # D+15
New-Transfer "5555555555" "6666666666" 1200.00 25  # D+25
New-Transfer "7777777777" "8888888888" 1500.00 35  # D+35
New-Transfer "9999999999" "1010101010" 3000.00 45  # D+45
```

## LISTAR TRANSFERÊNCIAS (EXTRATO)

### Listar todas as transferências agendadas:
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/transfers" -Method Get
```

### Listar com formatação:
```powershell
$transferencias = Invoke-RestMethod -Uri "http://localhost:8080/api/transfers" -Method Get
$transferencias | Format-Table sourceAccount, targetAccount, amount, fee, transferDate
```

## TESTE DE ERRO (> 50 dias)

### Transferência que deve ser rejeitada:
```powershell
$erro = @{ sourceAccount = "1234567890"; targetAccount = "0987654321"; amount = 1000.00; transferDate = (Get-Date).AddDays(55).ToString('yyyy-MM-dd') } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/transfers" -Method Post -Body $erro -ContentType "application/json"
```
**Resposta esperada:** Erro 400 - "No applicable fee policy"

## TABELA DE TAXAS DE REFERÊNCIA

| Dias      | Taxa Fixa | Taxa %  | Exemplo (R$ 1.000) |
|-----------|-----------|---------|---------------------|
| 0         | R$ 3,00   | 2,5%    | R$ 28,00           |
| 1-10      | R$ 12,00  | 0,0%    | R$ 12,00           |
| 11-20     | R$ 0,00   | 8,2%    | R$ 82,00           |
| 21-30     | R$ 0,00   | 6,9%    | R$ 69,00           |
| 31-40     | R$ 0,00   | 4,7%    | R$ 47,00           |
| 41-50     | R$ 0,00   | 1,7%    | R$ 17,00           |
| > 50      | ERRO      | N/A     | Rejeitado          |

## VALIDAÇÕES DO SISTEMA

### Regras de Negócio:
- **Contas**: Devem ter exatamente 10 dígitos
- **Valores**: Sempre positivos (> 0)
- **Datas**: Formato yyyy-MM-dd, não pode ser no passado
- **Conta origem ≠ conta destino**: Não permite transferência para a mesma conta
- **> 50 dias**: Sistema rejeita automaticamente

### Exemplo de Resposta de Sucesso:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "sourceAccount": "1234567890",
  "targetAccount": "0987654321",
  "amount": 1000.00,
  "fee": 28.00,
  "scheduleDate": "2025-09-04",
  "transferDate": "2025-09-04"
}
```

## SCRIPTS AUTOMATIZADOS

### Validação completa de todas as faixas:
```powershell
./scripts/validacao-completa.ps1
```

### Demo básico de taxas:
```powershell
./scripts/demo-fees.ps1
```

### Teste básico da API:
```powershell
./test-api.ps1
```

## TROUBLESHOOTING

### API não responde:
1. Verificar se o Spring Boot está rodando: `netstat -an | findstr :8080`
2. Iniciar: `./run-spring-boot.ps1`
3. Aguardar logs de inicialização

### Erro de validação:
- Verificar formato das contas (10 dígitos)
- Verificar se valor > 0
- Verificar formato da data (yyyy-MM-dd)

### Erro 400:
- Transferência > 50 dias
- Mesma conta origem/destino
- Campos obrigatórios ausentes

---
**Data de criação:** 4 de setembro de 2025  
**Sistema:** Bank Transfer Scheduler  
**Versão:** 1.0.0
