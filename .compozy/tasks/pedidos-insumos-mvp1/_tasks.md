---
schema_version: "compozy.tasks/v2"
workflow: pedidos-insumos-mvp1
graph:
  nodes:
    - id: task_01
      file: task_01.md
    - id: task_02
      file: task_02.md
    - id: task_03
      file: task_03.md
    - id: task_04
      file: task_04.md
    - id: task_05
      file: task_05.md
    - id: task_06
      file: task_06.md
  edges:
    - from: task_01
      to: task_02
    - from: task_01
      to: task_03
    - from: task_02
      to: task_03
    - from: task_02
      to: task_04
    - from: task_03
      to: task_04
    - from: task_04
      to: task_05
    - from: task_05
      to: task_06
---

# Pedidos de insumos agrícolas MVP 1 — lista de tarefas

## MVP Boundary

As tarefas **01–04** implementam o recorte da loja do produtor, o login
com telas próprias e a retaguarda no mesmo visual. As tarefas **05–06**
planejam e executam o QA. Pós-MVP: Google, recuperação de senha, preço
negociado, Agrofit, ERP real, outbox, defensivos, importação de
catálogo e área de conta. Fora de escopo: nativos, pagamento, entrega,
receita, reserva e multi-loja visível.

O contrato visual já existe: OpenDesign **MVP 1 — Pedidos de Insumos
Agrícolas**, artefato
`docs/design/opendesign/pedidos-insumos-mvp1/loja-insumos-agricolas.html`.
Não entra como tarefa numerada.

| # | Title | Status | Complexity | Dependencies |
| --- | --- | --- | --- | --- |
| 01 | Catálogo paginado, sessão e contratos HTTP | pending | high | - |
| 02 | Topo da loja, Entrar, Criar conta e destino por papel | pending | high | task_01 |
| 03 | Carrossel, busca composta e Carregar mais | pending | high | task_01, task_02 |
| 04 | Carrinho, checkout, pedidos e retaguarda no visual novo | pending | high | task_02, task_03 |
| 05 | QA Plan and Session Charters | pending | high | task_04 |
| 06 | Real-User QA Execution | pending | critical | task_05 |
