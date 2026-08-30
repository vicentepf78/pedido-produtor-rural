# Workflow Memory: pedidos-insumos-mvp1

## Current State

- Phase B `task_03` implementada e verificada (Playwright catálogo 28/28, suíte 46/46).
- Próxima ação esperada: Phase B `execute_task task=task_04`.
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
- Destino pós-Entrar: `resolverDestinoAposEntrada`; operador ignora `origem` da loja.
- Sair no topo é dois toques e não apaga o carrinho de convidado.
- `eng-ui-screenshot` não está instalado neste repo; não inventar pacote visual substituto.
- Query do catálogo S1: `categoria`, `consulta`, `tamanhoPagina`. `pagina` não entra na URL.
- Mock e2e de catálogo lista o recorte quando `consulta` está em branco e pagina os 30 SKUs semeados.

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
- `task_02` concluída: TopoLoja, `/entrar` `/cadastro`, destino por papel, TopoOperador mínimo.
- QA flag-only task_02: reset `AUTH-cadastro-e-sessao`; novos `AUTH-entrar-tela-propria`, `NAV-topo-loja`. Walk na Phase C.
- `task_03` concluída: carrossel fechado, busca composta, Carregar mais, query persistente.
- QA flag-only task_03: reset `CAT-produtor-descobre-produto`; novo `CAT-carrossel-e-carregar-mais`. Walk na Phase C.
- `task_04` não reconstrói o catálogo S1 nem o topo/identidade.
