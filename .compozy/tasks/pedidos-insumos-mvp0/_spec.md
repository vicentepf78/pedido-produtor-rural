# Parte I — Produto

## Visão geral

O MVP0 comprova que um produtor rural pode fazer um pedido simples de insumos
agrícolas muito mais rapidamente do que na jornada atual, dependente de ERP. Ele
atende uma revenda e seus produtores, ao mesmo tempo que oferece a um operador da
revenda visibilidade suficiente para acompanhar o resultado.

O produto reduz o atrito ao adiar a identificação para o checkout, limitar o
catálogo inicial a 30 produtos não regulamentados selecionados e exibir uma
confirmação imediata do pedido.

## Objetivos

- Permitir que um produtor encontre um produto elegível navegando ou pesquisando por
  nome.
- Permitir que um produtor crie e altere um carrinho antes de criar uma conta.
- Permitir que um produtor confirme um pedido após fornecer somente uma conta, uma
  propriedade e uma preferência de retirada.
- Exibir um resultado compreensível de pedido recebido em até um segundo após a
  confirmação.
- Permitir que um operador autorizado da revenda inspecione os pedidos recebidos e
  sua confirmação subsequente.

## Histórias de usuário

- US-001 — US-002: descoberta no catálogo e montagem do carrinho.
- US-003 — US-004: identificação tardia, checkout e confirmação.
- US-005: inspeção de pedidos pela revenda.

[Histórias de usuário completas](_user_stories.md)

## Funcionalidades principais

### Catálogo de produtos selecionados

O produtor navega ou pesquisa os 30 produtos selecionados para a prova de conceito.
Cada item mostra as informações necessárias para selecionar uma quantidade e está
disponível para inclusão ou visivelmente indisponível. O catálogo não contém nenhum
produto regulamentado de proteção de cultivos.

### Carrinho e checkout rápidos

O produtor pode adicionar, ajustar e remover produtos antes da identificação. O
checkout coleta uma conta, uma propriedade e uma preferência de retirada. O produtor
vê um total simples pelo preço unitário; ele não simula condições de pagamento.

### Pedido local e confirmação

Uma confirmação válida cria um pedido local e apresenta imediatamente “Pedido
recebido”. O sistema registra a confirmação simulada bem-sucedida separadamente do
pedido.

### Backoffice mínimo

O operador da revenda pode listar e inspecionar os pedidos enviados, incluindo a
confirmação registrada. Administração de catálogo, aprovação de vendas, estoque,
entrega e manutenção comercial de preços não fazem parte desta prova de conceito.

## Regras de negócio

1. O MVP0 possui uma empresa comercial configurada e uma única loja visível.
2. Somente o conjunto selecionado de 30 produtos não regulamentados pode aparecer no
   catálogo ou ser pedido.
3. Um item do carrinho deve ter uma quantidade válida e positiva.
4. O preço unitário do produto e o total do pedido exibidos na confirmação passam a
   fazer parte do registro local do pedido.
5. Um produtor deve se identificar, selecionar uma propriedade própria e selecionar
   uma preferência de retirada antes de confirmar.
6. Repetir uma solicitação de confirmação para o mesmo checkout não deve criar um
   segundo pedido.
7. Um produtor pode visualizar somente seus próprios pedidos.
8. Um operador da revenda pode visualizar pedidos da empresa configurada.
9. A confirmação simulada é sempre bem-sucedida no MVP0.

## Experiência do usuário

A jornada principal é:

1. Abrir a loja.
2. Navegar ou pesquisar produtos.
3. Adicionar produtos e quantidades ao carrinho.
4. Iniciar o checkout.
5. Entrar ou criar uma conta.
6. Selecionar uma propriedade e uma preferência de retirada.
7. Confirmar e ver “Pedido recebido”.
8. Reabrir o pedido posteriormente para ver o status da confirmação.

Usar rótulos curtos, atualizações imediatas do total, progresso visível, navegação
por teclado, controles adequados ao toque, contraste legível e mensagens de erro
claras. A jornada deve poder ser utilizada sem treinamento prévio.

A jornada de backoffice limita-se a listar e abrir pedidos.
O [mapa de alterações de UI](_uiux.md) definirá os estados de tela após a confirmação
desta parte de Produto.

