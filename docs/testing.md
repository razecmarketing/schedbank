# Estratégia de Testes

## Níveis de Teste

### 1. Testes Unitários
- Domínio: entidades, value objects, políticas
- Casos de uso: serviços de aplicação
- Validações: regras de negócio
- Cobertura mínima: 85%

### 2. Testes de Integração
- Repositórios
- Controllers REST
- Configurações
- Cenários de ponta a ponta

### 3. Testes de Aceitação
- Cenários de negócio
- Fluxos completos
- Validações funcionais

## Práticas

1. **TDD**
   - Red: Escrever teste que falha
   - Green: Implementação mínima
   - Refactor: Melhorar design

2. **Nomenclatura**
   - Given_When_Then
   - Descrição clara do cenário
   - Documentação através dos testes

3. **Organização**
   - Testes unitários junto ao código
   - Testes de integração separados
   - Fixtures centralizadas

4. **Cobertura**
   - Relatórios automatizados
   - Métricas de qualidade
   - Code coverage como gate
