import { expect } from "@playwright/test";
import { AURORA, mockarApis, test } from "./helpers/apiMock";

test.describe("Backoffice móvel", () => {
  test.use({ viewport: { width: 390, height: 844 } });

  test("E2E-009 operador lista e abre detalhe do pedido", async ({ page }) => {
    const apis = await mockarApis(page);
    apis.autenticarComo("operador");
    apis.povoarPedidoDemo();
    await page.goto("/retaguarda/pedidos");
    await expect(page.getByRole("heading", { name: "Pedidos do backoffice" })).toBeVisible();
    await expect(page.getByText("Produtor Alfa")).toBeVisible();
    await expect(page.locator(".pill.success", { hasText: "Aceita" })).toBeVisible();
    await page.locator('[data-od-id="bo-order-ord-demo"]').click();
    await expect(page.getByText("Snapshot imutável do pedido — operador não pode editar.")).toBeVisible();
    await expect(page.getByText(AURORA.nome)).toBeVisible();
    await expect(page.getByText("Fazenda Boa Vista — 420 ha")).toBeVisible();
    await expect(page.getByText("Total do pedido")).toBeVisible();
  });

  test("E2E-010 lista vazia do operador e rota negada ao produtor", async ({ page }) => {
    const apis = await mockarApis(page);
    apis.autenticarComo("operador");
    await page.goto("/retaguarda/pedidos");
    await expect(page.getByRole("heading", { name: "Nenhum pedido" })).toBeVisible();
    await expect(page.getByText("Nenhum pedido recebido nesta revenda.")).toBeVisible();

    apis.autenticarComo("alfa");
    await page.goto("/retaguarda/pedidos");
    await expect(page.getByRole("heading", { name: "Acesso negado" })).toBeVisible();
    await expect(page.getByText("Somente o operador da revenda pode inspecionar os pedidos da retaguarda.")).toBeVisible();
    await expect(page.locator('[data-od-id="backoffice-body"] .order-list-item')).toHaveCount(0);
    await expect(page.locator('[data-od-id="producer-top-bar"]')).toBeVisible();
    await expect(page.locator('[data-od-id="operator-top-bar"]')).toHaveCount(0);
  });
});
