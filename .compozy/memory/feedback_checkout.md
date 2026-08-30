---
name: checkout campo-nome-propriedade-timing-e2e
description: "seq 3360: assistant explica que o campo batia nos rótulos do Playwright e que passará a ser mostrado só depois da identificação"
type: feedback
scope: workspace
provenance:
  source_sessions:
  - sess-2808f3298bc13781
  source_actor: extractor
  confidence: candidate
  created_at: 2026-08-30T22:14:59.567732702Z
  updated_at: 2026-08-30T22:14:59.567732702Z
---

Evitar exibir o campo de nome da propriedade antes da etapa de identificação no checkout: quando aparece cedo demais, os rótulos do Playwright conflitam e os testes E2E falham.
