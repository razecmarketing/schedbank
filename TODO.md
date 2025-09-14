# TODO List

Algumas coisas que preciso arrumar quando der tempo:

- [ ] Melhorar a validação de conta - às vezes aceita coisa estranha
- [ ] Cache do Redis tá meio lento, talvez trocar por algo mais simples  
- [ ] Frontend precisa de loading states melhores
- [ ] Adicionar logs mais detalhados pra debug
- [ ] Testar com volumes maiores de dados
- [x] ~~Implementar as taxas básicas~~ - feito
- [ ] Documentar as APIs direito (swagger tá pela metade)
- [ ] Refatorar o cálculo de taxas - ficou meio confuso

## Ideias futuras

- Notificações por email quando transferência for processada
- Dashboard com métricas  
- Integração com outros bancos
- App mobile?

## Bugs conhecidos

- Às vezes o cache não limpa direito
- Frontend bugga se deixar aberto muito tempo
- Performance meio lenta com Redis - investigar