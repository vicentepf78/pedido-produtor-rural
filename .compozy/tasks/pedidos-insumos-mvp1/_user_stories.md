# Histórias de usuário: pedidos de insumos agrícolas MVP 1

Catálogo canônico de comportamentos do MVP 1. Complementar a `_spec.md`.
Consumido pela Parte II, pelo mapa de UI e pelo contrato de testes.

## Personas

- **Produtor rural** — Precisa achar um insumo e gerar um pedido de venda
  com pouco treinamento, no celular ou no computador, sem muralha de conta
  na frente do catálogo.
- **Operador do revendedor** — Precisa inspecionar os pedidos recebidos e
  a confirmação registrada, no mesmo visual da loja, sem usar o topo de
  compra do produtor.

## Índice de histórias

| ID | Área funcional | Persona | História |
| --- | --- | --- | --- |
| US-001 | Catálogo | Produtor rural | Escolhe uma categoria no carrossel. |
| US-002 | Catálogo | Produtor rural | Pesquisa por nome somando à categoria. |
| US-003 | Catálogo | Produtor rural | Controla quantos produtos vê e carrega mais. |
| US-004 | Carrinho | Produtor rural | Monta o pedido antes de se identificar. |
| US-005 | Identidade | Produtor rural | Entra ou cria conta e volta ao ponto de origem. |
| US-006 | Navegação | Produtor rural | Usa o topo da loja para circular e sair. |
| US-007 | Checkout | Produtor rural | Confirma o pedido com conta, propriedade e retirada. |
| US-008 | Pedido | Produtor rural | Vê “Pedido recebido” e reabre os próprios pedidos. |
| US-009 | Retaguarda | Operador do revendedor | Inspeciona pedidos no visual da loja, com topo próprio. |

## Catálogo

### US-001: Escolher categoria no carrossel

**Como** produtor rural, **quero** ver as categorias em um carrossel de
cards com foto e rótulo, **para que** eu recorte o catálogo sem ler uma
lista longa.

Critérios de aceitação:

- AC-1: Dado o catálogo aberto, quando olho o carrossel, então vejo
  Todos, Sementes, Fertilizantes e Correção, cada um com foto de
  demonstração e rótulo.
- AC-2: Dado o carrossel, quando escolho uma categoria, então a listagem
  mostra só produtos elegíveis daquela categoria e o card escolhido
  permanece marcado.
- AC-3: Dado um produto regulamentado, quando navego o carrossel ou a
  listagem, então não vejo esse produto nem uma categoria Defensivos.

Casos de borda:

- EC-1: Foto de categoria ausente → o card permanece utilizável sem
  imagem quebrada.
- EC-2: Categoria sem produtos elegíveis → listagem vazia com forma de
  voltar a Todos ou limpar filtros.
- EC-3: Toques rápidos em duas categorias → vale a última escolhida; a
  listagem não mistura as duas.
- EC-4: Carrossel não cabe na tela → desloca na horizontal com controle
  visível além do gesto.
- EC-5: Convidado ou conta expirada → o carrossel e a listagem continuam
  utilizáveis.
- EC-6: Endereço direto de produto oculto ou regulamentado → acesso
  negado sem revelar dados do item.
- EC-7: Interrupção ao trocar de categoria → permanece a última listagem
  bem-sucedida e há como tentar de novo.
- EC-8: Escala do conjunto selecionado (30 itens) → Todas as categorias
  visíveis permanecem as quatro combinadas; não surgem categorias novas.

### US-002: Pesquisar somando à categoria

**Como** produtor rural, **quero** digitar o nome do produto e manter a
categoria escolhida, **para que** eu ache ureia em Fertilizantes sem
perder o recorte.

Critérios de aceitação:

- AC-1: Dado uma categoria escolhida e um texto de busca, quando
  pesquiso, então vejo só produtos elegíveis que pertencem àquela
  categoria e cujo nome corresponde à busca.
- AC-2: Dado categoria e/ou busca ativas, quando limpo os filtros, então
  a categoria volta a Todos, a busca fica vazia e a listagem mostra o
  conjunto elegível sem recorte.
- AC-3: Dado o catálogo, quando o campo de busca está visível, então
  posso pesquisar sem abrir outra página.

Casos de borda:

