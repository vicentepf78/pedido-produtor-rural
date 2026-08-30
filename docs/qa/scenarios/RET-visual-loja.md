---
id: RET-visual-loja
area: RET
title: Retaguarda no visual da loja com TopoOperador
persona: Operador da revenda
journey: J-operador-retaguarda
expected: Lista e detalhe da retaguarda usam o visual da loja; TopoOperador mostra Pedidos da revenda, identificação e Sair, sem atalhos de compra; chegada só pela Entrar da loja
entry_points: /retaguarda/pedidos; /retaguarda/pedidos/{idPedido}; /entrar; TopoOperador
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence:
last_report:
overlaps: ORD-backoffice-lista; ORD-backoffice-vazio-ou-negado; NAV-topo-loja
---

O operador chega pela Entrar da loja (ADR-007). Não há login inline nem botão “Modo retaguarda”. A tabela e o snapshot permanecem somente leitura.
