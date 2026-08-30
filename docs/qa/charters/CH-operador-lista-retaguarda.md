# CH-operador-lista-retaguarda: a retaguarda mostra o snapshot certo e recusa o produtor

```yaml
charter:
  id: CH-operador-lista-retaguarda
  mission: "Como Operador da revenda, listar e abrir pedidos e provar isolamento de papel e de pedido."
  mode: charter-with-tour
  persona:
    name: Operador da revenda
    device: desktop
    network: wifi-fast
    locale: pt-BR
  journey: J-operador-retaguarda
  scenarios: [ORD-backoffice-lista, ORD-backoffice-vazio-ou-negado, ORD-acesso-negado, INT-mock-erp-aceita]
  tour: Feature Tour
  time_box_minutes: 30
  guidance:
    must_try:
      - "Entrar com operador.revenda@example.com em /retaguarda/pedidos e abrir o detalhe somente leitura."
      - "Comparar id, total e ACEITA com o que o produtor viu; refresh não altera o snapshot."
      - "Com sessão de produtor, abrir /retaguarda/pedidos e GET /api/v1/retaguarda/pedidos — ACESSO_NEGADO, sem lista."
      - "Com produtor Alfa, abrir o pedido de Beta em /pedidos/{id} — ACESSO_PEDIDO_NEGADO."
    must_avoid:
      - "Não procurar atalho Backoffice na nav do produtor — ele não deve existir."
      - "Não editar pedido, catálogo ou preço pela retaguarda."
      - "Não assumir que GET /api/v1/retaguarda/pedidos/{id} está em _dx.md; a superfície existe na task_05."
```

<!-- The charter is durable and immutable: re-run it in later cycles; each run's debrief goes in that run's report (Session Debriefs), never here. -->
