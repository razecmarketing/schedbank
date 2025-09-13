# Arquitetura Clean para Sistema de Agendamento de Transferências

## Decisões Arquiteturais

### Clean Architecture
- **Domain**: Regras de negócio puras, independentes de frameworks
- **Application**: Orquestração de casos de uso
- **Infrastructure**: Adaptadores para frameworks, banco de dados, APIs

### Princípios SOLID Aplicados
- **Single Responsibility**: Cada classe tem uma única responsabilidade
- **Open/Closed**: Extensível para novas políticas de taxa sem modificar código existente
- **Liskov Substitution**: Substituição transparente de políticas de taxa
- **Interface Segregation**: Interfaces específicas (FeePolicy, TransferRepository)
- **Dependency Inversion**: Domínio não depende de infraestrutura

### Domain-Driven Design
- **Entities**: Transfer com invariantes de negócio
- **Value Objects**: Money (valor monetário), AccountNumber (conta bancária)
- **Policies**: Estratégias de cálculo de taxa por faixa de dias
- **Ports**: Abstrações para repositório e serviços externos

## Estrutura de Camadas

```
src/main/java/com/bank/scheduler/
├── domain/                    # Regras de negócio puras
│   ├── entities/             # Transfer
│   ├── valueobjects/         # Money, AccountNumber
│   ├── policies/             # Estratégias de taxa
│   ├── ports/                # Interfaces para infraestrutura
│   └── exceptions/           # Exceções de domínio
├── application/              # Casos de uso
│   └── usecases/            # TransferSchedulerService
└── infrastructure/          # Adaptadores
    ├── persistence/         # JPA/H2
    ├── web/                 # REST Controllers, DTOs
    ├── config/              # Configuração Spring
    └── monitoring/          # Métricas, logs
```

## Padrões Utilizados

### Strategy Pattern
- `FeePolicy` com implementações específicas por faixa de dias
- Permite extensão fácil para novas regras de taxa

### Repository Pattern
- `TransferRepository` abstrai persistência
- Implementação JPA na camada de infraestrutura

### DTO Pattern
- Separação entre objetos de domínio e API
- `ScheduleTransferRequest`, `TransferResponse`

## Validações de Domínio

### Transfer Entity
- Contas devem ter 10 dígitos exatos
- Valor deve ser positivo
- Data de transferência não pode ser no passado
- Não permite transferência para a mesma conta

### Fee Policies
- Validação de faixas de dias específicas
- Rejeição automática para > 50 dias

## Testes

### Estratégia TDD
- Testes de unidade para cada política de taxa
- Testes de integração para o fluxo completo
- Testes de contrato para APIs REST

### Cobertura
- Domain: 100% (regras críticas de negócio)
- Application: 95% (casos de uso)
- Infrastructure: 80% (adaptadores)
