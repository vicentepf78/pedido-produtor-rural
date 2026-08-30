# Tech spec — MVP de pedidos digitais de insumos agrícolas

**Status:** aprovado para detalhamento de tarefas  
**Fonte de requisitos:** [PRD](prd.md)

## Arquitetura

O produto será um monólito modular: uma API Spring Boot com Spring Modulith e uma SPA
React independente. Não haverá comunicação entre serviços, broker nem integração com
ERP no MVP.

```text
React (Vite + TypeScript)
  └── HTTPS / REST + cookie HttpOnly
        └── Spring Boot + Spring Modulith
              ├── schemas PostgreSQL por módulo
              ├── Cloudinary (mídia)
              ├── Agrofit CSV diário (carga regulatória)
              ├── WhatsApp Cloud API (notificações)
              └── SMTP/transacional (notificações)
```

## Módulos e fronteiras

| Módulo | Responsabilidade | Schema PostgreSQL | Interfaces públicas |
| --- | --- | --- | --- |
| `identity` | Credenciais, sessão por cookie, papéis e autorização. | `identity` | Consulta de ator autenticado. |
| `producer` | Perfil do produtor, CPF/CNPJ e propriedades rurais. | `producer` | Consulta de produtor e propriedade autorizada. |
| `catalog` | Produtos, categorias, atributos técnicos, mídia, importação Agrofit e monitoramento de carga. | `catalog` | Consulta de catálogo e evento de atualização. |
| `pricing` | Tabelas, condições, regras de desconto/acréscimo e simulação. | `pricing` | Cotação imutável para o carrinho/pedido. |
| `cart` | Carrinho ativo, itens e recuperação após autenticação. | `cart` | Leitura/gravação de carrinho do ator. |
| `order` | Solicitação, snapshots, estados, revisão e auditoria. | `orders` | Criar e transicionar solicitação. |
| `fulfillment` | Depósitos, modalidade de retirada/entrega e reserva após aprovação. | `fulfillment` | Validar atendimento e criar/expirar reserva. |
| `compliance` | Exigência e validação humana de receita agronômica. | `compliance` | Bloqueio de confirmação regulatória. |
| `notification` | Registro, envio e retentativa de e-mail e WhatsApp. | `notification` | Publicação de notificação de domínio. |

Cada módulo expõe apenas classes no pacote público acordado; os demais módulos usam
essas interfaces, nunca repositórios, entidades JPA ou tabelas internas de outro
módulo. Testes `ApplicationModules` do Spring Modulith devem falhar em acessos
indevidos.

## Fluxos essenciais

### Importação Agrofit

1. Agendador diário busca o CSV oficial configurado.
2. O módulo valida formato e registra o arquivo, hash, data e resultado da execução.
3. Linhas válidas atualizam somente dados regulatórios, usando o registro MAPA como
   identificador de deduplicação.
4. Dados comerciais, visibilidade, preço, mídia e categorização da revenda nunca são
   sobrescritos pela importação.
5. Em erro, a última versão válida permanece publicada. É criado alerta no painel e
   são disparadas notificações por e-mail e WhatsApp para administradores.

O parser e o cliente de obtenção do CSV são portas. O mapeamento exato deve ser
confirmado contra o layout vigente do arquivo antes da primeira implementação.

### Pedido

1. Catálogo fornece preço de referência; `pricing` retorna cada simulação.
2. `cart` guarda produtos e quantidades; checkout autentica/cria produtor.
3. `order` cria snapshot de itens, cotação e logística com estado
   `AGUARDANDO_VALIDACAO`.
4. A revisão comercial aprova, pede ajuste ou recusa.
5. Para aprovação, `compliance` deve liberar cada item que exige receita.
6. `fulfillment` cria reserva com depósito e expiração configurados.
7. `notification` registra e envia alterações de estado.

Estados mínimos: `AGUARDANDO_VALIDACAO`, `AGUARDANDO_RECEITA`,
`AJUSTE_SOLICITADO`, `APROVADA`, `RECUSADA`, `RESERVA_EXPIRADA`.

