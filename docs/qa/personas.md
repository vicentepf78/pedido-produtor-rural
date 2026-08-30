# Personas do MVP0

Instâncias do produto — não o catálogo-semente. Atualizar só quando o público mudar.

O MVP0 é mobile-first e tem restrições de acessibilidade em `_uiux.md`. Há persona
baseada em Mobile User e em Accessibility-Reliant.

## Produtor rural

```yaml
persona:
  name: Produtor rural
  base: New User
  goal: Montar um pedido pequeno de insumo não regulamentado em poucos minutos, sem treinar no sistema
  device: phone-large
  network: 4g
  modality: touch
  locale: pt-BR
  patience_seconds: 60
```

Primeira visita à loja da revenda. Avalia se o fluxo é mais rápido que ligar para
o balcão. Abandona se o catálogo confundir, se pedir conta cedo demais ou se a
confirmação não aparecer.

## Operador da revenda

```yaml
persona:
  name: Operador da revenda
  base: Power User
  goal: Ver se o pedido do produtor chegou e se a confirmação simulada ficou ACEITA
  device: desktop
  network: wifi-fast
  modality: mouse-keyboard
  locale: pt-BR
  patience_seconds: 20
```

Usa a retaguarda várias vezes ao dia. Espera lista rápida, detalhe somente leitura
e isolamento de papel. Não administra catálogo nem preço neste MVP.

## Produtora no campo

```yaml
persona:
  name: Produtora no campo
  base: Mobile User
  goal: Ajustar quantidade no celular com uma mão e retomar o carrinho depois de uma interrupção
  device: phone-small
  network: flaky
  modality: touch
  locale: pt-BR
  patience_seconds: 30
```

Viewport estreito, rede instável, toque. Revela alvos pequenos, layout em 375px,
perda de carrinho ao voltar e spinner sem timeout.

## Produtor com leitor de tela

```yaml
persona:
  name: Produtor com leitor de tela
  base: Accessibility-Reliant
  goal: Completar o mesmo pedido rápido com teclado ou leitor, sem depender de hover
  device: laptop
  network: wifi-fast
  modality: keyboard-only
  locale: pt-BR
  patience_seconds: 90
```

Precisa de rótulos, foco visível, `aria-invalid` nos erros e estados anunciáveis.
Neste ciclo targeted não recebe charter próprio — as sessões registram paper cuts
desta persona quando aparecerem.

## Produtor após falha

```yaml
persona:
  name: Produtor após falha
  base: Recovering User
  goal: Conferir se o erro do checkout (credencial, campo obrigatório ou sessão) não comeu o carrinho
  device: phone-large
  network: 4g
  modality: touch
  locale: pt-BR
  patience_seconds: 20
```

Volta depois de um erro. Qualquer sinal do mesmo fracasso (carrinho vazio, senha
ecoada, mensagem genérica) provoca abandono.
