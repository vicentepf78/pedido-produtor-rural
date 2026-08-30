# CH-pedido-rapido-money: o pedido rápido realmente gera um único pedido com confirmação fiel

```yaml
charter:
  id: CH-pedido-rapido-money
  mission: "Como Produtor rural, percorrer o pedido rápido até o true end e atacar os hot spots de idempotência, produto regulamentado e mock que não muta o snapshot."
  mode: charter-with-tour
  persona:
    name: Produtor rural
    device: phone-large
    network: 4g
    locale: pt-BR
  journey: J-produtor-pedido-rapido
  scenarios: [CAT-produtor-descobre-produto, CAT-produto-indisponivel-ou-regulado, CART-monta-e-ajusta, CHK-identificacao-tardia, ORD-confirma-e-revisita, INT-mock-erp-aceita]
  tour: Money Tour
  time_box_minutes: 60
  guidance:
    must_try:
      - "Confirmar duas vezes com a mesma chave de idempotência e recarregar a confirmação — um só pedido, mesmo id."
      - "Abrir o UUID regulamentado 10000000-0000-4000-8000-000000000099 por URL e tentar adicioná-lo; listagem sem Defensivos."
      - "Depois de ACEITA, recarregar /pedidos/{id} e /meus-pedidos e conferir que itens, preços e total não mudaram."
      - "Conferir GET /actuator/health antes do walk e que agriplataforma.gatewayErp=simulado não cria segundo pedido."
    must_avoid:
      - "Não exigir pagamento, entrega, desconto ou ERP real."
      - "Não usar senha de fixture em log, print ou memória."
      - "Não abrir a retaguarda nesta sessão."
```

<!-- The charter is durable and immutable: re-run it in later cycles; each run's debrief goes in that run's report (Session Debriefs), never here. -->
