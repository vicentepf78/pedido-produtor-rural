# Workflow Memory: pedidos-insumos-mvp1

## Current State

- Phase B `task_01` implementada e verificada (`make test` + `make test-integration`).
- Próxima ação esperada: Phase B `execute_task task=task_02`.
- Branch de trabalho: `mvp-1`. Sem `--frontend` e sem `--stacked`.

## Shared Decisions

- Pacote Java-base herdado do MVP 0: `br.agriplataforma`.
- Spring Boot 4.1.1 + Spring Modulith 2.1.1; módulos não-OPEN; pacote `application` é named interface.
- Tenant semeado: `11111111-1111-1111-1111-111111111111`.
- Desenvolvimento nesta branch: `mvp-1`.
- Contrato visual já existe: `docs/design/opendesign/pedidos-insumos-mvp1/loja-insumos-agricolas.html`. Não é tarefa numerada.
- Tarefas 01–04 implementam a loja; 05–06 são o par QA. Sem worker frontend; execução local.
- Seed de catálogo não foi alterado para casar `_dx.md`: ureia semeada é `Ureia 45% N` @ 178.00 (UUID `...0013`), indisponível. ITs usam a semente; unitários mockam o exemplo 198.00.
- `GET /error` é `permitAll` para erros de validação não virarem 401.

## Shared Learnings

- `detect-phase.py` emite `phase=0 action=bootstrap` enquanto `state.yaml` não existe.
- `init-state.py` detectou `mode=tasks` pelo grafo `_tasks.md` + `task_*.md`.
- Jackson databind não entra no compile de teste; ITs HTTP devem parsear o corpo sem `ObjectMapper`.
- `Paginacao` da loja recusa 24; retaguarda continua com 25.

## Open Risks

- Nenhum bloqueio externo no bootstrap.

## Open Questions

- Nenhuma no bootstrap.

## Handoffs

- `task_01` concluída: catálogo paginado, `GET /sessao`, cadastro/entrada com `email`+`papeis`.
- QA flag-only: reset `CAT-produtor-descobre-produto`, `AUTH-cadastro-e-sessao`, `CAT-produto-indisponivel-ou-regulado`; novos `CAT-paginacao-catalogo`, `AUTH-sessao-atual`. Walk na Phase C.
- `task_02` herda destino/`origem` e topo (UT-071/UT-072).
