# Contribuindo para o Bank Transfer Scheduler

## Padrões de Código

### Clean Code
- Funções curtas e focadas
- Nomes significativos
- Zero duplicação
- Sem comentários desnecessários

### SOLID Principles
- Single Responsibility
- Open/Closed
- Liskov Substitution
- Interface Segregation
- Dependency Inversion

## Estrutura de Commits

```
feat: implement user authentication with JWT
fix: correct null pointer issue in payment service
refactor: extract order validation into separate class
test: add unit test for email service
docs: update architecture.md with new dependency rule
```

## Processo de Desenvolvimento

1. **Branch**: Criar feature/bugfix branch
2. **TDD**: Red → Green → Refactor
3. **Tests**: Cobertura mínima 80%
4. **Review**: Pull request obrigatório
5. **Quality**: Sem violações SOLID

## Validações Obrigatórias

- [ ] Testes passando
- [ ] Cobertura mantida
- [ ] Clean Architecture preservada
- [ ] Zero dependências circulares
- [ ] Documentação atualizada