- EC-1: Busca em branco → a listagem respeita só a categoria atual.
- EC-2: Nenhum produto corresponde → estado vazio com ação de limpar
  filtros.
- EC-3: Texto hostil, só espaços ou extremamente longo → não quebra a
  loja; ou não há resultado, ou a busca é recusada com orientação clara.
- EC-4: Trocar a categoria com busca preenchida → a busca permanece e o
  resultado passa a somar a nova categoria.
- EC-5: Trocar a busca com categoria marcada → a categoria permanece e o
  resultado é recalculado.
- EC-6: Dois textos digitados em sequência rápida → o resultado visível
  corresponde ao último texto estável; não mistura páginas de buscas
  diferentes.
- EC-7: Voltar do detalhe do produto → categoria, busca e tamanho de
  lista que o produtor tinha permanecem.
- EC-8: Primeira visita sem filtros → Todos, busca vazia, tamanho padrão
  de 10.

### US-003: Tamanho da lista e Carregar mais

**Como** produtor rural, **quero** ver 10 produtos por vez e poder pedir
15, 30 ou 50, **para que** eu controle o quanto rola a tela e continue
com Carregar mais.

Critérios de aceitação:

- AC-1: Dado o catálogo sem escolha prévia de tamanho, quando a listagem
  aparece, então mostra até 10 produtos do recorte atual.
- AC-2: Dado o controle de tamanho, quando escolho 15, 30 ou 50, então a
  listagem recomeça do início do recorte com até esse quantitativo.
- AC-3: Dado que ainda existem produtos do recorte fora da tela, quando
  aciono Carregar mais, então a lista acrescenta o próximo bloco do
  tamanho escolhido sem tirar os que já estavam visíveis.
- AC-4: Dado que todos os produtos do recorte já estão visíveis, quando
  olho o fim da lista, então Carregar mais não está disponível.

Casos de borda:

- EC-1: Recorte com menos itens que o tamanho (por exemplo 50 no conjunto
  de 30) → uma única página com todos os itens do recorte e sem Carregar
  mais.
- EC-2: Mudar o tamanho depois de ter carregado mais de um bloco → a
  lista recomeça com o primeiro bloco do novo tamanho; não soma o novo
  tamanho ao que já estava na tela.
- EC-3: Carregar mais no último bloco parcial → acrescenta só o restante
  e depois oculta Carregar mais.
- EC-4: Acionar Carregar mais duas vezes ao mesmo tempo → não duplica
  produto na lista.
- EC-5: Interrupção ao carregar mais → os itens já visíveis permanecem e
  há como tentar de novo.
- EC-6: Valor de tamanho que não seja 10, 15, 30 ou 50 → a loja não
  aceita; permanece o último tamanho válido ou o padrão 10.
- EC-7: Recorte vazio → sem Carregar mais; vale o estado vazio de US-002.
- EC-8: Conjunto selecionado de 30 no recorte Todos com tamanho 10 →
  três blocos (10 + 10 + 10) até esgotar.

## Carrinho

### US-004: Montar o pedido como convidado

**Como** produtor rural, **quero** adicionar, alterar e remover produtos
antes de criar conta, **para que** eu prepare o pedido sem cadastro
precoce.

Critérios de aceitação:

- AC-1: Dado um produto visível e disponível, quando adiciono uma
  quantidade positiva, então o carrinho mostra o item, o preço unitário,
  o total da linha e o total do pedido.
- AC-2: Dado um item no carrinho, quando altero a quantidade ou o
  removo, então o total passa a refletir só os itens atuais.
- AC-3: Dado que saí da conta, quando volto ao carrinho, então os itens
  de convidado que eu já tinha continuam lá.

Casos de borda:

- EC-1: Quantidade zero, negativa ou não numérica → recusa com orientação
  clara.
- EC-2: Produto indisponível → não entra no carrinho.
- EC-3: Adicionar o mesmo produto de novo → junta a quantidade; não cria
  linha duplicada.
- EC-4: Carrinho vazio → o checkout explica que precisa de itens; o
  atalho Checkout no topo permanece visível.
- EC-5: Interrupção ao alterar o carrinho → permanece o último estado
  confirmado.
- EC-6: Cem linhas ou quantidades altas → totais continuam corretos e
  legíveis.
- EC-7: Duas alterações ao mesmo tempo → o carrinho visível converge
  para um único estado coerente, sem linhas fantasma.
