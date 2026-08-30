# Plataforma de Pedidos de Insumos Agrícolas — Arquitetura e MVP

## 1. Objetivo da plataforma

A ideia discutida é criar uma plataforma para venda de insumos agrícolas com uma experiência de pedido muito mais ágil, semelhante à experiência de marketplaces e aplicativos de compras:

- poucos cliques;
- catálogo rápido;
- carrinho;
- checkout simples;
- criação do pedido sem depender de várias chamadas síncronas ao ERP;
- integração posterior com o ERP;
- possibilidade de atender várias cooperativas/empresas;
- arquitetura preparada para diferentes ERPs, APIs, meios de pagamento e mensagerias.

O objetivo inicial não é construir toda a plataforma definitiva, mas validar primeiro se essa experiência resolve um problema real.

---

# 2. Arquitetura geral

A recomendação evoluiu para uma arquitetura de **Modular Monolith**, e não microservices.

A ideia é ter uma única aplicação Spring Boot, com módulos de negócio bem separados.

```text
                    AGRI PLATFORM
                 Modular Monolith
                         │
        ┌────────────────┼────────────────┐
        │                │                │
        ▼                ▼                ▼
     Catalog           Order           Customer
        │                │                │
        ▼                ▼                ▼
   Regulatory          Fiscal          Property

                    Integration
                         │
        ┌────────────────┼────────────────┐
        ▼                ▼                ▼
      ERP SPI       Messaging SPI      Payment SPI
        │                │                │
     Adapters          Adapters          Adapters
        │                │                │
   SAP/TOTVS/etc.   Kafka/Rabbit/etc.   Provider A/B
```

A principal ideia é:

> O núcleo da aplicação não deve conhecer implementações concretas de fornecedores.

---

# 3. Modular Monolith em vez de Microservices

A recomendação foi não começar com microservices.

Um Modular Monolith permite:

- um único deploy;
- um único runtime;
- menor complexidade operacional;
- escala horizontal;
- módulos isolados;
- possibilidade futura de extrair um módulo para um serviço independente caso exista uma razão real.

Exemplo:

```text
agri-platform/
│
├── tenant/
├── identity/
├── customer/
├── property/
├── catalog/
├── cart/
├── order/
├── commercial/
├── fiscal/
├── regulatory/
└── integration/
```

A modularidade deve ser orientada a negócio, e não apenas organizada por tecnologia.

Em vez de:

```text
controller/
service/
repository/
entity/
```

para toda a aplicação, preferir:

```text
order/
├── domain/
├── application/
├── api/
└── infrastructure/

catalog/
├── domain/
├── application/
├── api/
└── infrastructure/
```

---

# 4. Arquitetura baseada em Ports & Adapters

A plataforma deve utilizar interfaces nas fronteiras que realmente variam.

Por exemplo:

```java
public interface ErpGateway {

    PedidoResult enviar(Pedido pedido);

    PedidoResult consultarPedido(String id);
}
```

Implementações:

```text
SapErpAdapter
TotvsErpAdapter
CustomErpAdapter
MockErpAdapter
```

O módulo de pedidos não deve saber qual ERP está sendo utilizado.

O mesmo princípio vale para mensageria:

```java
public interface EventPublisher {

    void publish(Event event);
}
```

Implementações:

```text
KafkaEventPublisher
RabbitMqEventPublisher
```

E para pagamentos:

```java
public interface PaymentGateway {

    PaymentResult authorize(PaymentRequest request);

    PaymentResult cancel(String transactionId);
}
```

Implementações:

```text
AsaasAdapter
StripeAdapter
OutroProviderAdapter
```

---

# 5. Conceito Plug-and-Play

A arquitetura desejada é que os fornecedores sejam substituíveis.

Exemplo:

```text
Tenant A
  ERP: TOTVS
  Messaging: Kafka
  Payment: Asaas
```

Outro:

```text
Tenant B
  ERP: SAP
  Messaging: RabbitMQ
  Payment: Stripe
```

A mesma aplicação atende ambos.