## Restrições técnicas de alto nível

- Uma confirmação não deve depender de disponibilidade externa para criar o pedido
  local.
- O produto deve manter conceitualmente separados a identidade e o registro comercial
  de cliente da revenda.
- O sistema deve suportar uma empresa agora sem impedir o isolamento futuro entre
  empresas.
- Um operador deve poder inspecionar os resultados de confirmação fora da jornada do
  produtor.
- Proteger informações de conta e pedido contra acesso não autorizado.
- Extensões de terceiros não fazem parte do MVP0.

## Não objetivos (fora do escopo)

- Vender ou confirmar produtos regulamentados de proteção de cultivos.
- Prescrição agronômica, documentação fiscal, reserva de estoque, seleção de lote,
  fluxos de pagamento, entrega ou crédito.
- Condições de preço, tabelas de preço, descontos ou simulação de parcelamento.
- Funcionalidades de backoffice para manutenção de catálogo, envio de mídia,
  notificações e cadastro de produtores.
- Múltiplas empresas comerciais visíveis, conexões reais com ERP, brokers e
  infraestrutura assíncrona.
- Aplicativos móveis nativos.

## Questões em aberto

Nenhuma. O limite inicial do produto está definido. A validação legal e comercial de
produtos regulamentados pertence ao MVP piloto posterior, não ao MVP0.

# Parte II — Técnico

## Resumo executivo

O MVP0 é um monólito modular Java 21 Spring Boot combinado com uma aplicação React
mobile-first. O Spring Modulith verifica os limites dos módulos de negócio; o
PostgreSQL armazena dados pertencentes aos módulos por meio de migrações Flyway. O
produto começa com um tenant semeado, mas todo registro comercial carrega `idTenant`.

O desenho mantém o pedido local como fonte de autoridade. Um `GatewayErpSimulado`
síncrono aceita o pedido depois que ele é persistido. Ele fornece a confirmação
exibida pela prova de conceito e torna explícito o futuro limite com o ERP.

## Limite do MVP

O MVP0 abrange as tarefas 01–06: wireframes de baixa fidelidade, fundação da
aplicação, contexto de identidade e tenant, catálogo selecionado e carrinho, checkout
e pedido local, e backoffice mínimo com Mock ERP. Agrofit, produtos regulamentados,
precificação comercial, adaptadores reais de ERP, notificações e todos os fluxos do
MVP piloto são pós-MVP. As tarefas de QA 07–08 encerram o MVP0.

## Experiência do desenvolvedor

- [Contrato de experiência do desenvolvedor](_dx.md) — API de navegador para
  catálogo, carrinhos, autenticação, pedidos do produtor e pedidos de backoffice.
- [Mapa de alterações de UI](_uiux.md) — superfícies mobile-first e wireframes
  obrigatórios.

## Arquitetura do sistema

| Componente | Responsabilidade | Limite |
| --- | --- | --- |
| `tenant` | Resolve a revenda configurada e delimita os registros comerciais. | É proprietário da identidade do tenant. |
| `identity` | Cadastra produtores, autentica sessões e atribui papéis. | Não é proprietário de propriedades de produtores. |
| `producer` | É proprietário do perfil e das propriedades do produtor. | Lê a identidade autenticada somente por seu contrato público. |
| `catalog` | É proprietário dos produtos selecionados e da disponibilidade. | Nunca expõe produtos regulamentados como passíveis de pedido. |
| `cart` | É proprietário de um carrinho de convidado ou produtor e recalcula totais a partir dos preços dos produtos. | Não cria pedidos. |
| `order` | É proprietário de snapshots imutáveis de pedidos e do acesso do produtor aos pedidos. | Não chama DTOs de fornecedores. |
| `integration` | Implementa a confirmação do Mock ERP. | Depende somente da porta pública de `order`. |
| `backoffice` | Lê resumos de pedidos delimitados por tenant para operadores da revenda. | Não altera catálogo ou pedidos no MVP0. |

A raiz de composição da API conecta módulos e adaptadores. Os módulos de
funcionalidade do frontend são `catalog`, `cart`, `checkout`, `my-orders` e
`backoffice-orders`.

