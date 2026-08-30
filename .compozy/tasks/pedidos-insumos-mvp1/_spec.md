# Parte I — Produto

## Visão geral

O MVP 1 torna a loja da revenda tão rápida de usar quanto o produtor já
espera de um marketplace: achar o insumo por categoria ou pelo nome,
montar o pedido sem cadastro precoce e confirmar com poucos passos. O
MVP 0 provou o pedido local; esta versão tira o atrito da descoberta, do
login e do visual partido (atalhos no rodapé, categorias em abas).

Atende o produtor rural da loja única já configurada e o operador que
inspeciona os pedidos recebidos. A premissa continua a mesma: facilidade
e agilidade para gerar pedidos de venda de insumos agrícolas, sem
treinamento prévio.

## Objetivos

- Permitir que o produtor recorte o catálogo por um carrossel de
  categorias (Todos, Sementes, Fertilizantes, Correção) com foto de
  demonstração, sem ver produto regulamentado.
- Permitir que o produtor busque por nome **e** mantenha a categoria,
  com ação explícita de limpar os dois filtros.
- Mostrar 10 produtos por vez por padrão e deixar o produtor escolher
  15, 30 ou 50, avançando com Carregar mais — sem inventar SKUs além do
  conjunto selecionado de 30 não regulamentados.
- Manter catálogo e carrinho utilizáveis sem conta; oferecer Entrar e
  Criar conta com e-mail e senha no topo e no checkout; após sucesso,
  devolver o produtor à página de origem.
- Colocar Catálogo, Carrinho, Checkout e Pedido no topo (não no
  rodapé); após a entrada, mostrar identificação curta e Sair sem
  apagar o carrinho de convidado.
- Confirmar o pedido local com conta, propriedade (ainda só no
  checkout) e retirada, e mostrar “Pedido recebido” em até um segundo.
- Permitir que o operador liste e abra pedidos no mesmo visual da loja,
  com topo próprio de operação.

## Critérios de sucesso

- Um convidado acha um produto elegível pelo carrossel ou pela busca
  composta e adiciona ao carrinho sem criar conta.
- Um produtor entra pela tela própria, volta de onde veio e conclui o
  checkout sem cadastrar de novo.
- Um produtor sem conta toca em Pedido, identifica-se e vê só os
  próprios pedidos.
- Um operador autentica e inspeciona pedidos sem ver o topo de compra.
- Nenhum produto regulamentado aparece no carrossel nem na listagem.
- O preço unitário visível é o mesmo antes e depois de Entrar.

## Histórias de usuário

- US-001 — US-003: carrossel, busca composta, tamanho da lista e
  Carregar mais.
- US-004: carrinho de convidado.
- US-005 — US-006: Entrar / Criar conta e topo da loja.
- US-007 — US-008: checkout, “Pedido recebido” e meus pedidos.
- US-009: retaguarda no visual da loja, com topo de operador.

[Histórias de usuário completas](_user_stories.md)

## Funcionalidades principais

### Descoberta no catálogo

O produtor vê um carrossel horizontal de cards (foto de demonstração +
rótulo). As categorias visíveis são só as do conjunto atual, mais
Todos. A busca por nome permanece na mesma página. Categoria e busca
somam. Limpar filtros restaura Todos e esvazia a busca. A listagem não
é um feed que cresce sozinho: o produtor escolhe o tamanho do bloco e
aciona Carregar mais.

### Identidade visível, ainda tardia para comprar

O catálogo e o carrinho não exigem conta. O topo oferece Entrar. Há
telas próprias de Entrar e Criar conta (e-mail e senha). O checkout
repete essa identificação se ela ainda não ocorreu. Recuperação de
senha e entrada com provedor externo não fazem parte desta versão.

### Topo da loja e topo da operação

