# Deep-review round 1 — pedidos-insumos-mvp1

- **Base:** `66e004c` (pai do primeiro commit do loop MVP1; `main` inclui o MVP0 já revisado)
- **Spec:** `.compozy/tasks/pedidos-insumos-mvp1`
- **Subagent:** `codex` (Task `gpt-5.6-sol-medium`; herdr/`claude` ausentes neste host)
- **Working tree:** inclui o dirty tree pós-QA
- **Verdict:** `SHIP`

## Summary

O loop MVP1 entrega catálogo paginado com carrossel fechado, `GET /sessao`, topo por papel, checkout que pula Entrar quando já é `PRODUTOR`, retaguarda no mesmo visual e o par QA. Invariantes de tenant, regulado, idempotência e `origem` relativa estão no código. Os nits abaixo foram remediados nesta mesma iteração.

## Issues (remediados)

| ID | Sev | Finding | Fix |
| --- | --- | --- | --- |
| D1-01 | P3 | `?pagina=` podia ficar na URL do catálogo; o spec manda `pagina` só no HTTP de Carregar mais | `PaginaCatalogo` apaga `pagina` da query; E2E-027 cobre `tamanhoPagina=24&pagina=2` |
| D1-02 | P3 | Select vazio “Propriedade” no checkout colidia com “Nome da propriedade” e aparecia sem lista | Select só com `propriedades.length > 0`; nome só se identificado e lista vazia; foco do select via `useEffect` após o login |
| D1-03 | P3 | Linha da retaguarda só respondia a clique | `tabIndex={0}` + Enter/Espaço; `:focus-visible` |
| D1-04 | P3 | `TopoLoja` montava `origem` sem encode | `encodeURIComponent` no `?origem=` |
| D1-05 | P2 | `make gate` validava `_spec.md` do MVP0 (`SLUG ?= pedidos-insumos-mvp0`) | default `pedidos-insumos-mvp1` |

## Nitpicks

Todos os P3 acima. Nenhum nit ficou aberto.

## Questions

Nenhuma. Worker Fable 5 / herdr continua ausente; a rodada rodou na sessão com lane `codex` via Task.

## Suggested validation

- `make gate` (check-spec agora no slug MVP1 + unit + IT + Playwright)
- E2E-027, E2E-037/037b/037c, E2E-046/047

## Non-issues checked

- `Paginacao.PADRAO=10`; `{10,15,30,50}`; 24 → `TAMANHO_PAGINA_INVALIDO` antes do repositório
- `consulta` em branco lista o recorte; regulado fora da lista; detalhe → `PRODUTO_NAO_ELEGIVEL`
- Cadastro da loja sempre `PRODUTOR`; operador em Entrar ignora `origem` da loja
- `destinoPermitido` recusa URL absoluta / `//` / `..`
- Sair em dois toques; `encerrar` não mexe no carrinho
- `ServicoPedido` idempotência + `DataIntegrityViolation` em transação nova
- `catalog`/`cart` CLOSED; `identity` dono da matriz HTTP; `GET /sessao` `permitAll`
- Features não recalculam elegibilidade/preço
