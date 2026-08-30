---
status: completed
title: Tenant, identidade, produtor e propriedades
type: backend
complexity: high
---

# Task 2: Tenant, identidade, produtor e propriedades

## Overview

Entrega o contexto comercial único, o cadastro/autenticação do produtor e o
cadastro de propriedades próprias. O checkout só pode existir depois que a
sessão, o papel e a titularidade da propriedade forem contratos públicos
estáveis.

<critical>
- ALWAYS READ `_spec.md` and its catalogs (`_user_stories.md`, `_dx.md`, `_uiux.md` when present, `_tests.md`) before starting
- REFERENCE `_spec.md` Part II for implementation details — do not duplicate here
- FOCUS ON "WHAT" — describe what needs to be accomplished, not how
- MINIMIZE CODE — show code only to illustrate current structure or problem areas
- TESTS REQUIRED — implement every test case assigned in ## Tests
</critical>

<requirements>
- MUST persistir Tenant, Usuario, Produtor e Propriedade com `idTenant` em
  todo registro comercial e filtrar toda consulta por ele (SD-007).
- MUST semear um tenant ativo, um operador da revenda e ao menos um produtor
  de fixture com propriedades próprias, sem dados pessoais reais (SD-008).
- MUST implementar `POST /api/v1/autenticacao/cadastro` conforme `_dx.md`
  (cookie de sessão seguro HttpOnly + CSRF; senha nunca na resposta ou log).
- MUST implementar entrada por e-mail e senha na mesma superfície
  `/api/v1/autenticacao`, porque US-003 exige entrar ou criar conta; se a rota
  de entrada ainda não estiver em `_dx.md`, atualizar `_dx.md` no mesmo
  entregável.
- MUST expor listagem das propriedades do produtor autenticado para o
  checkout; um produtor NÃO DEVE ver propriedade de outro.
- MUST distinguir papel `PRODUTOR` e papel de operador da revenda.
- MUST expirar sessão e exigir nova autenticação sem destruir o carrinho de
  convidado (UT-019).
- MUST rejeitar e-mail duplicado e credenciais inválidas sem vazar hash ou
  existência detalhada além do necessário para recuperação.
- MUST NOT criar catálogo, carrinho, pedido ou backoffice nesta tarefa.
</requirements>

## Subtasks

- [x] 2.1 Migrar e persistir Tenant, Usuario, Produtor e Propriedade com
      campos `camelCase` quoted.
- [x] 2.2 Semear tenant, operador e produtor/propriedades de fixture.
- [x] 2.3 Publicar cadastro, entrada, logout e cookie de sessão seguro.
- [x] 2.4 Publicar consulta de propriedades do produtor autenticado.
- [x] 2.5 Aplicar papéis e isolamento por `idTenant` em todas as consultas.
- [x] 2.6 Garantir que sessão expirada preserve o carrinho de convidado.
- [x] 2.7 Implementar UT-016, UT-017, UT-019 e IT-005.

## Implementation Details

Módulos `tenant`, `identity` e `producer` segundo `_spec.md` Parte II.
`identity` não é dono de propriedades; `producer` lê a identidade só pelo
contrato público. Sessão por cookie HttpOnly + CSRF, nunca JWT em
localStorage. Referência de DX: cadastro e sessão.

### Relevant Files

- `.compozy/tasks/pedidos-insumos-mvp0/_spec.md` — modelos Tenant, Usuario,
  Produtor, Propriedade e invariantes de segurança 1 e 7.
- `.compozy/tasks/pedidos-insumos-mvp0/_dx.md` — `POST /api/v1/autenticacao/cadastro`.
- `.compozy/tasks/pedidos-insumos-mvp0/_user_stories.md` — US-003 AC-1, EC-1, EC-4.
- `backend/src/main/java/br/agriplataforma/{tenant,identity,producer}/` — raízes
  criadas na task_01.
- `backend/src/main/resources/db/migration/{tenant,identity,producer}/` —
  migrações da fundação a complementar.

### Dependent Files

- `backend/src/main/java/br/agriplataforma/identity/api/` — cadastro, entrada,
  sessão.
- `backend/src/main/java/br/agriplataforma/producer/application/` — contrato
  público de propriedades.
- `backend/src/main/java/br/agriplataforma/tenant/application/` — resolução do
  tenant configurado.
- `backend/src/test/java/br/agriplataforma/identity/` — UT-016, UT-017, UT-019.
- `backend/src/test/java/br/agriplataforma/identity/api/` — IT-005.
- `.compozy/tasks/pedidos-insumos-mvp0/_dx.md` — completar rota de entrada se
  ausente.

### Related ADRs

- [ADR-001](adrs/adr-001-fast-non-regulated-order-proof.md) — identificação
  tardia faz parte da hipótese de pedido rápido.

### Web/Docs Impact

- `web/`: none — checked surfaces: `web/`; reason: cliente vive em `frontend/`.
  A task_04 consome estes contratos no checkout; esta tarefa não renderiza UI.
- `packages/site`: none — checked surfaces: `packages/site/`; reason: sem site
  Compozy neste repositório.
- QA impact: new scenarios — add content-addressed untested files
  `docs/qa/scenarios/AUTH-cadastro-e-sessao.md` (e sobreposição com
  `CHK-identificacao-tardia.md` quando o checkout existir). Reset para
  `untested` se o arquivo já existir.

### Extensibility / Agent Manageability / Config Lifecycle

- Extensibility: none — checked surfaces: hooks, MCP, registries; reason:
  identidade não é ponto de extensão no MVP0.
- Agent manageability: `POST /api/v1/autenticacao/cadastro`, rota de entrada
  pareada, cookie de sessão, erros determinísticos de credencial/duplicidade.
  Sem CLI/UDS. Agentes inspecionam via HTTP documentado em `_dx.md`.
- Config lifecycle: segredos de sessão e datasource só por ambiente;
  nenhum `config.toml`. Validação impede subida sem cookie secure settings em
  perfil não-local.

## Deliverables

- Tenant único semeado e isolado por `idTenant`.
- Cadastro, entrada e sessão por cookie seguro.
- Propriedades listáveis apenas pelo dono autenticado.
- Every test case assigned in `## Tests` implemented and passing **(REQUIRED)**

## Tests

Cases assigned from `_tests.md`, the test contract — read each ID's full definition there before writing tests.

- [x] UT-016, UT-017 — cadastro duplicado e credenciais inválidas sem expor senha.
- [x] UT-019 — sessão expirada exige autenticação e preserva carrinho de convidado.
- [x] IT-005 — cadastro define cookie seguro e o produtor seleciona somente
      propriedades próprias.

## Success Criteria

- Every assigned test case implemented and passing
- Hash de senha nunca aparece em resposta, log ou fixture
- Operador e produtor de fixture existem e estão delimitados ao tenant semeado
- Um produtor autenticado não lista propriedade de outro