Na jornada do produtor, os atalhos de compra ficam no topo. Pedido sem
conta pede Entrar e devolve a meus pedidos. Checkout sem itens explica
a falta; o atalho permanece. O operador não compartilha esse topo: vê
pedidos da revenda, identificação e Sair.

### Carrinho, checkout e pedido local

O comportamento comercial do MVP 0 permanece: carrinho antes da conta,
preço unitário único, propriedade e retirada só no checkout, pedido
local imediato, confirmação simulada sempre bem-sucedida, operador só
inspeciona.

### Visual único

Loja e retaguarda usam o mesmo visual desta versão. O mapa tela a tela
fica em `_uiux.md` depois que esta Parte I for confirmada e o workspace
de design da loja estiver definido.

## Regras de negócio

1. Continua uma empresa comercial configurada e uma única loja visível.
2. Somente o conjunto selecionado de 30 produtos não regulamentados
   pode aparecer no catálogo ou ser pedido. Defensivos não entram no
   carrossel.
3. Fotos dos cards de categoria são de demonstração; não representam
   acervo oficial da revenda e não há envio de mídia nesta versão.
4. Filtros de categoria e de nome somam. Limpar filtros volta a Todos e
   busca vazia.
5. Tamanhos de bloco permitidos: 10 (padrão), 15, 30 e 50. Carregar
   mais acrescenta o próximo bloco. Trocar o tamanho recomeça a lista.
6. Item de carrinho exige quantidade válida e positiva. Adicionar de
   novo o mesmo produto junta quantidade.
7. O preço unitário e o total exibidos na confirmação passam a fazer
   parte do registro local do pedido. O preço não muda após Entrar.
8. Confirmar exige identidade de produtor, propriedade própria e
   preferência de retirada. Propriedade não tem área própria fora do
   checkout.
9. Repetir a confirmação do mesmo checkout não cria segundo pedido.
10. O produtor vê só os próprios pedidos. O operador vê os pedidos da
    empresa configurada. Papéis não trocam de topo.
11. Sair encerra a sessão e não apaga o carrinho de convidado.
12. Após Entrar ou Criar conta com papel de produtor, o destino é a
    origem (catálogo, carrinho, checkout ou meus pedidos). Conta de
    operador autenticada na Entrar da loja vai à retaguarda, com o
    topo de operação. Criar conta na loja cria somente papel de
    produtor.
13. A confirmação simulada é sempre bem-sucedida nesta versão.
14. Esqueci a senha não existe neste MVP.

## Experiência do usuário

Jornada principal do produtor:

1. Abrir a loja e ver o carrossel, a busca e a listagem (10 itens).
2. Escolher categoria e/ou nome; limpar se o recorte não servir.
3. Ajustar 10 / 15 / 30 / 50 e Carregar mais se precisar.
4. Adicionar quantidades ao carrinho sem conta.
5. Entrar pelo topo a qualquer momento, ou só no checkout.
6. Voltar à origem; no checkout, escolher propriedade e retirada.
7. Confirmar e ver “Pedido recebido”.
8. Reabrir Pedido no topo para o histórico próprio.

Jornada do operador:

1. Identificar-se como operador.
2. Ver o topo de operação.
3. Listar pedidos e abrir o detalhe no visual desta versão.

A loja deve funcionar sem treinamento: rótulos curtos, toque e teclado,
contraste legível, estados vazios com próxima ação, foco visível,
redução de movimento quando a pessoa pede menos animação.

O [mapa de alterações de UI](_uiux.md) será escrito após a confirmação
desta parte e após o desenho das telas no workspace de design.

## Restrições técnicas de alto nível

- A confirmação não depende de sistema externo para criar o pedido
  local.
- Identidade de acesso e ficha comercial do cliente da revenda
  permanecem conceitos distintos.
- Uma empresa agora, sem impedir isolamento futuro entre empresas.
- O operador inspeciona o resultado da confirmação fora da jornada do
  produtor — inclusive sem usar a interface da loja, por um canal de
  operação que liste o mesmo pedido e o status da confirmação.
