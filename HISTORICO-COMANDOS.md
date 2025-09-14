# HISTÓRICO DE COMANDOS TESTADOS

## 2025-09-04 - Comandos executados com sucesso

### Health Check
```
Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method Get
```

### Transferência D+0 (R$ 1000 - Taxa esperada: R$ 28,00)
```
$req = @{ sourceAccount = "1234567890"; targetAccount = "0987654321"; amount = 1000.00; transferDate = "2025-09-04" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/transfers" -Method Post -Body $req -ContentType "application/json"
```

### Transferência D+5 (R$ 500 - Taxa esperada: R$ 12,00)
```
$req = @{ sourceAccount = "1111111111"; targetAccount = "2222222222"; amount = 500.00; transferDate = "2025-09-09" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/transfers" -Method Post -Body $req -ContentType "application/json"
```

### Listar transferências
```
Invoke-RestMethod -Uri "http://localhost:8080/api/transfers" -Method Get
```

---
Para adicionar novos comandos testados, edite este arquivo.
