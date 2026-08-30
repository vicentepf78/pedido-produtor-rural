# CH-descoberta-carrossel: o recorte do catálogo cabe no celular sem scroll infinito

```yaml
charter:
  id: CH-descoberta-carrossel
  mission: "Como Produtor rural, recortar o catálogo pelo carrossel fechado, somar busca e tamanho de página e avançar com Carregar mais sem perder o recorte ao voltar do detalhe."
  mode: charter-with-tour
  persona:
    name: Produtor rural
    device: phone-large
    network: 4g
    locale: pt-BR
  journey: J-produtor-pedido-rapido
  scenarios: [CAT-carrossel-e-carregar-mais, CAT-produtor-descobre-produto, CAT-paginacao-catalogo, CAT-produto-indisponivel-ou-regulado]
  tour: Landmark Tour
  time_box_minutes: 45
  guidance:
    must_try:
      - "Tocar Todos, Sementes, Fertilizantes e Correção; confirmar que não há card extra nem Defensivos."
      - "Somar consulta ureia em Fertilizantes; Limpar filtros volta ao recorte Todos com tamanho 10."
      - "Trocar tamanho 10/15/30/50; Carregar mais duas vezes rápido não duplica cards; query tem categoria, consulta e tamanhoPagina — sem pagina."
      - "Abrir um detalhe e Voltar: o recorte e a busca permanecem."
    must_avoid:
      - "Não exigir foto real de categoria nem scroll infinito."
      - "Não inventar SKU ou preço fora do seed/fixture."
      - "Não abrir checkout nesta sessão além de um toque acidental."
```

<!-- The charter is durable and immutable: re-run it in later cycles; each run's debrief goes in that run's report (Session Debriefs), never here. -->