- Conta e pedido não são visíveis a quem não tem o papel correspondente.
- Extensões de terceiros não participam deste MVP: nenhum ponto de
  extensão de vitrine, autenticação ou catálogo é aberto a parceiros.

## Não objetivos (fora do escopo)

- Vender ou confirmar produtos regulamentados de proteção de cultivos.
- Entrar com Google ou outro provedor externo.
- Recuperação de senha.
- Preço diferenciado após o login, tabela, desconto ou parcelamento.
- Área “minha conta” além de Entrar, Criar conta e Sair.
- Cadastro ou edição de propriedades fora do checkout.
- Scroll infinito automático (a lista só cresce com Carregar mais).
- Ampliar o conjunto de SKUs além dos 30 selecionados.
- Prescrição, documento fiscal, reserva de estoque, lote, pagamento,
  entrega ou crédito.
- Backoffice de importar catálogo, cadastrar produtor, mídia oficial,
  notificação ou acompanhamento de ERP real.
- Várias empresas visíveis na mesma loja.
- Aplicativos nativos.

## Questões em aberto

1. **Ajustes finos do visual.** O OpenDesign já entregou
   `loja-insumos-agricolas.html` (carrossel, topo, login, carrinho,
   checkout, pedido e retaguarda). Paleta e fotos SVG de demonstração
   estão no artefato; você pode pedir mudança de cor, card ou cópia
   antes de congelar em `docs/design/opendesign/pedidos-insumos-mvp1/`.

Decisões já registradas: `adrs/adr-001` a `adrs/adr-007`.

---

# Part II — Técnico

## Resumo executivo

O MVP 1 evolui o monólito modular Java 21 Spring Boot e a aplicação
React já entregues no MVP 0. Não cria módulos novos. O catálogo passa a
filtrar categoria e paginar nos tamanhos 10 / 15 / 30 / 50; a identidade
ganha leitura de sessão e telas `/entrar` e `/cadastro`; o shell move a
navegação para o topo e separa o topo do operador. O pedido local, o
Mock ERP síncrono e o recorte de 30 produtos não regulamentados
permanecem.

## Limite do MVP (MVP boundary)

MVP boundary: tasks 01-04 implement the storefront catalog contract,
session, top navigation, login screens, discovery UI, checkout/orders
and operator backoffice visual. Tasks 05-06 are qa-report and
qa-execution. Post-MVP / out of scope: Google, recuperação de senha,
preço negociado, Agrofit, ERP real, outbox, defensivos, importação de
catálogo e área de conta.

## Experiência do desenvolvedor

- [Contrato de experiência do desenvolvedor](_dx.md) — sessão, catálogo
  com `categoria` e `tamanhoPagina` fechado, autenticação, carrinho,
  pedidos, retaguarda.
- [Mapa de alterações de UI](_uiux.md) — S1–S11 e artefatos OpenDesign.

## Arquitetura do sistema

| Componente | Responsabilidade | Limite |
| --- | --- | --- |
| `tenant` | Revenda configurada. | Sem mudança de contrato. |
| `identity` | Cadastro PRODUTOR, entrada, saída, **sessão atual** e papéis. | Não dono de propriedade. |
| `producer` | Propriedades do produtor. | Só no checkout. |
| `catalog` | Listagem visível com categoria + busca + página. | Nunca regula/defensivo. |
| `cart` | Carrinho de convidado. | Sem pedido. |
| `order` | Pedido local e histórico do produtor. | Porta `GatewayErp`. |
| `integration` | `GatewayErpSimulado`. | Só a porta de `order`. |
| `backoffice` | Leitura para operador. | Sem mutação de pedido. |
| frontend `shell` | `TopoLoja` / `TopoOperador`; remove `bottom-nav`. | Sem regra de preço. |
| frontend `identity` | `/entrar`, `/cadastro`, retorno `origem`. | Lê `papeis` da sessão. |
| frontend `catalog` | Carrossel, tamanho, Carregar mais. | Usa só a API de `_dx.md`. |