Configurações podem determinar os providers:

```yaml
erp:
  provider: totvs

messaging:
  provider: kafka

payment:
  provider: asaas
```

O Core não deve depender de DTOs ou classes específicas de cada ERP.

---

# 6. Anti-Corruption Layer

Cada integração deve possuir uma camada de tradução.

Exemplo:

```text
Seu domínio
    ↓
ErpGateway
    ↓
Adapter
    ↓
DTO específico do ERP
    ↓
ERP
```

O domínio usa conceitos próprios:

```text
Pedido
Cliente
Produto
```

e não conceitos específicos como:

```text
SalesOrder
BusinessPartner
Material
SC5
SA1
SB1
```

Isso evita contaminar o domínio com detalhes de fornecedores.

---

# 7. Multi-tenancy

Como a plataforma poderá atender várias empresas/cooperativas, foi discutida a estratégia de banco de dados.

A recomendação inicial é:

> PostgreSQL compartilhado + schema compartilhado + `tenant_id` nas entidades de negócio.

Exemplo:

```sql
CREATE TABLE orders (
    id          BIGINT PRIMARY KEY,
    tenant_id   UUID NOT NULL,
    customer_id BIGINT NOT NULL,
    status      VARCHAR(30),
    created_at  TIMESTAMP NOT NULL
);
```

O mesmo vale para produtos, clientes, propriedades etc.

Estrutura conceitual:

```text
PostgreSQL
│
└── agri_platform
    │
    ├── tenant
    ├── product
    ├── customer
    ├── property
    ├── order
    └── order_item
```

---

# 8. Estratégia de evolução do banco

Não é necessário começar com banco dedicado por cliente.

Pode-se evoluir:

### Tier 1 — Shared

```text
PostgreSQL
Schema compartilhado
tenant_id
```

### Tier 2 — Dedicated schema

```text
PostgreSQL
├── tenant_a
├── tenant_b
└── tenant_c
```

### Tier 3 — Dedicated database

```text
Tenant A → PostgreSQL próprio
Tenant B → PostgreSQL próprio
```

A aplicação continua podendo ser a mesma.

---

# 9. Segurança multi-tenant

No MVP pode-se começar com:

- `tenant_id`;
- `TenantContext`;
- filtros tenant-aware no backend.

Exemplo conceitual:

```java
tenantContext.getTenantId();
```

Posteriormente pode-se adicionar PostgreSQL Row-Level Security (RLS) como uma segunda camada de proteção.

A recomendação foi não tornar RLS obrigatório no primeiro MVP.

---

# 10. Dados globais x dados do tenant

Nem tudo precisa ter `tenant_id`.

Dados oficiais ou compartilhados podem ser globais:

```text
NCM
CFOP
UF
Municípios
Regras regulatórias
Dados oficiais
```

Dados específicos da empresa:

```text
Produto comercial
Cliente
Produtor
Propriedade
Preço
Condição comercial
Pedido
Estoque
```

Uma estrutura interessante:

```text
global_product
       │
       ├── tenant_product A
       ├── tenant_product B
       └── tenant_product C
```

Assim um produto regulatório comum pode ser reutilizado por diversos tenants.

---

# 11. Usuário, identidade e cliente

Foi discutida uma estratégia de login simples para o produtor.

A recomendação é não confundir:

> identidade digital

com:

> cadastro comercial do produtor.

O Google pode confirmar a identidade do usuário, mas o cadastro comercial pode estar na cooperativa/ERP.

Estrutura:

```text
User
 │
 ├── Identity
 │      ├── Google
 │      └── Email
 │
 └── UserTenant
          │
          ▼
        Tenant
          │
          ▼
       Customer
          │
          ▼
       Property
```

---

# 12. Login do MVP

A experiência desejada:

```text
┌─────────────────────────────────┐
│       Entre para fazer pedido   │
│                                 │
│ [ Continuar com Google ]        │
│                                 │
│ [ Continuar com e-mail ]        │
│                                 │
│ [ Criar minha conta ]           │
└─────────────────────────────────┘
```

