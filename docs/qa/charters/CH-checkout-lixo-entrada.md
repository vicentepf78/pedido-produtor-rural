# CH-checkout-lixo-entrada: o checkout sobrevive a entrada abusiva sem perder carrinho nem vazar senha

```yaml
charter:
  id: CH-checkout-lixo-entrada
  mission: "Como Produtor após falha, abusar de cadastro, entrada e campos de checkout para ver se o erro fica em foco e o carrinho permanece."
  mode: charter-with-tour
  persona:
    name: Produtor após falha
    device: phone-large
    network: 4g
    locale: pt-BR
  journey: J-produtor-pedido-rapido
  scenarios: [AUTH-cadastro-e-sessao, CHK-erros-validacao, CHK-identificacao-tardia, CART-convidado-persiste]
  tour: Garbage Tour
  time_box_minutes: 60
  guidance:
    must_try:
      - "Submeter e-mail duplicado, senha errada, clique duplo e campos vazios; mensagens de _dx.md em foco aria-invalid."
      - "Colar texto longo ou lixo no e-mail/senha e confirmar sem propriedade/retirada."
      - "Forçar POST /api/v1/autenticacao/saida e sessão expirada; cookie chaveCarrinhoConvidado e totais intactos."
      - "Conferir cookies sessao e XSRF-TOKEN; senha nunca na resposta, URL ou mensagem."
    must_avoid:
      - "Não tratar isto como teste de penetração ou injeção."
      - "Não gravar senha de fixture em evidência."
      - "Não inspecionar a retaguarda nesta sessão."
```

<!-- The charter is durable and immutable: re-run it in later cycles; each run's debrief goes in that run's report (Session Debriefs), never here. -->
