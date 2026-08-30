# ADR-001: Validar pedido rápido com produtos não regulamentados

## Status

Aceito

## Data

2026-08-30

## Contexto

O produto busca substituir uma jornada lenta de pedido agrícola, dependente de ERP,
por um fluxo rápido voltado ao produtor. Produtos de proteção de cultivos podem exigir
prescrição e conformidade específica por estado, o que obscureceria a hipótese de
experiência durante a primeira prova comercial.

## Decisão

O MVP0 atende uma revenda com um catálogo selecionado de 30 produtos não
regulamentados. Ele valida descoberta, carrinho, identificação tardia, seleção de
propriedade, preferência de retirada, confirmação de pedido local e uma confirmação
simulada subsequente.

## Alternativas consideradas

### Incluir produtos regulamentados

- **Descrição:** Adicionar produtos de proteção de cultivos e seus controles
  operacionais.
- **Prós:** Mais próximo do catálogo eventual do piloto.
- **Contras:** Adiciona dependências de prescrição, estado e conformidade.
- **Motivo da rejeição:** Testa o fluxo de conformidade em vez da hipótese de pedido
  rápido.

### Construir primeiro o MVP piloto completo

- **Descrição:** Entregar importação de catálogo, análise comercial, reserva e
  notificações antes dos testes com usuários.
- **Prós:** Produz uma solução mais completa.
- **Contras:** Adia o feedback e amplia substancialmente a primeira entrega.
- **Motivo da rejeição:** O objetivo atual é uma prova de conceito comercial restrita.

## Consequências

### Positivas

- O primeiro teste isola a jornada do produtor.
- O escopo permanece viável como uma pequena fatia vertical de produto.
- O trabalho posterior de conformidade pode se basear em um fluxo de pedido comprovado.

### Negativas

- O MVP0 não pode validar vendas de produtos regulamentados de proteção de cultivos.
- O catálogo selecionado exige preparação manual.

### Riscos

- Usuários de teste podem esperar condições de pagamento, entrega ou preço.
  Mitigação: identificar o MVP0 como uma prova de conceito e capturar feedback para o
  MVP piloto.

## Notas de implementação

O desenho técnico deve manter a confirmação simulada fora da autoridade do pedido
local e impedir que produtos regulamentados entrem no catálogo do MVP0.

## Referências

- [_idea.md](../_idea.md)
- [_spec.md](../_spec.md)
- [Narrativa de arquitetura](../../../../agri_platform_mvp_arquitetura.md)
