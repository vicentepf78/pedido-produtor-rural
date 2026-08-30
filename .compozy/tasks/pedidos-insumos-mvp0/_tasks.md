---
schema_version: "compozy.tasks/v2"
workflow: pedidos-insumos-mvp0
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
    - id: task_07
      file: task_07.md
  edges:
    - from: task_01
      to: task_02
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
    - from: task_06
      to: task_07
---

# Pedidos de insumos agrícolas MVP0 — lista de tarefas

## MVP Boundary

Os wireframes de baixa fidelidade já existem e são o contrato visual: projeto
OpenDesign Cloud **MVP0 — Pedidos de Insumos Agrícolas**, artefato
`mvp0-pedidos-insumos.html`, cópia local em
`references/mvp0-pedidos-insumos.html`. Eles não entram como tarefa numerada.

As tarefas **01–05** implementam o restante da ordem de construção do `_spec.md`
(fundação, identidade/tenant, catálogo/carrinho, checkout/pedido, Mock ERP e
backoffice). As tarefas **06–07** planejam e executam o QA. Pós-MVP: Agrofit,
produtos regulamentados, precificação comercial, ERP real, notificações e o
MVP piloto em `.specs/features/pedidos-insumos-mvp/`. Fora de escopo: nativos,
pagamento, entrega, receita, reserva e multi-loja visível.

| # | Title | Status | Complexity | Dependencies |
| --- | --- | --- | --- | --- |
| 01 | Fundação modular, banco e verificação de arquitetura | pending | high | - |
| 02 | Tenant, identidade, produtor e propriedades | pending | high | task_01 |
| 03 | Catálogo curado e carrinho de convidado | pending | high | task_02 |
| 04 | Checkout, pedido idempotente e Meus pedidos | pending | high | task_02, task_03 |
| 05 | Mock ERP e backoffice somente leitura | pending | high | task_04 |
| 06 | QA Plan and Session Charters | pending | high | task_05 |
| 07 | Real-User QA Execution | pending | critical | task_06 |
