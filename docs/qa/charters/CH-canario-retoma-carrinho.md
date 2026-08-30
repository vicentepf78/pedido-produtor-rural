# CH-canario-retoma-carrinho: o carrinho de convidado sobrevive a interrupção (canário adjacente)

```yaml
charter:
  id: CH-canario-retoma-carrinho
  mission: "Como Produtora no campo, interromper o carrinho de convidado e retomar — canário de continuidade ao lado do pedido rápido."
  mode: charter-with-tour
  persona:
    name: Produtora no campo
    device: phone-small
    network: flaky
    locale: pt-BR
  journey: J-convidado-retoma-carrinho
  scenarios: [CART-convidado-persiste, CART-monta-e-ajusta]
  tour: Interrupt Tour
  time_box_minutes: 30
  guidance:
    must_try:
      - "Adicionar item, fechar a aba, voltar; PATCH/DELETE e reload devem manter o último estado confirmado."
      - "Cortar a rede no meio de um ajuste; o total não pode sumir nem inventar linha."
      - "Fazer logout de identidade e conferir que chaveCarrinhoConvidado permanece."
      - "Em 375px, conferir que quantidade e total não dependem de hover."
    must_avoid:
      - "Não concluir o checkout nesta sessão — o canário para no carrinho retomado."
      - "Não limpar cookies no meio do box, salvo para o ramo explícito de dados apagados."
      - "Não inspecionar a retaguarda."
```

<!-- The charter is durable and immutable: re-run it in later cycles; each run's debrief goes in that run's report (Session Debriefs), never here. -->
