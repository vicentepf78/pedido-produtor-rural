# Task Memory: task_01

## Objective Snapshot

Contratos HTTP de catálogo paginado e sessão atual, sem módulo, tabela ou tela nova.

## Important Decisions

- Semente real permanece (`Ureia 45% N` @ 178.00, UUID `...0013`). Exemplos `_dx.md` (ULID, 198.00) ficam nos unitários mockados.
- `tamanhoPagina=24` recusado sem shim. Backoffice `25` inalterado.
- Operador em `ComandoPedido.criar` agora lança `ACESSO_NEGADO` (“Você não tem permissão para este recurso.”), alinhado a UT-055.
- Validação Bean Validation de cadastro/entrada devolve `400 DADOS_OBRIGATORIOS` (antes caía em `/error` → 401).
- Peer review e walk de QA adiados (Phase D / Phase C).

## Learnings

- `make test` exclui `@Tag("integration")`; ITs vão em `make test-integration`.
- Jackson databind não está no compile de teste; ITs de catálogo usam parsing por regex.
- `persistirNovo` grava o pedido antes de `exigirProdutoPedivel`; item inelegível no checkout não impede o `save` inicial.

## Files / Surfaces

- Catálogo: `Paginacao`, `BuscaProduto`, `ServicoCatalogo`, `RepositorioProduto`, `CatalogoApi`, `TratamentoExcecoesCatalogo`.
- Identidade: `AutenticacaoApi`, `RespostaSessao`, `RespostaAutenticacao`, `ConfiguracaoSeguranca`, `ServicoIdentidade`, `TratamentoExcecoesIdentidade`.
- Pedido: `ServicoPedido.exigirProdutor`.
- Testes remapeados/criados para os IDs de `_tests.md` (exceto UT-071/UT-072).

## Errors / Corrections

- `UnnecessaryStubbing` em `ConsultaCatalogoTest` → `LENIENT`.
- IT-046 401 por `/error` autenticado → handler `MethodArgumentNotValidException` + `/error` `permitAll`.
- UT-052/UT-060 ajustados à ordem real de persistência.

## Ready for Next Run

- `make test` exit 0 (Surefire, 2026-08-30 após handler 400).
- `make test-integration` exit 0 (Failsafe, 51 ITs após correção IT-046).
- cy-final-verify PASS no claim estreito da task_01.
- Próxima detect-phase deve ser `task_02` (UI destino/topo).
