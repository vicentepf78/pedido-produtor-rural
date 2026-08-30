# CH-entrar-e-topo-loja: Entrar próprio, origem e Sair no topo

```yaml
charter:
  id: CH-entrar-e-topo-loja
  mission: "Como Produtor rural, usar o TopoLoja e as telas /entrar e /cadastro para voltar à origem, e ser Operador da revenda só pela Entrar da loja."
  mode: charter-with-tour
  persona:
    name: Produtor rural
    device: phone-large
    network: 4g
    locale: pt-BR
  journey: J-produtor-pedido-rapido
  scenarios: [NAV-topo-loja, AUTH-entrar-tela-propria, AUTH-sessao-atual, AUTH-cadastro-e-sessao, RET-visual-loja]
  tour: Feature Tour
  time_box_minutes: 45
  guidance:
    must_try:
      - "Convidado: Catálogo, Carrinho, Checkout e Pedido no cabeçalho; Entrar abre /entrar?origem=…; Voltar não cria sessão."
      - "GET /api/v1/autenticacao/sessao sem cookie (autenticado false) e depois de Entrar (email e papeis)."
      - "Produtor: origem /checkout ou /meus-pedidos; Sair em dois toques não apaga o carrinho de convidado."
      - "Operador em /entrar?origem=/checkout cai em Pedidos da revenda com TopoOperador, sem Catálogo/Carrinho/Checkout."
    must_avoid:
      - "Não procurar bottom-nav nem botão Modo retaguarda."
      - "Não gravar senha de fixture em print, log ou memória."
      - "Não completar um pedido nesta sessão."
```

<!-- The charter is durable and immutable: re-run it in later cycles; each run's debrief goes in that run's report (Session Debriefs), never here. -->
