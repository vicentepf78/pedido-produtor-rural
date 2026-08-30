# Mapa de mudanças de UI/UX: pedidos de insumos agrícolas MVP 1

Inventário das superfícies web. A referência visual autoritativa é o
projeto OpenDesign **MVP 1 — Pedidos de Insumos Agrícolas**, artefato
`loja-insumos-agricolas.html` (preview no daemon local). Copiar para
`docs/design/opendesign/pedidos-insumos-mvp1/` antes de implementar.
Agentes recuperam esse artefato antes de construir a tela.

Complementos: `_spec.md`, `_user_stories.md`, `_dx.md`.

## Restrições de design

- Visual próximo ao Mercado Livre: fundo cinza claro, cards brancos,
  cabeçalho com energia amarelo/ouro, tipografia sans-serif, contraste
  alto. Paleta final vem do artefato OpenDesign, não desta página.
- Mobile-first (390 px) e utilizável em desktop. Atalhos de compra no
  **topo**, nunca no rodapé.
- Carrossel: cards iguais, foto de demonstração em cima, rótulo
  centralizado, seta circular para o restante, categoria marcada.
- Fotos de categoria são de demonstração, não mídia oficial da revenda.
- Preço, quantidade, total e status sempre visíveis, sem conteúdo só no
  hover.
- Foco de teclado visível, títulos semânticos, erros inline, alvos de
  toque. Respeitar redução de movimento.
- Estados explícitos: vazio, pendente, erro, permissão, indisponível.
- Produto regulamentado nunca aparece como pedível.
- Operador não vê Catálogo, Carrinho, Checkout nem Pedido no topo.

## Mapa de superfícies

| # | Superfície | Tipo | Mudança principal | Histórias |
| --- | --- | --- | --- | --- |
| S1 | Catálogo | Modificar | Carrossel, busca composta, tamanho, Carregar mais. | US-001–US-003 |
| S2 | Carrinho | Modificar | Mesmo comportamento; visual novo. | US-004 |
| S3 | Entrar | Novo | Tela própria; volta à origem; operador → retaguarda. | US-005, US-006 |
| S4 | Criar conta | Novo | E-mail e senha; papel produtor. | US-005 |
| S5 | Checkout | Modificar | Identidade só se faltar; vazio explica itens. | US-007, US-006 |
| S6 | Pedido recebido e detalhe | Modificar | Visual novo; “Pedido recebido”. | US-008 |
| S7 | Meus pedidos | Modificar | Sem conta pede Entrar. | US-008, US-006 |
| S8 | Topo da loja | Modificar | Substitui a barra inferior. | US-006 |
| S9 | Retaguarda lista | Modificar | Visual da loja; topo de operação. | US-009 |
| S10 | Retaguarda detalhe | Modificar | Snapshot no visual da loja. | US-009 |
| S11 | Topo do operador | Novo | Pedidos da revenda, identificação, Sair. | US-009 |

### S1. Catálogo

- **Hoje:** `frontend/src/features/catalog/PaginaCatalogo.tsx` — abas
  derivadas dos produtos, um fetch de 30 itens, filtro de categoria no
  cliente, busca por nome.
- **Mudança:** carrossel fixo Todos / Sementes / Fertilizantes /
  Correção; busca soma à categoria; limpar filtros; tamanho 10 / 15 /
  30 / 50; Carregar mais via `pagina` da API; rodapé de abas some.
- **Estados:** carrossel, categoria marcada, busca composta, sem
  correspondência + limpar (US-002.EC-2), foto de card ausente
  (US-001.EC-1), categoria vazia (US-001.EC-2), Carregar mais visível /
  oculto (US-003), tamanho inválido recusado, carregando, erro com
  tentar de novo, produto indisponível, deep link regulado
  (US-001.EC-6), volta do detalhe com filtros intactos (US-002.EC-7).
- **Artboard:** `pedidos-insumos-mvp1-catalogo.html`.

### S2. Carrinho

- **Hoje:** `frontend/src/features/cart/PaginaCarrinho.tsx`.
- **Mudança:** visual do artefato; regras do MVP 0.
- **Estados:** vazio (US-004.EC-4), uma linha, várias, quantidade
  inválida, indisponível, interrupção, totais.
- **Artboard:** `pedidos-insumos-mvp1-carrinho.html`.

### S3. Entrar

- **Hoje:** formulário só em `frontend/src/features/checkout/PaginaCheckout.tsx`.
- **Mudança:** rota `/entrar`; link para Criar conta; após sucesso,
  origem ou retaguarda (US-005.EC-8).
- **Estados:** formulário, campos vazios, credenciais inválidas,
  sessão já ativa, cancelar/voltar, destino operador.
- **Artboard:** `pedidos-insumos-mvp1-entrar.html`.

### S4. Criar conta

- **Hoje:** só no checkout.
- **Mudança:** rota `/cadastro`; e-mail duplicado aponta para Entrar;
  sem “esqueci a senha”.
- **Estados:** formulário, validação, e-mail duplicado, sucesso → origem.
- **Artboard:** `pedidos-insumos-mvp1-cadastro.html`.