A raiz de composição continua `br.agriplataforma.AgriPlatformApplication`.

## Limites arquiteturais (Architectural Boundaries)

- Cada módulo backend permanece `domain` / `application` / `api` /
  `infrastructure` sob `br.agriplataforma.<modulo>`.
- Outro módulo importa somente o contrato `application` público.
- Sem acesso cruzado a entidade JPA, repositório, tabela ou DTO de
  fornecedor.
- `catalog` e `cart` continuam CLOSED com `allowedDependencies` atuais.
- `identity` continua dono da matriz HTTP (`permitAll` catálogo/carrinho/
  csrf/cadastro/entrada/saida/sessao; `PRODUTOR` produtor e pedidos;
  `OPERADOR_REVENDA` retaguarda).
- `order` define `GatewayErp`; só `integration` implementa.
- Nenhum pacote interno novo. Nenhum `daemon/` neste produto — a raiz
  de composição é o módulo Spring Boot, não um daemon Compozy.
- Features React não duplicam elegibilidade, preço nem autorização.

## Desenho da implementação

### Interfaces principais

A linguagem de produção é Java. O bloco Go abaixo existe para o marcador
de qualidade da spec; as assinaturas Java são as autoritativas.

```java
public interface ConsultaCatalogo {
    Pagina<ResumoProduto> listarProdutosVisiveis(BuscaProduto busca, Paginacao pagina);
    ProdutoParaCarrinho exigirProdutoPedivel(UUID idTenant, IdProduto idProduto);
}

public record BuscaProduto(String consulta, String categoria) {}

public record Paginacao(int pagina, int tamanhoPagina) {
    public static final int PADRAO = 10;
    public static Set<Integer> tamanhosPermitidos() {
        return Set.of(10, 15, 30, 50);
    }
}

public interface ConsultaIdentidade {
    Optional<UsuarioAtual> usuarioAtual();
    UsuarioAtual exigirAutenticado();
}

public interface ComandoIdentidade {
    UsuarioAtual cadastrar(Cadastro comando);
    UsuarioAtual entrar(Credenciais credenciais);
}
```

```go
type ConsultaCatalogo interface {
    ListarProdutosVisiveis(busca BuscaProduto, pagina Paginacao) (Pagina, error)
    ExigirProdutoPedivel(idTenant string, idProduto string) (ProdutoParaCarrinho, error)
}

type ConsultaIdentidade interface {
    UsuarioAtual() (UsuarioAtual, bool)
    ExigirAutenticado() (UsuarioAtual, error)
}

type ComandoIdentidade interface {
    Cadastrar(cmd Cadastro) (UsuarioAtual, error)
    Entrar(cred Credenciais) (UsuarioAtual, error)
}
```

`BuscaProduto.categoria` vazia ou `Todos` não filtra. `Paginacao` rejeita
tamanho fora de `{10,15,30,50}` com `TAMANHO_PAGINA_INVALIDO` **antes**
de consultar o repositório. `consulta` em branco não força página vazia.

Não há endpoint de categorias: o carrossel é conjunto fechado no cliente
com mídia estática de demonstração em `frontend/public/media/categorias/`.

`GET /api/v1/autenticacao/sessao` lê `ConsultaIdentidade.usuarioAtual()`.

### Modelos de dados

Nenhuma tabela nova. Nenhuma coluna nova obrigatória.

| Name | Shape | Rationale |
| --- | --- | --- |
| Produto.categoria | TEXT | Já existe; passa a ser filtro de `BuscaProduto`, não só campo de resposta. |
| Produto.regulado | BOOLEAN | Continua excluindo o item da listagem e do pedido. |
| Usuario.papel | TEXT | Decide destino pós-Entrar e o topo (PRODUTOR vs OPERADOR_REVENDA). |
| Carrinho.chaveProprietario | TEXT | Sobrevive a Sair. |
| Pedido.chaveIdempotencia | TEXT | Um pedido por chave no tenant+produtor. |

