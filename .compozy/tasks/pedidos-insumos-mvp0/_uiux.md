# Mapa de mudanças de UI/UX: pedidos de insumos agrícolas MVP0

Este mapa inventaria as novas superfícies web do MVP0. A referência visual
autoritativa é o projeto OpenDesign Cloud **MVP0 — Pedidos de Insumos Agrícolas**,
artefato `mvp0-pedidos-insumos.html`. Agentes de implementação devem recuperar esse
artefato antes de construir uma superfície voltada ao usuário.

Documentos complementares: `_spec.md`, `_user_stories.md` e `_dx.md`.

## Restrições de design

- Usar rótulos claros em português nas telas voltadas ao produtor e evitar jargões
  agrícolas ou técnicos sem explicação.
- Projetar primeiro para navegadores móveis, preservando um layout utilizável em desktop.
- Manter carrinho, preço, quantidade e status de confirmação visíveis sem conteúdo
  disponível apenas ao passar o cursor.
- Fornecer foco de teclado visível, títulos semânticos, resumos de erros e alvos de toque
  adequados para navegadores móveis.
- Usar estados explícitos de vazio, indisponível, erro, permissão negada e carregamento.
- Nunca exibir um produto regulado como disponível para pedido no MVP0.

## Mapa de superfícies

| # | Superfície | Tipo | Mudança principal | Histórias |
| --- | --- | --- | --- | --- |
| S1 | Catálogo | Novo | Navegar e pesquisar produtos elegíveis. | US-001 |
| S2 | Carrinho | Novo | Ajustar produtos e exibir um total simples. | US-002 |
| S3 | Checkout | Novo | Identificar o produtor e coletar o contexto de retirada. | US-003 |
| S4 | Confirmação e detalhe do pedido | Novo | Exibir pedido local e confirmação downstream. | US-004 |
| S5 | Meus pedidos | Novo | Listar e reabrir pedidos do produtor. | US-004 |
| S6 | Pedidos do backoffice | Novo | Listar e inspecionar pedidos recebidos. | US-005 |

### S1. Catálogo

- **Hoje:** Nova superfície.
- **Estados a projetar:** navegação por categoria, resultados, pesquisa vazia, item indisponível,
  imagem ausente, link direto oculto de produto regulado, carregamento e erro de serviço.
- **Referência:** artefato OpenDesign Cloud `mvp0-pedidos-insumos.html`, visão Catálogo.

### S2. Carrinho

- **Hoje:** Nova superfície.
- **Estados a projetar:** carrinho vazio, um item, vários itens, validação de quantidade,
  item indisponível, pedido com 100 itens, carregamento, interrupção do estado salvo e carrinho
  mantido até a confirmação do pedido.
- **Referência:** artefato OpenDesign Cloud `mvp0-pedidos-insumos.html`, visão Carrinho.

### S3. Checkout

- **Hoje:** Nova superfície.
- **Estados a projetar:** conta obrigatória, criação de conta, falha de entrada, dados de
  checkout ausentes, propriedade selecionada, retirada selecionada, confirmação em andamento e
  sessão interrompida.
- **Referência:** artefato OpenDesign Cloud `mvp0-pedidos-insumos.html`, visão Checkout.

### S4. Confirmação e detalhe do pedido

- **Hoje:** Nova superfície.
- **Estados a projetar:** confirmação downstream aceita, acesso ao pedido negado, carregamento e
  erro de serviço.
- **Referência:** artefato OpenDesign Cloud `mvp0-pedidos-insumos.html`, visão Pedido.

### S5. Meus pedidos

- **Hoje:** Nova superfície.
- **Estados a projetar:** lista de pedidos, lista vazia, detalhe do pedido, carregamento e
  erro de serviço.
- **Referência:** artefato OpenDesign Cloud `mvp0-pedidos-insumos.html`, visão Meus pedidos.

### S6. Pedidos do backoffice

- **Hoje:** Nova superfície.
- **Estados a projetar:** lista de pedidos, lista vazia, detalhes, confirmação downstream aceita,
  permissão negada, carregamento e erro de serviço.
- **Referência:** artefato OpenDesign Cloud `mvp0-pedidos-insumos.html`, visão Backoffice.

## Plano de componentes

### Regras de composição

Usar controles de formulário comuns, rótulos de status, tabelas ou listas responsivas e padrões
de feedback de forma consistente nas cinco superfícies. Evitar componentes personalizados até que
a base de design aprovada estabeleça uma necessidade.

### Novos primitivos compartilhados

Nenhum é autorizado antes da aprovação da base de design.

### Novos componentes de domínio

- Cartão de produto — S1; renderiza o resumo do produto e a ação de adicionar ao carrinho.
- Linha do carrinho — S2; renderiza controles de quantidade e totais.
- Formulário de checkout — S3; coleta o contexto de conta, propriedade e retirada.
- Status do pedido — S4 e S5; renderiza a confirmação downstream de forma fiel.
- Linha da lista de pedidos — S5; resume um pedido para um operador.

### Mapeamento de sinais e estados

| Estado | Significado | Apresentação obrigatória |
| --- | --- | --- |
| Disponível | O item pode entrar no carrinho. | Ação clara de adicionar ao carrinho. |
| Indisponível | O item não pode entrar no carrinho. | Explicar sem uma ação de compra ativa. |
| Recebido | O pedido local foi registrado. | Confirmação e identificador do pedido. |
| Aceito | A confirmação downstream simulada foi bem-sucedida. | Rótulo de status positivo. |
| Erro | A ação atual falhou. | Erro inline conciso e ação de recuperação. |