## Persistência e consistência

- PostgreSQL com um schema por módulo; Flyway mantém as migrações do schema proprietário.
- UUIDs são usados para identificadores externos; registros MAPA permanecem como chave
  regulatória natural, não como identificador primário.
- Snapshots do pedido são serializados no schema `orders`; alterações posteriores de
  catálogo ou preço não alteram histórico.
- Ações de revisão, receita e reserva são auditáveis: ator, data/hora, estado anterior,
  estado posterior e motivo.
- Transições que abrangem módulos usam chamadas síncronas de interfaces públicas dentro
  da transação do monólito. Não há entrega eventual ou broker no MVP.

## API e interface

REST versionado em `/api/v1`; contratos OpenAPI publicados pela API.

- A API responde objetos de erro padronizados e códigos de domínio previsíveis.
- A SPA React é dividida por features equivalentes: `catalog`, `cart`, `checkout`,
  `orders`, `admin-catalog`, `admin-orders` e `admin-imports`.
- Preços/simulações são recalculados pela API em alterações de carrinho; o frontend não
  é a fonte de regras comerciais.
- O painel de importação mostra última carga válida, início/fim, contagens, erro,
  operador e próxima execução.

## Segurança

- Spring Security autentica e-mail/senha, com senha protegida por algoritmo adaptativo.
- Cookie de sessão é `HttpOnly`, `Secure` em produção e `SameSite` adequado ao domínio.
- CSRF é tratado para requisições mutáveis autenticadas por cookie.
- Autorização garante que produtor só vê seus dados/pedidos; vendedor e administrador
  recebem permissões explicitamente atribuídas.
- Credenciais de Cloudinary, Meta e e-mail ficam somente no ambiente do backend.
- URLs de upload do Cloudinary são assinadas e os limites de tipo/tamanho são
  verificados no backend.
- Só há envio de WhatsApp após registro de opt-in e com templates aprovados quando
  necessários. Cada envio guarda provedor, destinatário mascarado, tipo, resultado e
  custo estimado; a tabela oficial da Meta precisa ser consultada antes de orçamento.

## Integrações

| Integração | Uso | Falha |
| --- | --- | --- |
| Agrofit CSV | Dados regulatórios diários. | Mantém última carga válida, alerta e exibe falha. |
| Cloudinary Free | Upload e entrega de mídia comercial. | Não publica mídia nova; produtos existentes continuam com URLs salvas. Monitorar 25 créditos/mês. |
| WhatsApp Cloud API | Notificações de estado e falhas operacionais. | Registra falha e retenta de modo limitado; e-mail permanece como contingência. |
| E-mail | Avisos para produtor e administradores. | Registra falha e retenta de modo limitado. |

## Observabilidade

- Logs estruturados com ID de correlação, sem senha, dados pessoais completos ou tokens.
- Health checks de aplicação, banco e dependências críticas.
- Métricas: tempo de criação do pedido, conversão carrinho→solicitação, total por
  estado, sucesso/falha de importação, consumo estimado de mídia e mensagens.
- Alertas de importação diária, expiração iminente de reserva e falha persistente de
  notificação.

## Estratégia de testes

| Nível | Cobertura mínima |
| --- | --- |
| Unitário | Cálculo de preço, regras de estado, receita e expiração de reserva. |
| Integração | Repositórios PostgreSQL, Flyway, endpoints, autenticação e contratos dos módulos. |
| Arquitetura | Verificação `ApplicationModules` das fronteiras Spring Modulith. |
| E2E crítico | Catálogo→carrinho→checkout→aprovação/reserva; item com receita; falha Agrofit. |

## Não decidido / condição de início

Antes da integração produtiva, obter da revenda: conta Meta Business verificada,
número WhatsApp, opt-ins, templates aprovados, provedor de e-mail e credenciais
Cloudinary. Esses itens são configuração de ambiente e não bloqueiam o esqueleto
local do MVP.