## Limites arquiteturais

- Cada módulo de backend usa pacotes `domain`, `application`, `api` e
  `infrastructure` sob sua raiz de módulo.
- Somente contratos `application` declarados públicos por um módulo podem ser
  importados por outro módulo.
- Um módulo não deve importar entidades JPA, repositórios, migrações de banco de
  dados ou adaptadores de infraestrutura de outro módulo.
- `order` define `GatewayErp`; somente `integration` o implementa.
- `cart` lê dados de produto por consultas públicas de `catalog` e cria um pedido por
  comandos públicos de `order`.
- O módulo da aplicação Spring Boot é a única raiz de composição.
- Os módulos de funcionalidade React usam os contratos da API e não podem duplicar
  regras de precificação ou autorização.

## Desenho da implementação

### Interfaces principais

```java
public interface ConsultaCatalogo {
    Pagina<ResumoProduto> listarProdutosVisiveis(BuscaProduto busca, Paginacao pagina);
    ProdutoParaCarrinho exigirProdutoPedivel(UUID idTenant, IdProduto idProduto);
}
```

```java
public interface ComandoPedido {
    ConfirmacaoPedido criar(CriarPedido comando);
    VisaoPedido obterParaProdutor(IdUsuario idUsuario, IdPedido idPedido);
}
```

```java
public interface GatewayErp {
    ConfirmacaoErp aceitar(PedidoLocal pedido);
}
```

### Modelos de dados

| Entidade | Campos e justificativa |
| --- | --- |
| Tenant | `"id" UUID` identifica a revenda; `"nome" TEXT` identifica a loja; `"ativo" BOOLEAN` impede o uso de tenant desabilitado. |
| Usuario | `"id" UUID` identifica o login; `"email" TEXT` é único; `"senhaHash" TEXT` protege as credenciais; `"papel" TEXT` controla o acesso de produtor/operador. |
| Produtor | `"id" UUID` é o perfil comercial; `"idTenant" UUID` delimita o perfil; `"idUsuario" UUID` vincula a identidade. |
| Propriedade | `"id" UUID` identifica a propriedade; `"idProdutor" UUID` comprova a titularidade; `"nome" TEXT` permite a seleção no checkout. |
| Produto | `"id" UUID` identifica o conteúdo selecionado; `"idTenant" UUID` delimita a visibilidade; `"nome" TEXT`, `"descricaoCurta" TEXT`, `"categoria" TEXT`, `"unidade" TEXT`, `"precoUnitario" NUMERIC`, `"disponivel" BOOLEAN` e `"regulado" BOOLEAN` permitem descoberta e elegibilidade. |
| Carrinho | `"id" UUID` identifica o carrinho de convidado ou usuário; `"idTenant" UUID` isola o contexto comercial; `"chaveProprietario" TEXT` associa um navegador ou usuário. |
| Item do carrinho | `"idCarrinho" UUID`, `"idProduto" UUID`, `"quantidade" INTEGER` e `"precoUnitario" NUMERIC` fornecem uma linha atual do carrinho. |
| Pedido | `"id" UUID`, `"idTenant" UUID`, `"idProdutor" UUID`, `"idPropriedade" UUID`, `"preferenciaRetirada" TEXT`, `"situacao" TEXT`, `"confirmacao" TEXT`, `"total" NUMERIC` e datas preservam a transação confirmada. |
| Item do pedido | `"idPedido" UUID`, campos de snapshot do produto, quantidade, preço unitário e total da linha preservam o resultado da confirmação. |

Usar tabelas relacionais e chaves estrangeiras para cada entidade associável e estado
do ciclo de vida. Usar um campo `"metadadosJson" JSONB` somente para metadados
diagnósticos opacos e não consultáveis; o JSON não deve conter estado de autorização,
preço, disponibilidade ou pedido. Os itens do pedido são uma tabela auxiliar, não um
array JSON, pois totais e histórico devem permanecer consultáveis e auditáveis.

### Endpoints da API

A implementação fornece as rotas e os contratos de resposta/erro congelados em
`_dx.md`. Os handlers de requisição delegam a comandos da aplicação, derivam tenant e
ator do contexto autenticado, validam a entrada e nunca expõem hashes de senha ou
erros internos de adaptadores.

