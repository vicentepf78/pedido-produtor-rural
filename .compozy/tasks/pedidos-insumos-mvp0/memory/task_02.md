# Task Memory: task_02

## Objective Snapshot

Tenant, identidade, produtor e propriedades; UT-016, UT-017, UT-019, IT-005.
Auto-commit desligado; status do `task_02.md` permanece `in_progress`.

## Checklist

1. Migrar Usuario/Produtor/Propriedade camelCase quoted + idTenant — feito
2. Semear operador + 2 produtores com propriedades — feito
3. POST /api/v1/autenticacao/cadastro (cookie HttpOnly+Secure, CSRF) — feito
4. POST entrada/saida e GET csrf; _dx.md atualizado — feito
5. GET /api/v1/produtor/propriedades só do dono — feito
6. Papéis PRODUTOR vs OPERADOR_REVENDA; filtro idTenant — feito
7. Sessão expirada → 401; cookie chaveCarrinhoConvidado intacto — feito
8. UT-016/017/019 e IT-005; UT-026/IT-011 verdes — feito
9. QA scenario AUTH-cadastro-e-sessao.md untested — feito
10. Sem catálogo, carrinho, pedido ou backoffice — feito

## Important Decisions

- `nome` e `idTenant` em Usuario; `idTenant` também em Propriedade.
- API usa UUID. Produtor criado na primeira listagem de propriedades.
- Cookie de sessão: `sessao`. CSRF: GET `/api/v1/autenticacao/csrf` devolve `{token}` + cookie `XSRF-TOKEN`.
- Logout/expiração não apagam `chaveCarrinhoConvidado`.
- `UserDetailsServiceAutoConfiguration` excluída para não logar senha gerada.

## Learnings

- MockMvc não emite `Set-Cookie` da sessão; IT-005 usa servidor real (RANDOM_PORT).
- Spring Security 6 XOR no CSRF: o header precisa do token cru, não da cópia do cookie.

## Files / Surfaces

- Módulos `tenant`, `identity`, `producer`; `_dx.md`; `docs/qa/scenarios/AUTH-cadastro-e-sessao.md`

## Errors / Corrections

- Cookie `sessao` ausente no MockMvc → HttpClient + Tomcat.
- POST cadastro 403 CSRF → devolver token cru em GET /csrf.

## Ready for Next Run

- `cy-final-verify`: `make test` 8/0 e `make test-integration` 4/0.
- WebMvcTest excluía UserDetailsService e ainda logava senha gerada; `TestPropertySource` de exclude no `SessaoExpiradaTest`.
- QA walk adiado para Phase C. Peer-review adiado para Phase D.
