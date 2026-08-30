# VC-01 — Catálogo povoado (390×844)

## Veredito: PASS

Shell, busca, abas, aviso de regulados, cartões (thumb + nome + descrição + preço/unidade + ação) e nav inferior batem com a Tela 1 Resultados.

## Divergências autorizadas
- 3 SKUs de fixture visual no lugar dos 4 de demo / 30 do seed (task_03 VC-01 + SD-009).
- Sem aba Correção: a fixture E2E não inclui SKU dessa categoria; o seed Flyway tem Correção.
- Sem barra Wireframe / chips / rótulo “Tela 1” / atalho Backoffice / aba Defensivos.
- Ordem e nomes dos cartões seguem o catálogo real (Aurora, Ureia, DK697).

## Divergências de conteúdo (runtime)
- Texto de Ureia: “Este produto não está disponível.” A API não expõe previsão de reposição do wireframe.

## Bloqueios
Nenhum. `blocking_divergences: 0`.