Filtro de categoria e tamanho de página são parâmetros de consulta, não
colunas novas. Fotos de categoria não vão ao banco.

### Decisão side-table vs JSON

Nenhuma entidade nova. Pedido e item de pedido continuam **side-tables**
relacionais (consultáveis na retaguarda). JSON/`metadadosJson` só para
diagnóstico opaco — nunca categoria, preço, papel, página ou
autorização. Preferências de tamanho de lista e filtros ficam no cliente
(query da página), não em JSON de usuário.

### Endpoints da API

Handlers servem `_dx.md`. Novos ou alterados nesta versão:

- `GET /api/v1/autenticacao/sessao` — `permitAll`; corpo documentado.
- `GET /api/v1/catalogo/produtos` — aceita `categoria`; default
  `tamanhoPagina=10`; conjunto fechado de tamanhos; busca em branco lista
  o recorte (quebra o comportamento do MVP 0 que devolvia página vazia).
- Entrada/cadastro passam a devolver `email` além de `nome` e `papeis`
  para o topo.

Sem fallback, shim ou “se o cliente antigo mandar 24”. Cliente MVP 0 que
enviar `tamanhoPagina=24` recebe `TAMANHO_PAGINA_INVALIDO`. Delete target:
filtro de categoria só no cliente e a `bottom-nav` em `LayoutApp.tsx`.

## Pontos de integração

Inalterados: só `GatewayErpSimulado` atrás de `GatewayErp`. Sem rede de
ERP, sem broker, sem provedor de identidade externo.

## Análise de impacto

| Component | Impact Type | Description and Risk | Required Action |
| --- | --- | --- | --- |
| `catalog` application + API | modified | Filtro e paginação viram contrato. Risco: listagem vazia se `consulta` em branco continuar no código atual. | Alterar `BuscaProduto`/`Paginacao` e ITs. |
| `identity` API | modified | Sessão explícita. Risco: topo sem papel. | `GET sessao` + resposta de entrada com e-mail. |
| frontend shell | modified | Troca de nav. Risco: operador no topo de compra. | `TopoLoja` / `TopoOperador`. |
| frontend catalog | modified | Carrossel e Carregar mais. | Paginar de verdade. |
| frontend identity | new | `/entrar`, `/cadastro`. | Destino por `papeis` e `origem`. |
| retaguarda UI | modified | Visual + topo. | Sem nova API. |
| `web/` Compozy, `config.toml` | unaffected | Este repositório não é o daemon Compozy. | Sem mudança. |

Delete targets no mesmo merge: `bottom-nav` e o fetch único
`tamanhoPagina=30` sem `categoria`. No fallback / no compat shim / no
placeholder para tamanho 24 ou abas geradas a partir de Defensivos.

## Plano de integração para extensibilidade

Manifestos de extensão, hooks, skills, tools/resources, registros, SDKs
de bridge, sidecars MCP e docs de protocolo do Compozy **não são
afetados**: esta spec entrega a loja do produto, não uma extensão do
runtime Compozy. Superfícies checadas: nenhum `packages/`,
`config.toml` de Compozy, nem MCP sidecar no repo. `GatewayErp`
permanece o único ponto futuro de provedor.

## Plano de gerenciabilidade por agentes

Agentes operam pelas rotas HTTP de `_dx.md` (`/actuator/health`,
catálogo, sessão, retaguarda) e pelos códigos de erro determinísticos.
Sem CLI, UDS ou tools `compozy__*` neste produto. UI-only é incompleto:
a retaguarda HTTP e os logs estruturados (idTenant, idPedido, sem
senha) são o canal fora da loja.