## Pontos de integração

`integration` contém somente `GatewayErpSimulado` no MVP0. Ele aceita um pedido local
persistido e retorna uma confirmação aceita de forma síncrona. Nenhuma conexão de
rede, segredo de provedor, loop de retentativa ou message broker é introduzido. Um
futuro adaptador de ERP real substitui essa implementação atrás da mesma porta.

## Análise de impacto

| Componente | Tipo de impacto | Descrição e risco | Ação necessária |
| --- | --- | --- | --- |
| Aplicação de backend | Novo | Módulos e esquemas de banco de dados greenfield. | Implementar testes de propriedade de módulos e migrações. |
| Aplicação web | Novo | Cinco superfícies mobile-first de produtor/operador. | Implementar a partir dos wireframes de baixa fidelidade aprovados. |
| Contrato de API | Novo | O cliente de navegador depende de rotas congeladas. | Cobrir todas as rotas e erros em testes de integração. |
| Integrações externas | Novo | Limite de confirmação somente mock. | Testar a porta com um adaptador determinístico. |

Nenhum fallback, shim de compatibilidade ou caminho de placeholder é necessário,
porque esta é uma implementação greenfield.

## Plano de integração para extensibilidade

- Manifestos de extensão, hooks, skills/capacidades, tools/resources, registros,
  SDKs de bridge, sidecars MCP e documentação de protocolo não são afetados: o MVP0
  entrega uma aplicação de produto independente, não uma extensão do Compozy.
- `GatewayErp` é o único ponto de integração futura planejado. Ele impede que modelos
  específicos de ERP entrem nos módulos de negócio.

## Plano de gerenciabilidade por agentes

Agentes podem inspecionar o comportamento do produto pelas rotas HTTP documentadas em
`_dx.md`, logs da aplicação, status de integridade e lista de pedidos de backoffice.
Os códigos de resposta determinísticos de `_dx.md` são o contrato de erro visível ao
consumidor. Nenhuma API CLI, UDS ou exclusiva para agentes é necessária, pois o MVP é
operado por suas superfícies web e logs padrão de implantação.

## Ciclo de vida da configuração

A configuração da aplicação inclui uma identidade de tenant semeada, conexão de banco
de dados, configurações de segurança de sessão e o perfil ativo de `GatewayErpSimulado`.
Variáveis de ambiente fornecem segredos e valores específicos do ambiente; nenhum
segredo é versionado. A validação de configuração falha a inicialização quando faltam
valores obrigatórios. Nenhum `config.toml`, extensão, documentação de site gerada ou
configuração de provedor é adicionado no MVP0, pois nenhum provedor externo é
invocado.

## Abordagem de testes

Usar JUnit para regras de domínio e aplicação, testes de integração apoiados por
PostgreSQL para migrações e conexão da API, testes Spring Modulith para limites e
Playwright para jornadas de navegador mobile-first. Usar apenas dados de fixture
fixos; fakes são permitidos na porta de I/O do Mock ERP, nunca dentro do
comportamento de order, cart ou catalog.

## Sequenciamento de desenvolvimento

### Ordem de construção

1. Criar wireframes de baixa fidelidade e aprovar o contrato visual.
2. Estruturar a aplicação modular, esquemas, migrações, verificações de integridade e
   testes de arquitetura.
3. Implementar contexto de tenant, identidade, produtor e propriedades.
4. Implementar catálogo selecionado e carrinho de convidado.
5. Implementar checkout, confirmação idempotente de pedido local e histórico do
   produtor.
6. Implementar confirmação do Mock ERP e backoffice somente leitura.
7. Produzir e executar o plano de QA.

### Dependências técnicas

Os wireframes devem existir antes da implementação do frontend. O catálogo e o
carrinho dependem do contexto de tenant; o checkout depende de identidade,
propriedade do produtor e carrinho; a confirmação do pedido depende de um pedido
local persistido. Nenhuma conta externa ou credencial de provedor bloqueia o MVP0.

## Monitoramento e observabilidade

