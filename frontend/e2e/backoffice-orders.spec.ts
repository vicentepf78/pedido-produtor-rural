import { expect } from "@playwright/test";
import { AURORA, mockarApis, test } from "./helpers/apiMock";

test.describe("Retaguarda", () => {
  test.use({ viewport: { width: 390, height: 844 } });

  test("E2E-046 lista da retaguarda no visual da loja, atualizar e outra empresa ausente", async ({ page }) => {
    const apis = await mockarApis(page);
    apis.autenticarComo("operador");
    apis.povoarPedidoDemo();
    apis.povoarPedidoOutraEmpresa();
    await page.goto("/retaguarda/pedidos");
    await expect(page.getByRole("heading", { name: "Pedidos da revenda" })).toBeVisible();
    await expect(page.getByText("Produtor Alfa")).toBeVisible();
    await expect(page.locator(".pill.success", { hasText: "Aceita" })).toBeVisible();
    await expect(page.getByText("ord-outra-empresa")).toHaveCount(0);
    await page.reload();
    await expect(page.locator(".pill.success", { hasText: "Aceita" })).toBeVisible();
    await expect(page.getByText("Produtor Alfa")).toBeVisible();
  });

  test("E2E-046b tabela da retaguarda legível em desktop", async ({ page }) => {
    const apis = await mockarApis(page);
    apis.autenticarComo("operador");
    apis.povoarPedidoDemo();
    await page.setViewportSize({ width: 1440, height: 900 });
    await page.goto("/retaguarda/pedidos");
    await expect(page.locator("[data-od-id='orders-table']")).toBeVisible();
    await expect(page.getByRole("columnheader", { name: "Pedido" })).toBeVisible();
    await expect(page.getByRole("columnheader", { name: "Produtor" })).toBeVisible();
    await expect(page.getByText("Produtor Alfa")).toBeVisible();
  });

  test("E2E-047 detalhe da retaguarda com snapshot imutável", async ({ page }) => {
    const apis = await mockarApis(page);
    apis.autenticarComo("operador");
    apis.povoarPedidoDemo();
    await page.goto("/retaguarda/pedidos");
    await page.locator('[data-od-id="bo-order-ord-demo"]').click();
    await expect(page.getByText("Snapshot imutável do pedido — operador não pode editar.")).toBeVisible();
    await expect(page.getByText(AURORA.nome)).toBeVisible();
    await expect(page.getByText("Fazenda Norte")).toBeVisible();
    await expect(page.getByText("Retirar na loja (Centro)")).toBeVisible();
    await expect(page.getByText("Total do pedido")).toBeVisible();
  });

  test("E2E-048 TopoOperador sem atalhos de compra; Sair some a lista", async ({ page }) => {
    const apis = await mockarApis(page);
    apis.autenticarComo("operador");
    apis.povoarPedidoDemo();
    await page.goto("/retaguarda/pedidos");
    await expect(page.locator('[data-od-id="operator-top-bar"]')).toBeVisible();
    await expect(page.locator('[data-od-id="nav-pedidos-revenda"]')).toBeVisible();
    await expect(page.locator('[data-od-id="nav-catalogo"]')).toHaveCount(0);
    await expect(page.locator('[data-od-id="nav-carrinho"]')).toHaveCount(0);
    await expect(page.locator('[data-od-id="nav-checkout"]')).toHaveCount(0);
    await expect(page.getByRole("link", { name: /^Pedido$/ })).toHaveCount(0);

    await page.goto("/meus-pedidos");
    await expect(page.getByRole("heading", { name: "Pedidos da revenda" })).toBeVisible();
    await expect(page.locator('[data-od-id^="my-order-"]')).toHaveCount(0);

    await page.goto("/retaguarda/pedidos");
    await page.locator('[data-od-id="btn-sair"]').click();
    await page.locator('[data-od-id="btn-sair"]').click();
    await expect(page.locator('[data-od-id="btn-entrar"]')).toBeVisible();
    await expect(page.locator('[data-od-id="orders-table"]')).toHaveCount(0);
    await expect(page.locator('[data-od-id="bo-order-ord-demo"]')).toHaveCount(0);
  });

  test("E2E-049 produtor negado, lista vazia, deep link sem sessão e identificação falha", async ({ page }) => {
    const apis = await mockarApis(page);
    apis.autenticarComo("alfa");
    await page.goto("/retaguarda/pedidos");
    await expect(page.getByRole("heading", { name: "Acesso negado" })).toBeVisible();
    await expect(page.getByText("Somente o operador da revenda pode inspecionar os pedidos da retaguarda.")).toBeVisible();
    await expect(page.locator('[data-od-id="producer-top-bar"]')).toBeVisible();
    await expect(page.locator('[data-od-id="operator-top-bar"]')).toHaveCount(0);
    await expect(page.locator('[data-od-id="orders-table"]')).toHaveCount(0);

    apis.autenticarComo("operador");
    await page.goto("/retaguarda/pedidos");
    await expect(page.getByRole("heading", { name: "Nenhum pedido" })).toBeVisible();
    await expect(page.getByText("Nenhum pedido recebido nesta revenda.")).toBeVisible();

    apis.expirarSessao();
    apis.povoarPedidoDemo();
    await page.goto("/retaguarda/pedidos/ord-demo");
    await expect(page.getByRole("heading", { name: "Entrar" })).toBeVisible();
    await expect(page.getByText("Snapshot imutável do pedido — operador não pode editar.")).toHaveCount(0);
    await page.getByLabel("E-mail").fill("operador.revenda@example.com");
    await page.getByLabel("Senha").fill("errada");
    await page.locator('[data-od-id="btn-auth-submit"]').click();
    await expect(page.getByText("E-mail ou senha inválidos.")).toBeVisible();
    await expect(page.getByRole("heading", { name: "Entrar" })).toBeVisible();
    await expect(page.getByText(AURORA.nome)).toHaveCount(0);
  });
});
