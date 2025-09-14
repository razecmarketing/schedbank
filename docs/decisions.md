# Architectural Decision Records

## ADR-001: Arquitetura em Camadas

### Status
Aceito

### Contexto
Sistema de agendamento de transferências financeiras precisa de uma arquitetura que separe as responsabilidades e permita manutenibilidade.

### Decisão
Implementar arquitetura em camadas:
- Domain: Regras de negócio puras
- Application: Casos de uso e orquestração
- Infrastructure: Adaptadores para BD, Web, etc.
- Presentation: Interface do usuário

### Consequências
- Separação clara de responsabilidades
- Testabilidade aprimorada
- Manutenibilidade facilitada
- Inversão de dependências

## ADR-002: Persistência H2 em Memória

### Status
Aceito

### Contexto
Sistema precisa de persistência para demonstração, mas não requer produção.

### Decisão
Usar H2 Database em memória.

### Consequências
- Simplicidade de configuração
- Dados perdidos entre reinicializações
- Adequado para demonstração

## ADR-003: Frontend Vue.js 3

### Status
Aceito

### Contexto
Interface de usuário moderna e reativa.

### Decisão
Vue.js 3 com Composition API e Vite.

### Consequências
- Performance otimizada
- API moderna
- Ecossistema robusto