Para o MVP, a recomendação foi começar com:

- Google;
- e-mail com código/passwordless.

Senha tradicional pode ser adicionada depois.

Não é necessário começar com:

- Apple;
- Microsoft;
- MFA sofisticado;
- múltiplos Identity Providers;
- RBAC complexo.

Também foi recomendado usar um Identity Provider em vez de implementar autenticação OAuth2/OIDC do zero.

Opções possíveis:

- Keycloak;
- Auth0;
- Amazon Cognito;
- Clerk;
- Supabase Auth.

Para o MVP, Keycloak pode ser avaliado caso seja desejável maior controle.

---

# 13. Cadastro progressivo

A experiência pode ser:

```text
Continuar com Google
        ↓
Conta criada
        ↓
Qual seu CPF/CNPJ?
        ↓
Buscar cadastro
        ↓
Encontramos:
João Silva
Cooperativa Alfa
Fazenda São João
        ↓
[Confirmar]
        ↓
Fazer pedido
```

Isso evita um formulário inicial enorme.

O sistema pode buscar o produtor no cadastro da cooperativa/ERP apenas no onboarding.

Depois, mantém um snapshot local para não depender do ERP durante cada pedido.

---

# 14. React no frontend

A escolha definida para o frontend é:

> React

Arquitetura inicial:

```text
React
├── authentication/
├── onboarding/
├── catalog/
├── cart/
├── order/
└── profile/
```

Backend:

```text
Spring Boot
├── identity/
├── tenant/
├── customer/
├── product/
├── order/
└── integration/
```

---

# 15. Problema atual de performance

O problema principal que motivou a plataforma é que o pedido atual pode levar aproximadamente:

```text
30–50 segundos
```

devido a várias interações síncronas com o ERP e dependências externas.

A proposta é separar:

### Experiência do usuário

```text
Adicionar produto
      ↓
Carrinho
      ↓
Confirmar
      ↓
Pedido criado
```

de:

### Integração com ERP

```text
Pedido criado
      ↓
Processamento assíncrono
      ↓
ERP
      ↓
Integrado
```

A meta inicial é que o usuário não fique esperando o ERP.

---

# 16. Escalabilidade

Não é necessário usar microservices para escalar.

A aplicação pode ser stateless e ter várias instâncias:

```text
Internet
   ↓
Load Balancer
   ↓
┌─────────┬─────────┬─────────┐
│ App #1  │ App #2  │ App #3  │
└─────────┴─────────┴─────────┘
             │
        PostgreSQL
```

Em momentos de pico:

```text
2 → 4 → 8 → 12 instâncias
```

e depois reduzir novamente.

---

# 17. API e Workers

Foi recomendada a separação lógica entre:

### API

Responsável por:

- login;
- catálogo;
- carrinho;
- checkout;
- consultas.

### Workers

Responsáveis por:

- integração ERP;
- sincronizações;
- processamento de pedidos;
- notificações;
- tarefas assíncronas.

Inicialmente, ambos podem estar dentro do mesmo Modular Monolith.

Não é necessário criar um microservice separado.

---

# 18. Processamento assíncrono

Uma evolução recomendada:

```text
Pedido
  ↓
PostgreSQL
  ↓
Outbox
  ↓
Worker
  ↓
ERP
```

Posteriormente:

```text
PostgreSQL
    ↓
Outbox
    ↓
Kafka
    ↓
ERP Worker
    ↓
ERP
```

No MVP inicial nem Kafka é obrigatório.

Pode-se começar com um worker simples ou `@Scheduled`.

---

# 19. Kafka/RabbitMQ

A ideia de plug-and-play continua válida.

Uma interface:

```java
public interface EventPublisher {
    void publish(Event event);
}
```

Implementações futuras:

```text
InMemoryEventPublisher
KafkaEventPublisher
RabbitMqEventPublisher
```

No MVP inicial, Kafka e RabbitMQ podem ficar fora.

