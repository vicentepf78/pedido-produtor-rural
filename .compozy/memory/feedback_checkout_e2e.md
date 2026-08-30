---
name: checkout-e2e campo-propriedade-pos-identificacao
description: "seq 3622: e2e fecharam após corrigir labels ambíguos; propriedade só no checkout"
type: feedback
scope: workspace
provenance:
  source_sessions:
  - sess-2808f3298bc13781
  source_actor: extractor
  confidence: candidate
  created_at: 2026-08-30T22:19:10.721116845Z
  updated_at: 2026-08-30T22:19:10.721116845Z
---

Em testes Playwright do checkout, rótulos ambíguos do campo de propriedade que colidem com outros textos da página quebram getByText em modo estrito; o campo deve aparecer só após a identificação no fluxo de checkout.
