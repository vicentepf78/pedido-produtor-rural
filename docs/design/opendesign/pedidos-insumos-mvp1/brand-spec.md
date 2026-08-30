# Brand spec — Loja de insumos (referência Mercado Livre)

Paleta e tipografia inspiradas na linguagem visual do Mercado Livre: header amarelo/dourado, cards brancos, fundo cinza claro, sans-serif de alto contraste.

## Tokens OKLch

```css
:root {
  --bg: oklch(0.94 0.004 260);
  --surface: oklch(1 0 0);
  --fg: oklch(0.28 0.02 260);
  --muted: oklch(0.52 0.02 260);
  --border: oklch(0.88 0.01 260);
  --accent: oklch(0.58 0.19 252);
  --header: oklch(0.91 0.17 95);
  --header-fg: oklch(0.28 0.04 260);
  --success: oklch(0.62 0.17 145);
  --warn: oklch(0.75 0.15 85);
  --danger: oklch(0.55 0.2 25);
}
```

## Font stacks

- **Display / UI:** `"Nunito Sans", "Helvetica Neue", Arial, sans-serif`
- **Body:** `"Source Sans 3", "Segoe UI", sans-serif`
- **Mono:** `"IBM Plex Mono", ui-monospace, monospace`

## Regras visuais

1. Header fixo amarelo (`--header`) com navegação horizontal; sombra sutil separando do conteúdo.
2. Cards brancos com borda `--border`, raio 8px, sombra leve — nunca barra colorida à esquerda.
3. CTAs primários em azul ML (`--accent`); amarelo reservado ao header e destaques de navegação ativa.
4. Fundo de página `--bg`; áreas de conteúdo respiram com padding generoso no mobile (16px).
5. Tipografia sans-serif densa e legível; labels de botão com `letter-spacing: 0.02em`.

**Resumo:** Marketplace rural brasileiro com energia amarela no topo, cards limpos e fluxo direto — familiar ao produtor que já compra online.