Só devem entrar quando houver necessidade real.

---

# 20. Backpressure

Quando a plataforma receber muitos pedidos e o ERP suportar uma taxa menor, a fila funciona como amortecedor.

Exemplo:

```text
5.000 pedidos/s
       ↓
     Kafka
       ↓
   100 pedidos/s
       ↓
      ERP
```

Isso impede que o pico de usuários derrube o ERP.

---

# 21. Escala por tenant

No futuro pode haver quotas/prioridades por tenant.

Exemplo:

```text
Tenant A → 500 req/s
Tenant B → 100 req/s
Tenant C → 50 req/s
```

Isso reduz o risco de um cliente muito grande prejudicar os demais.

Também pode haver filas ou partições separadas por tenant quando houver necessidade.

---

# 22. PostgreSQL e escalabilidade

PostgreSQL provavelmente será um dos componentes que exigirá atenção conforme a escala crescer.

Índices tenant-aware são importantes:

```sql
CREATE INDEX idx_order_tenant_status
ON orders(tenant_id, status);
```

Consultas devem considerar o tenant:

```sql
WHERE tenant_id = ?
AND status = ?
```

Também é necessário dimensionar corretamente o pool de conexões.

Não se deve simplesmente multiplicar o número de instâncias e abrir centenas de conexões por instância.

Uma arquitetura futura pode usar:

```text
Spring Boot
   ↓
HikariCP
   ↓
PgBouncer
   ↓
PostgreSQL
```

---

# 23. Redis

Redis pode ser adicionado posteriormente para:

- cache;
- rate limiting;
- idempotency keys;
- dados frequentemente consultados;
- locks distribuídos quando realmente necessários.

Não deve substituir o PostgreSQL como banco principal.

---

# 24. Observabilidade

A recomendação foi tratar observabilidade em três pilares:

```text
Observabilidade
├── Logs
├── Metrics
└── Traces
```

Posteriormente:

```text
OpenTelemetry
      │
 ┌────┼─────────────┐
 ▼    ▼             ▼
Logs Metrics       Traces
 │    │             │
Loki Prometheus    Tempo
 └────┴──────┬──────┘
             ▼
          Grafana
```

Mas no MVP não é necessário montar toda essa infraestrutura.

---

# 25. Logs

Preferir logs estruturados.

Exemplo:

```json
{
  "timestamp": "2026-08-29T21:00:00Z",
  "level": "INFO",
  "service": "order-service",
  "tenantId": "abc",
  "orderId": "12345",
  "traceId": "7f8a9c",
  "event": "ORDER_CREATED",
  "durationMs": 184
}
```

Evitar colocar dados pessoais ou sensíveis livremente nos logs.

Preferir IDs internos em vez de CPF, CNPJ, dados bancários etc.

---

# 26. Métricas

Algumas métricas importantes:

### API

```text
http_requests_total
http_request_duration_seconds
http_requests_errors_total
```

### Pedidos

```text
orders_created_total
orders_failed_total
order_processing_duration
orders_waiting_erp
```

### ERP

```text
erp_request_duration
erp_request_errors
erp_timeout_total
erp_availability
```

### Mensageria

```text
consumer_lag
messages_processed
messages_failed
retry_count
```

### Banco

```text
connection_pool_usage
query_duration
active_connections
```

---

# 27. Tracing

Com OpenTelemetry será possível acompanhar uma operação ponta a ponta:

```text
POST /orders
   │
   ├── validate customer
   ├── product lookup
   ├── fiscal rule
   ├── save order
   └── publish event
             │
             ▼
           Kafka
             │
             ▼
        ERP Worker
             │
             ▼
        ERP Adapter
             │
             ▼
             ERP
```

Isso permite descobrir exatamente onde os 30–50 segundos estão sendo gastos.

---

# 28. SLOs

Exemplos de metas futuras:

### Checkout

99% das requisições < 1 segundo.

### Disponibilidade

99,9%.

### Pedido

Pedido aceito pela plataforma deve ser persistido com sucesso.