### S5. Checkout

- **Hoje:** `frontend/src/features/checkout/PaginaCheckout.tsx` — login
  inline + propriedade + retirada.
- **Mudança:** se já identificado, pula Entrar; se vazio, mensagem
  `CARRINHO_VAZIO` e atalho Checkout permanece (US-006.EC-2); visual novo.
- **Estados:** precisa de itens, precisa Entrar, já logado, sem
  propriedade (cadastra no próprio checkout), confirmação em curso,
  sessão expirada, produto inelegível no carrinho.
- **Artboard:** `pedidos-insumos-mvp1-checkout.html`.

### S6. Pedido recebido e detalhe

- **Hoje:** `frontend/src/features/my-orders/PaginaDetalhePedido.tsx`.
- **Mudança:** visual novo; atualizar não cria outro pedido.
- **Estados:** recebido + aceito, acesso negado, não encontrado,
  carregando, erro.
- **Artboard:** `pedidos-insumos-mvp1-pedido.html`.

### S7. Meus pedidos

- **Hoje:** `frontend/src/features/my-orders/PaginaMeusPedidos.tsx`.
- **Mudança:** convidado vê pedido de Entrar, não lista alheia.
- **Estados:** precisa Entrar, lista, vazia com ir ao catálogo,
  erro, escala.
- **Artboard:** `pedidos-insumos-mvp1-meus-pedidos.html`.

### S8. Topo da loja

- **Hoje:** `frontend/src/shell/LayoutApp.tsx:17-37` — `bottom-nav`.
- **Mudança:** cabeçalho com Catálogo, Carrinho (badge), Checkout,
  Pedido, Entrar **ou** nome/e-mail + Sair. Sem barra inferior.
- **Estados:** convidado, produtor, tela estreita, Sair mantém
  carrinho, Pedido sem conta.
- **Artboard:** incluído em todas as visões do produtor.

### S9. Retaguarda lista

- **Hoje:** `frontend/src/features/backoffice-orders/PaginaPedidosRetaguarda.tsx`.
- **Mudança:** mesmo visual da loja; topo S11; sem login inline de
  produtor.
- **Estados:** lista, vazia, permissão negada, sessão ausente,
  atualizar.
- **Artboard:** `pedidos-insumos-mvp1-retaguarda-lista.html`.

### S10. Retaguarda detalhe

- **Hoje:** `frontend/src/features/backoffice-orders/PaginaDetalheRetaguarda.tsx`.
- **Mudança:** visual da loja; snapshot imutável.
- **Estados:** detalhe, sem sessão, produtor negado, não encontrado.
- **Artboard:** `pedidos-insumos-mvp1-retaguarda-detalhe.html`.

### S11. Topo do operador

- **Hoje:** retaguarda oculta a nav (`LayoutApp.tsx:9`).
- **Mudança:** topo próprio — Pedidos da revenda, identificação, Sair.
- **Estados:** autenticado, após Sair (lista some).
- **Artboard:** incluído nas visões S9 e S10.

## Plano de componentes

### Regras

Compor a partir dos controles já usados na loja (campo, botão, card,
alerta). O CSS do artefato é contrato visual, não folha para importar.
A barra inferior `bottom-nav` é alvo de remoção nesta versão.

### Novos primitivos compartilhados

Nenhum primitivo genérico novo. O carrossel e o topo são compostos de
domínio nesta aplicação, não biblioteca compartilhada de outro produto.

### Novos componentes de domínio

- `TopoLoja` — S8; atalhos de compra + Entrar/Sair.
- `TopoOperador` — S11.
- `CarrosselCategoria` — S1; cards com foto de demonstração.
- `ControleTamanhoLista` — S1; 10 / 15 / 30 / 50.
- `BotaoCarregarMais` — S1; ausente quando `itens.length >= total`.
- `FormularioEntrar` / `FormularioCadastro` — S3, S4, reutilizados no
  checkout se a sessão ainda não existir.
- Cartão de produto, linha de carrinho, status de pedido — evoluem o
  visual, não a regra.

### Mapeamento de sinais e estados

| Estado | Significado | Apresentação |
| --- | --- | --- |
| Disponível | Pode ir ao carrinho | Ação Adicionar |
| Indisponível | Não pode ir ao carrinho | Sem ação de compra |
| Categoria ativa | Recorte atual | Card marcado no carrossel |
| Filtros ativos | Categoria ≠ Todos ou busca preenchida | Limpar filtros visível |
| Mais páginas | `itens` acumulados < `total` | Carregar mais |
| Fim da lista | Tudo visível | Sem Carregar mais |
| Recebido | Pedido local gravado | “Pedido recebido” + id |
| Aceito | Confirmação simulada ok | Selo positivo |
| Precisa entrar | Dado pessoal sem sessão | Formulário Entrar, sem vazar lista |
| Sem permissão | Papel errado | Mensagem `ACESSO_NEGADO` |
| Erro | Falha da ação | Erro inline + recuperação |