## Ciclo de vida da configuração

`config.toml` do Compozy: **não afetado** (não existe neste repo).
Configuração que permanece: `agriplataforma.idTenantSemeado`, cookie
`sessao`, `agriplataforma.gatewayErp=simulado`, datasource. Nenhuma
chave nova obrigatória. Segredos só em ambiente. Validação atual de
boot permanece. Sem docs de site gerados.

## Abordagem de testes

Estratégia: JUnit 5 no domínio/aplicação; IT com PostgreSQL
(Testcontainers) para API e migração; Spring Modulith para limites;
Playwright nas jornadas S1–S11. Casos concretos em `_tests.md`. Fake só
na porta `GatewayErp`. Fixtures: 30 produtos + 1 regulado oculto; contas
Alfa, Beta e operador.

## Sequenciamento de desenvolvimento

### Ordem de construção

1. Congelar artefatos OpenDesign em `docs/design/opendesign/pedidos-insumos-mvp1/`.
2. `ConsultaCatalogo` + API de categoria/tamanho + ITs (quebra consciente do vazio em busca em branco).
3. `GET sessao` + e-mail na resposta de entrada/cadastro.
4. Shell: topo da loja, remover bottom-nav, `/entrar` e `/cadastro`.
5. Catálogo: carrossel, limpar filtros, Carregar mais.
6. Checkout e meus pedidos no visual novo; destino do operador.
7. Retaguarda no visual novo com topo próprio.
8. QA report + execução.

### Dependências técnicas

Artefato OpenDesign antes do polish visual. Catálogo paginado antes do
Carregar mais. Sessão antes do topo com nome. Nenhuma conta de provedor
externo.

## Monitoramento e observabilidade

Mesmos eventos do MVP 0, mais: listagem com `categoria`/`tamanhoPagina`,
entrada com papel, redirecionamento de operador, Carregar mais
(`pagina`). Sem senha, e-mail completo em log ou memória de task.

## Considerações técnicas

### Decisões principais

- Filtrar categoria no servidor para a página 2 não misturar recortes.
- Conjunto fechado de tamanhos para não reabrir o default 24/100.
- Sessão explícita para não inferir login só por `GET propriedades`
  (que falha para operador).
- Mídia de categoria no frontend estático para não inventar upload.

### Riscos conhecidos

- Cliente ou teste antigo com `tamanhoPagina=24` quebra. Mitigação:
  contrato em `_dx.md` e ITs novas; sem shim.
- OpenDesign atrasar o visual. Mitigação: comportamento em `_uiux.md`
  independe da paleta final.
- Operador na Entrar da loja. Mitigação: ADR-007 + `papeis` na resposta.

## Safety Invariants

1. Todo registro comercial carrega `idTenant` da revenda configurada e
   toda consulta filtra por ele.
2. Produtor lê só os próprios pedidos.
3. Operador lê pedidos do tenant e nunca confirma pedido de produtor.
4. Produto regulamentado ou indisponível não entra em listagem pedível,
   carrinho ou pedido.
5. Uma chave de idempotência mapeia para no máximo um pedido local.
6. Snapshot do pedido é imutável após confirmar.
7. Mock ERP não cria, altera nem apaga pedido local.
8. Senha e cookie de sessão não aparecem em resposta JSON, log, fixture
   documentada como segredo de produção, nem memória de task.
9. Sair invalida a sessão e não apaga o carrinho de convidado.
10. `origem` aberta pelo cliente não aceita URL absoluta externa.

## Referências de arquivos

### Arquivos do repositório