### ERP

A indisponibilidade do ERP não deve impedir a criação do pedido.

Princípio importante:

> O ERP nunca deve ser o single point of failure da experiência do produtor.

---

# 29. Fiscal e regulatório

Foi discutida a ideia de criar:

### Regulatory

Responde:

> O produto pode ser vendido/utilizado nessa situação?

### Fiscal

Responde:

> Como a operação deve ser tributada/documentada?

Exemplo:

```text
Produto
 │
 ├── Regulatory
 │      ├── permitido?
 │      ├── UF?
 │      ├── restrições?
 │      └── finalidade?
 │
 └── Fiscal
        ├── NCM
        ├── CFOP
        ├── ICMS
        └── outras regras
```

Foi recomendado não construir um motor fiscal completo no primeiro MVP.

Começar com poucas regras concretas e evoluir conforme forem validadas.

---

# 30. Catálogo oficial

A ideia é evitar que o usuário cadastre manualmente informações que podem ser obtidas de fontes oficiais.

Futuro fluxo:

```text
Fontes oficiais
      ↓
Importador/Sincronizador
      ↓
Global Data Store
      ↓
NCM / CFOP / Regulatório etc.
      ↓
Todos os tenants
```

O importante é separar dados oficiais globais dos dados comerciais específicos da cooperativa.

---

# 31. MVP — decisão principal

Foi concluído que não é necessário implementar toda a arquitetura discutida para o MVP.

A regra deve ser:

> Validar o produto primeiro e sofisticar a arquitetura conforme necessidade real.

Evitar inicialmente:

- Kubernetes;
- microservices;
- Kafka;
- RLS sofisticado;
- múltiplos ERPs;
- motor fiscal completo;
- catálogo governamental completo;
- observabilidade avançada;
- múltiplos providers.

---

# 32. MVP 1 — escopo recomendado

Stack:

```text
React
   ↓
Spring Boot 3 + Java 21
   ↓
PostgreSQL
```

Módulos:

```text
tenant
customer
product
property
cart
order
integration
```

Funcionalidades:

### Produtor

- login;
- escolher propriedade;
- pesquisar produto;
- adicionar quantidade;
- carrinho;
- confirmar pedido;
- visualizar pedido.

### Backoffice

- cadastrar/importar produtos;
- cadastrar produtor;
- visualizar pedidos;
- acompanhar integração ERP.

### Backend

- multi-tenancy básico;
- catálogo;
- pedido;
- `ErpGateway`;
- Mock ERP;
- logs;
- métricas;
- tratamento da indisponibilidade do ERP.

---

# 33. MVP 0 — prova de conceito comercial

Antes do MVP completo, pode-se fazer uma versão ainda menor.

Objetivo:

> Um produtor consegue entrar, encontrar um produto, montar um pedido e confirmar em poucos cliques.

### Semana 1

```text
React
  ↓
Login Google/e-mail
  ↓
Spring Boot
  ↓
PostgreSQL

Produto
Produtor
Propriedade
```

### Semana 2

```text
Carrinho
   ↓
Checkout
   ↓
Pedido
   ↓
Mock ERP
```

Depois colocar na mão de usuários reais.

---

# 34. Incrementos recomendados

## Incremento 1 — Entrar

```text
Google/e-mail
    ↓
Usuário
```

## Incremento 2 — Escolher onde comprar

```text
Usuário
 ↓
Cooperativa
 ↓
Propriedade
```

## Incremento 3 — Comprar

```text
Catálogo
 ↓
Produto
 ↓
Carrinho
```

## Incremento 4 — Pedido

```text
Carrinho
 ↓
Confirmar
 ↓
Pedido criado
```

## Incremento 5 — ERP

```text
Pedido criado
 ↓
Fila/outbox simples
 ↓
ERP
```

## Incremento 6 — Operação

```text
Dashboard
Pedidos
Status
Erros
Logs
```

---

# 35. Roadmap incremental

## Fase 1