- EC-8: Preço do catálogo → o mesmo valor unitário aparece para
  convidado e para quem já entrou.

## Identidade

### US-005: Entrar ou criar conta e voltar

**Como** produtor rural, **quero** uma tela própria de Entrar e de Criar
conta com e-mail e senha, **para que** eu me identifique pelo topo ou
pelo checkout e volte de onde vim.

Critérios de aceitação:

- AC-1: Dado que não estou identificado, quando aciono Entrar no topo,
  então vejo a tela de Entrar e o caminho para Criar conta, sem perder
  o catálogo ou o carrinho já montado.
- AC-2: Dado Entrar ou Criar conta bem-sucedido a partir do catálogo,
  do carrinho, do checkout ou de meus pedidos, quando a identidade é
  aceita, então volto àquela origem com a sessão ativa.
- AC-3: Dado que não estou identificado no checkout, quando preciso
  confirmar, então posso Entrar ou Criar conta ali, com a mesma regra
  de e-mail e senha da tela própria.

Casos de borda:

- EC-1: E-mail ou senha inválidos → permaneço na identificação com
  explicação; o carrinho não some.
- EC-2: E-mail já usado ao criar conta → recusa e explica que devo
  Entrar; sem “esqueci a senha” neste MVP.
- EC-3: Sessão expirada no meio do checkout → volto à identificação e
  o carrinho permanece.
- EC-4: Já identificado e abro Entrar de propósito → não crio segunda
  conta; sigo identificado (origem ou loja).
- EC-5: Envio repetido do mesmo cadastro ou da mesma entrada → no
  máximo uma sessão ativa; sem contas duplicadas para o mesmo e-mail.
- EC-6: Campos vazios → aponta o que falta, sem seguir adiante.
- EC-7: Abrir Entrar e cancelar / voltar → origem intacta, ainda como
  convidado.
- EC-8: Conta de operador na Entrar da loja → identidade aceita e
  destino a retaguarda, com o topo de operação (US-009), não a origem
  da loja.

## Navegação

### US-006: Topo da loja

**Como** produtor rural, **quero** Catálogo, Carrinho, Checkout e Pedido
no topo, mais Entrar ou minha identificação e Sair, **para que** eu não
procure esses atalhos no rodapé.

Critérios de aceitação:

- AC-1: Dado qualquer tela da loja do produtor (exceto o detalhe em que
  o mapa de UI ocultar o topo), quando olho o cabeçalho, então vejo
  Catálogo, Carrinho, Checkout e Pedido no topo, e não vejo essa barra
  no rodapé.
- AC-2: Dado que não estou identificado, quando olho o topo, então vejo
  Entrar.
- AC-3: Dado que estou identificado como produtor, quando olho o topo,
  então vejo identificação curta (nome ou e-mail) e Sair, e não vejo
  Entrar.
- AC-4: Dado Sair, quando confirmo a saída, então volto a convidado, o
  carrinho de convidado permanece e o topo mostra Entrar de novo.

Casos de borda:

- EC-1: Pedido no topo sem conta → abre meus pedidos e pede Entrar; após
  sucesso, volto a meus pedidos (US-005 / US-008).
- EC-2: Checkout no topo com carrinho vazio → abre o checkout e explica
  que precisa de itens; o atalho não desaparece.
- EC-3: Tela estreita → os atalhos do topo continuam utilizáveis por
  toque e por teclado, sem depender só do rodapé.
- EC-4: Operador autenticado não usa este topo; vê o topo da US-009.
- EC-5: Atualizar a página → permanece o mesmo papel (convidado,
  produtor ou operador) e o mesmo carrinho de convidado.
- EC-6: Dois toques em Sair → uma única saída; sem erro opaco.
- EC-7: Deep link de retaguarda com conta de produtor → acesso negado
  (US-009), sem trocar o topo da loja por topo de operador.

## Checkout e pedido

### US-007: Confirmar o pedido

**Como** produtor rural, **quero** informar só o que falta (conta se
ainda não entrei, propriedade e retirada) e confirmar, **para que** a
revenda receba o pedido local na hora.

Critérios de aceitação:

- AC-1: Dado carrinho com itens e produtor ainda não identificado,
  quando abro o checkout, então posso Entrar ou Criar conta antes de
  confirmar.