- `backend/src/main/java/br/agriplataforma/catalog/application/ServicoCatalogo.java` — hoje `consulta` em branco vira página vazia; precisa listar o recorte.
- `backend/src/main/java/br/agriplataforma/catalog/application/Paginacao.java` — default 24 / máx. 100; fechar em 10 / 15 / 30 / 50.
- `backend/src/main/java/br/agriplataforma/catalog/application/BuscaProduto.java` — só `consulta`; incluir `categoria`.
- `backend/src/main/java/br/agriplataforma/catalog/api/CatalogoApi.java` — query params públicos.
- `backend/src/main/java/br/agriplataforma/identity/api/AutenticacaoApi.java` — csrf / cadastro / entrada / saida; falta sessao.
- `backend/src/main/java/br/agriplataforma/identity/infrastructure/ConfiguracaoSeguranca.java` — matriz de rotas.
- `backend/src/main/java/br/agriplataforma/identity/application/ServicoIdentidade.java` — cadastro sempre PRODUTOR.
- `frontend/src/shell/LayoutApp.tsx` — `bottom-nav` a remover.
- `frontend/src/App.tsx` — sem `/entrar` nem `/cadastro`.
- `frontend/src/features/catalog/PaginaCatalogo.tsx` — abas e fetch único.
- `frontend/src/features/catalog/api.ts` — `tamanhoPagina=30` fixo.
- `frontend/src/features/checkout/PaginaCheckout.tsx` — login inline.
- `frontend/src/features/backoffice-orders/PaginaPedidosRetaguarda.tsx` — login inline de operador.
- `.compozy/tasks/pedidos-insumos-mvp0/_dx.md` — contrato anterior a quebrar nos pontos documentados.
- `CLAUDE.md` — monólito, camelCase citado, sem inventar regra comercial.
- `docs/_memory/standing_directives.md` — SD-001–SD-012; SD-003/009/010 continuam no recorte.

### Fontes de desenho e análise

- `.compozy/tasks/pedidos-insumos-mvp1/_user_stories.md` — ACs e ECs.
- `.compozy/tasks/pedidos-insumos-mvp1/_dx.md` — superfície HTTP.
- `.compozy/tasks/pedidos-insumos-mvp1/_uiux.md` — S1–S11.
- `.compozy/tasks/pedidos-insumos-mvp1/adrs/` — ADR-001 a ADR-007.
- `docs/design/opendesign/pedidos-insumos-mvp1/loja-insumos-agricolas.html` — protótipo visual autoritativo (topo, carrossel, login, retaguarda).
- `docs/design/opendesign/pedidos-insumos-mvp1/brand-spec.md` — tokens de cor e tipo.
- `agri_platform_mvp_arquitetura.md` — origem do recorte; não puxar §32 backoffice amplo.
- Nenhuma fatia `.resources/<competitor>/`: o carrossel veio da referência visual anexada pelo usuário, não de um repo vendored.

## Premissas e padrões

- Tenant semeado ativo; contas fixture Alfa, Beta e operador.
- 30 produtos não regulamentados; 1 defensivo oculto.
- Carrinho de convidado no mesmo navegador até confirmar ou limpar dados.
- Total = soma dos preços unitários atuais, sem desconto.
- Mock ERP sempre aceita.
- Textos da loja em português brasileiro.

## Registros de decisão arquitetural

- [ADR-001](adrs/adr-001-recorte-loja-do-produtor.md) — recorte da loja.
- [ADR-002](adrs/adr-002-identificacao-convidado-e-tela-entrar.md) — convidado + Entrar.
- [ADR-003](adrs/adr-003-descoberta-por-categoria-busca-e-carregar-mais.md) — carrossel e Carregar mais.
- [ADR-004](adrs/adr-004-retaguarda-no-mesmo-visual.md) — retaguarda visual.
- [ADR-005](adrs/adr-005-retorno-pos-entrar-e-tamanho-da-lista.md) — origem e 10/15/30/50.
- [ADR-006](adrs/adr-006-topo-do-operador-e-midia-de-demonstracao.md) — topo operador.
- [ADR-007](adrs/adr-007-operador-na-entrar-da-loja.md) — operador vai à retaguarda.