Registrar ID de correlação estruturado da requisição, ID do tenant, ID do usuário, ID
do pedido, nome do evento e duração; omitir senhas e dados pessoais. Capturar
contadores de alterações de itens do carrinho, pedidos confirmados, confirmações
aceitas e requisições de acesso negado. Medir atualizações do carrinho e confirmação
do pedido em relação às metas de resposta do MVP0.

## Considerações técnicas

### Decisões principais

- Um `idTenant` semeado existe agora para que o isolamento comercial não exija uma
  reescrita disruptiva quando outra revenda for adicionada.
- Os dados são carregados por migrações para fornecer a todos os ambientes o mesmo
  catálogo de 30 produtos.
- O Mock ERP é apenas síncrono porque é local e sempre aceita; o processamento
  assíncrono de ERP real permanece uma capacidade futura.

### Riscos conhecidos

- Wireframes de baixa fidelidade podem revelar vocabulário ausente para o produtor.
  Mitigação: validá-los com um produtor antes do trabalho em alta fidelidade.
- Preços de fixture não são promessas comerciais. Mitigação: identificar o MVP0 como
  uma prova de conceito e reter o preço somente como snapshot do pedido.
- O onboarding por senha pode adicionar atrito. Mitigação: medir a jornada de três
  minutos e considerar um fluxo futuro sem senha somente com evidências de usuários.

## Invariantes de segurança

1. Todo registro pertencente a tenant carrega o identificador de tenant da revenda
   configurada, e toda consulta filtra por ele.
2. Um produtor pode ler somente pedidos cujo produtor do pedido pertença ao usuário
   autenticado.
3. Um produto regulamentado ou indisponível não pode ser adicionado a um carrinho ou
   pedido.
4. Uma chave de idempotência de confirmação mapeia para no máximo um pedido local.
5. O snapshot do pedido é imutável após a confirmação.
6. O Mock ERP não pode criar, modificar ou excluir um pedido local.
7. Senhas e credenciais de sessão nunca aparecem em respostas, logs, fixtures ou
   memória da tarefa.

## Referências de arquivos

### Arquivos do repositório

- `agri_platform_mvp_arquitetura.md:1090-1420` — escopo-fonte do MVP0, meta de
  desempenho, justificativa do monólito modular e direcionamento do Mock ERP.
- `.specs/STATE.md` — decisões técnicas anteriores que orientam o MVP piloto
  posterior.
- `.specs/features/pedidos-insumos-mvp/prd.md` — escopo completo do piloto
  intencionalmente excluído do MVP0.
- `.specs/features/pedidos-insumos-mvp/tech-spec.md` — limites de módulos e
  integração a manter para incrementos futuros.
- `CLAUDE.md` — arquitetura, segurança e regras de fluxo de trabalho do projeto.
- `docs/_memory/standing_directives.md` — restrições imutáveis de entrega do MVP0.

### Fontes de desenho e análise

- `.compozy/tasks/pedidos-insumos-mvp0/_idea.md` — resumo e insumos da descoberta.
- `.compozy/tasks/pedidos-insumos-mvp0/_user_stories.md` — comportamento observável
  canônico e casos de borda.
- `.compozy/tasks/pedidos-insumos-mvp0/_dx.md` — contrato da API de navegador.
- `.compozy/tasks/pedidos-insumos-mvp0/_uiux.md` — inventário de telas e metas de
  wireframe.

## Premissas e padrões

- O tenant configurado está ativo e possui uma conta semeada de operador da revenda.
- O catálogo selecionado contém exatamente 30 produtos não regulamentados.
- Carrinhos de convidado persistem no mesmo navegador até a confirmação do pedido ou
  a limpeza dos dados do navegador.
- O total inicial do pedido é igual à soma dos preços unitários atuais dos produtos e
  não aplica condições de pagamento, descontos ou taxas de entrega.
- A confirmação mock sempre é bem-sucedida.
- Os textos voltados ao produtor estão em português brasileiro.

## Registros de decisão arquitetural

- [ADR-001: Validar pedido rápido com produtos não regulamentados](adrs/adr-001-fast-non-regulated-order-proof.md)
  — O MVP0 isola a hipótese de pedido rápido das vendas regulamentadas.