- AC-2: Dado produtor já identificado (pelo topo ou pelo checkout),
  quando seleciono uma propriedade própria e uma preferência de
  retirada e confirmo, então o sistema registra um pedido local.
- AC-3: Dado que não tenho propriedade cadastrada, quando estou no
  checkout identificado, então posso registrar uma propriedade ali,
  sem sair para outra área de conta.

Casos de borda:

- EC-1: Propriedade ou retirada ausente → aponta o obrigatório e não
  confirma.
- EC-2: Duas confirmações do mesmo checkout → um só pedido.
- EC-3: Voltar no checkout → preserva o que já foi informado.
- EC-4: Carrinho vazio no checkout → não confirma; explica a falta de
  itens (US-006.EC-2).
- EC-5: Interrupção na confirmação → ou o pedido existe e “Pedido
  recebido” pode ser reaberto, ou nenhum pedido foi criado; sem meio
  termo visível ao produtor.
- EC-6: Produto do carrinho que deixou de ser elegível → não confirma
  esse item; explica o que precisa ajustar.
- EC-7: Conta expirada no confirmar → identificação de novo, carrinho
  intacto.
- EC-8: Propriedades de outro produtor → não aparecem para escolha.

### US-008: Pedido recebido e meus pedidos

**Como** produtor rural, **quero** ver “Pedido recebido” na hora e
reabrir só os meus pedidos, **para que** eu saiba que a revenda
registrou a compra.

Critérios de aceitação:

- AC-1: Dado checkout válido, quando confirmo, então vejo identificador
  do pedido e “Pedido recebido” em até um segundo.
- AC-2: Dado um pedido meu, quando o reabro, então vejo produtos,
  quantidades, total, propriedade, retirada e confirmação atual.
- AC-3: Dado que acabei de entrar a partir de Pedido no topo, quando a
  identidade é aceita, então vejo a lista dos meus pedidos — vazia se
  ainda não houver nenhum.

Casos de borda:

- EC-1: Abrir pedido de outro produtor → acesso negado sem revelar
  itens.
- EC-2: Atualizar a página de “Pedido recebido” → não cria outro pedido.
- EC-3: Lista sem pedidos → estado vazio explícito, com caminho para o
  catálogo.
- EC-4: Convidado em meus pedidos cancela o Entrar → não vê lista de
  outra pessoa; permanece sem dados de pedido.
- EC-5: Confirmação simulada → nesta versão o produtor vê o resultado
  de recebimento bem-sucedido, separado do registro do pedido.
- EC-6: Muitos pedidos → a lista permanece navegável; o detalhe abre um
  pedido por vez.
- EC-7: Endereço de pedido inexistente → não encontrado, sem dados de
  outro pedido.
- EC-8: Operador não usa esta lista como “meus pedidos”; usa a
  retaguarda (US-009).

## Retaguarda

### US-009: Inspecionar pedidos no visual da loja

**Como** operador do revendedor, **quero** listar e abrir os pedidos
recebidos no mesmo visual da loja, com topo só de operação, **para que**
eu acompanhe a prova sem cair no checkout do produtor.

Critérios de aceitação:

- AC-1: Dado acesso de operador, quando abro a retaguarda, então o topo
  mostra pedidos da revenda, identificação e Sair — sem Catálogo,
  Carrinho, Checkout nem Pedido do produtor.
- AC-2: Dado a lista, quando a abro, então vejo identificador, produtor,
  horário, total e status da confirmação, no visual desta versão.
- AC-3: Dado um pedido, quando abro o detalhe, então vejo o snapshot
  imutável de itens e checkout.

Casos de borda:

- EC-1: Conta de produtor abre a retaguarda → acesso negado.
- EC-2: Nenhum pedido → estado vazio explícito.
- EC-3: Confirmação já bem-sucedida repetida → não muda o resultado.
- EC-4: Lista desatualizada → atualizar mostra a confirmação mais
  recente conhecida.
- EC-5: Sair do operador → encerra a sessão de operação; não deixa a
  lista visível a convidado.
- EC-6: Pedido de outra empresa comercial → não aparece (uma empresa
  configurada nesta versão).
- EC-7: Deep link de detalhe sem sessão → pede identificação de
  operador; não mostra o snapshot antes disso.
- EC-8: Identificação falha → permanece fora da lista, com explicação.
