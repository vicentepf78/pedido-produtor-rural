# Task Memory: task_03

## Objective Snapshot

Superfície S1: carrossel fechado, busca composta, tamanho 10/15/30/50 e Carregar mais, com query persistente ao detalhe.

## Important Decisions

- Filtros e tamanho vivem na query (`categoria`, `consulta`, `tamanhoPagina`); `pagina` fica só no estado da lista.
- Carrossel é conjunto fechado no cliente (`ITENS_CARROSSEL`); slugs `todos|sementes|fertilizantes|correcao` nos `data-od-id`.
- Cards do carrossel usam `role="listitem"` como o HTML de referência — testes clicam por `data-od-id`.
- `consulta` na URL só muda após debounce de 300 ms; o campo local não é resincronizado a cada identidade de `URLSearchParams`.
- Tamanho inválido na query é removido e cai no padrão 10; a API nunca recebe `24`.
- Mock e2e passou a servir os 30 produtos semeados, com `categoria`/`pagina`/`tamanhoPagina` e busca em branco listando o recorte.
- `eng-ui-screenshot` ausente: gate visual do companion omitido — sem pacote substituto.
- Peer review e walk de QA adiados (Phase D / Phase C).

## Learnings

- Sincronizar o input de busca em `params` (objeto novo a cada render) apagava o texto antes do debounce — E2E-012/021 falharam até o efeito depender só de `consultaParam`.
- Playwright não expõe o card do carrossel como `button` quando `role="listitem"`.

## Files / Surfaces

- `frontend/src/features/catalog/{PaginaCatalogo,api,CarrosselCategoria,ControleTamanhoLista,BotaoCarregarMais,CartaoProduto,categorias}.ts(x)`
- `frontend/src/infra/http.ts` (`getJson` aceita `signal`)
- `frontend/src/estilos.css`, `frontend/public/media/categorias/*.svg`
- `frontend/e2e/catalog.spec.ts`, `frontend/e2e/helpers/apiMock.ts`
- QA: reset `CAT-produtor-descobre-produto`; novo `CAT-carrossel-e-carregar-mais`

## Errors / Corrections

- E2E-001: asserção por `listitem`/`button` “Todos” → `data-od-id`.
- E2E-012/021: reset do input por `useSearchParams` a cada render.
- E2E-019: segundo `click()` no botão que some após o primeiro bloco — `Promise.all` de dois cliques.

## Ready for Next Run

- Playwright `catalog.spec.ts` 28/28 e suíte frontend 46/46, 2026-08-30.
- `eng-ui-screenshot` ausente: VC-01–VC-07 sem bundle companion.
- Próxima detect-phase deve ser `task_04`.
