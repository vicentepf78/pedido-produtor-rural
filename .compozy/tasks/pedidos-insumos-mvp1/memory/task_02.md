# Task Memory: task_02

## Objective Snapshot

TopoLoja no lugar da bottom-nav, rotas `/entrar` e `/cadastro` com whitelist de `origem`, destino por papel.

## Important Decisions

- Destino extraído em `resolverDestinoAposEntrada` (UT-071/UT-072). Operador sempre `/retaguarda/pedidos`.
- `origem` na URL fica sem encode (`/entrar?origem=/checkout`) para casar `_dx.md`.
- Sair no topo é dois toques (arma e confirma) e uma só `POST /saida` (US-006.EC-6).
- Checkout inline continua no visual do MVP 0; só sincroniza a sessão do topo após cadastro/entrada/saída.
- `eng-ui-screenshot` não está instalado; o gate visual do companion foi omitido — sem pacote substituto inventado.
- Peer review e walk de QA adiados (Phase D / Phase C).

## Learnings

- `GET /api/v1/autenticacao/sessao` precisa existir no mock de e2e; sem isso o topo fica convidado após reload.
- E2E-010 deixou de exigir ausência de “Navegação principal”: produtor na retaguarda mantém TopoLoja.

## Files / Surfaces

- `frontend/src/features/identity/`, `shell/TopoLoja.tsx`, `shell/TopoOperador.tsx`, `estado/ProvedorSessao.tsx`
- `LayoutApp`, `App`, `PaginaMeusPedidos`, `checkout/api.ts`, `estilos.css`
- e2e: `destino-origem.spec.ts`, `identity-topo.spec.ts`, `helpers/apiMock.ts`

## Errors / Corrections

- E2E-036/045 falharam por corrida: reload/goto antes de sessão/saída. Testes agora esperam `user-name` / `btn-entrar`.

## Ready for Next Run

- Playwright 20/20 (UT-071/072 + E2E atribuídos + regressão catálogo/carrinho/checkout/pedidos/retaguarda), 2026-08-30.
- `eng-ui-screenshot` ausente: gate visual do companion omitido.
- Próxima detect-phase deve ser `task_03` (carrossel / busca / carregar mais).
