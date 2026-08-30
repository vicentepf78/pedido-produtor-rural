# J-convidado-retoma-carrinho

Jornada canário adjacente ao pedido rápido: o carrinho de convidado sobrevive a
abandono, reload e logout de identidade. Compartilha cookie e mutações com
J-produtor-pedido-rapido.

```mermaid
flowchart TD
    E[Entrada: /catalogo sem conta] --> A[Adiciona item; cookie chaveCarrinhoConvidado]
    A --> B[Vê totais em /carrinho]
    B --> C{Interrompe}
    C -->|fecha a aba| D[Abandono]
    C -->|recarrega| E1[GET /api/v1/carrinhos/convidado]
    C -->|rede cai na mutação| E2[Último estado confirmado preservado]
    C -->|POST autenticacao/saida| E3[Sessão some; cookie do carrinho fica]
    D --> F[Volta no mesmo navegador]
    F --> E1
    E2 --> B
    E3 --> E1
    E1 -->|cookie intacto| G[True end: mesmas linhas e total do último sucesso]
    E1 -->|dados do navegador limpos| X[Carrinho vazio esperado; checkout bloqueado]
```

```yaml
journey:
  id: J-convidado-retoma-carrinho
  name: Retomar carrinho de convidado
  value_statement: "O produtor reencontra o carrinho que montou sem conta, mesmo depois de sair ou recarregar."
  personas: [Produtora no campo, Produtor rural]
  entry_points:
    - url: /carrinho
      origin: in-app-nav
    - url: GET /api/v1/carrinhos/convidado
      origin: in-app-nav
    - url: cookie chaveCarrinhoConvidado
      origin: direct
  actions:
    - step: 1
      verb: Adiciona um item sem se identificar
      expected_observable: Cookie de convidado gravado; totais visíveis
    - step: 2
      verb: Sai, recarrega ou interrompe a rede
      expected_observable: Nenhum pedido criado; identidade não apaga o carrinho
    - step: 3
      verb: Volta ao carrinho
      expected_observable: Mesmas linhas e total do último estado confirmado
  goal:
    observable: Carrinho retomado igual ao último sucesso, sem pedido fantasma
    side_effects: []
  true_end_state: GET /api/v1/carrinhos/convidado e /carrinho mostram o mesmo total após o retorno
  exit:
    natural: Carrinho pronto para checkout ou abandono consciente
  abandonment:
    - at_step: 2
      how: Fecha o celular no meio do ajuste de quantidade
      resume: Ao desbloquear, o último PATCH/POST bem-sucedido ainda está lá
  crosses: [cart, identity]
```
