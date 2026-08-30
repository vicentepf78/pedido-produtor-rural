# VC-02 — Checkout inválido com erro inline (390×844)

## Veredito: PASS

Propriedade e retirada vazias com `aria-invalid` e copy “Selecione uma propriedade.” / “Selecione a preferência de retirada.” nos dois lados.

## Divergências autorizadas
- E-mail/senha com erro no wireframe e sem erro na implementação: sessão `sessao` já ativa (VC-01 / task_02); `validarIdentidade` não revalida identidade autenticada. E2E-006 cobre o caminho não autenticado.
- CTA do wireframe fica desabilitado quando inválido; a implementação mantém o botão ativo com itens no carrinho para o produtor poder ver o erro (E2E-006).

## Bloqueios
Nenhum. `blocking_divergences: 0`.