```text
✔ Pedido rápido
✔ PostgreSQL
✔ Multi-tenant básico
✔ Modular Monolith
✔ ERP Adapter
✔ Logs
✔ Mock ERP
```

## Fase 2

```text
✔ Outbox
✔ Worker
✔ Retry
✔ Idempotência
✔ ERP assíncrono
```

## Fase 3

```text
✔ Kafka/RabbitMQ
✔ EventPublisher
✔ Observabilidade
✔ OpenTelemetry
✔ Métricas
```

## Fase 4

```text
✔ Catálogo regulatório
✔ Fontes oficiais
✔ Estados
✔ Sincronização automática
```

## Fase 5

```text
✔ Motor fiscal
✔ CFOP
✔ NCM
✔ ICMS
✔ Regras por UF
✔ Versionamento das regras
```

## Fase 6

```text
✔ Múltiplos ERPs
✔ Múltiplos provedores
✔ Múltiplas mensagerias
✔ Escala
✔ RLS
✔ Cache avançado
✔ Observabilidade completa
```

---

# 36. Estimativa de desenvolvimento com IA

Considerando um desenvolvedor experiente em Java/Spring usando IA/Cursor como copiloto/agente:

### POC extremamente enxuto

**10–15 dias**

### MVP funcional para usuários reais

**3–5 semanas**

### MVP mais polido

**6–8 semanas**

### Produto inicial mais completo

**3–6 meses**

Essas estimativas dependem fortemente do controle de escopo.

---

# 37. Estratégia de desenvolvimento com IA

A IA deve ser utilizada como agente de desenvolvimento, mas com limites arquiteturais claros.

Exemplo de solicitação:

> Implemente o módulo `order` seguindo as regras arquiteturais do projeto. Não altere outros módulos. Crie domínio, use case, repository port, adapter JPA, controller e testes. Não introduza novas dependências.

Depois:

> Analise os testes do módulo order e identifique os cenários de negócio que estão faltando.

E:

> Gere testes para os casos de criação de pedido sem alterar código de produção.

A IA pode acelerar muito a implementação, mas existe o risco de gerar código demais.

A regra recomendada é:

> Cada incremento precisa entregar uma funcionalidade utilizável.

---

# 38. Métrica principal do MVP

O principal objetivo de performance é melhorar a experiência atual.

Hoje:

```text
30–50 segundos
```

Meta da primeira versão:

```text
Adicionar produto
        ↓
< 100 ms

Adicionar outro
        ↓
< 100 ms

Finalizar
        ↓
< 1 segundo

"Pedido recebido"
```

Depois:

```text
Pedido recebido
       ↓
Enviando ao ERP...
       ↓
ERP
       ↓
Integrado
```

O produtor não precisa esperar o ERP.

---

# 39. Princípio central do projeto

A maior conclusão das discussões foi:

> **O ERP nunca deve ser o single point of failure da experiência do produtor.**

A experiência deve ser:

```text
Abrir catálogo
      ↓
Adicionar produtos
      ↓
Montar pedido
      ↓
Validar regras locais
      ↓
Confirmar
      ↓
Pedido criado
```

Mesmo que:

```text
ERP ❌
```

o pedido pode ficar:

```text
AGUARDANDO_INTEGRACAO
```

e ser processado posteriormente.

---

# 40. Decisão arquitetural para o MVP

A arquitetura recomendada é:

```text
React
   ↓
Spring Boot 3 / Java 21
   ↓
Modular Monolith
   ↓
PostgreSQL
```

Com:

```text
Multi-tenancy básico
User / Identity / Tenant / Customer
ErpGateway
Mock ERP
Logs
Métricas básicas
```

E deixando para depois:

```text
Kafka
RabbitMQ
Outbox completo
OpenTelemetry
RLS
Redis
Múltiplos ERPs
Motor fiscal completo
Catálogo regulatório
Autoscaling avançado
Kubernetes
```

A estratégia geral é:

> **Começar pequeno, validar rápido, medir o uso real e evoluir a arquitetura somente quando houver necessidade concreta.**
