import { expect } from "@playwright/test";
import { AURORA, mockarApis, test } from "./helpers/apiMock";

test.describe("Meus pedidos", () => {
  test.use({ viewport: { width: 390, height: 844 } });

  test("E2E-040 Pedido recebido com ACEITA em até 1s e refresh não duplica", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("link", { name: /Checkout/ }).click();
    await page.getByLabel("E-mail").fill("produtor.alfa@example.com");
    await page.getByLabel("Senha").fill("Rural#2026Order");
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await page.getByLabel("Propriedade", { exact: true }).selectOption({ label: "Fazenda Norte" });
    await page.getByLabel("Preferência de retirada").selectOption({ label: "Retirar na loja (Centro)" });
    const inicio = Date.now();
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await expect(page.getByRole("heading", { name: "Pedido recebido" }).first()).toBeVisible();
    expect(Date.now() - inicio).toBeLessThan(1000);
    await expect(page.getByText("Aceita")).toBeVisible();
    const identificador = (await page.locator("[data-od-id='order-id']").innerText()).trim();
    expect(identificador).toMatch(/^ord-/);

    await page.reload();
    await expect(page.getByRole("heading", { name: "Pedido recebido" }).first()).toBeVisible();
    await expect(page.locator("[data-od-id='order-id']")).toHaveText(identificador);

    await page.getByRole("link", { name: /Pedido/ }).click();
    await page.locator(`[data-od-id="my-order-${identificador}"]`).click();
    await expect(page.getByText(AURORA.nome)).toBeVisible();
    await expect(page.getByText("Fazenda Norte")).toBeVisible();
    await expect(page.getByText("Retirar na loja (Centro)")).toBeVisible();
    await expect(page.getByText("Total do pedido")).toBeVisible();
  });

  test("E2E-041 lista vazia, povoada e muitos pedidos; convidado pede Entrar", async ({ page }) => {
    const apis = await mockarApis(page);
    await page.goto("/meus-pedidos");
    await expect(page.getByRole("heading", { name: "Entre para ver seus pedidos" })).toBeVisible();
    await page.locator('[data-od-id="btn-pedidos-entrar"]').click();
    await expect(page.getByRole("heading", { name: "Entrar" })).toBeVisible();
    await page.getByRole("button", { name: "Voltar" }).click();
    await expect(page.getByRole("heading", { name: "Entre para ver seus pedidos" })).toBeVisible();
    await expect(page.locator('[data-od-id^="my-order-"]')).toHaveCount(0);

    apis.autenticarComo("alfa");
    await page.reload();
    await expect(page.getByRole("heading", { name: "Nenhum pedido" })).toBeVisible();
    await expect(page.getByRole("link", { name: "Ir ao catálogo" })).toBeVisible();

    apis.povoarMuitosPedidos(12);
    await page.reload();
    await expect(page.locator('[data-od-id^="my-order-"]')).toHaveCount(12);
    await page.locator('[data-od-id="my-order-ord-lista-1"]').click();
    await expect(page.getByRole("heading", { name: "Pedido recebido" }).first()).toBeVisible();
    await expect(page.locator('[data-od-id="my-order-ord-lista-2"]')).toHaveCount(0);
  });

  test("E2E-042 pedido de outro produtor e endereço inexistente", async ({ page }) => {
    const apis = await mockarApis(page);
    apis.autenticarComo("alfa");
    apis.povoarPedidoBeta();
    await page.goto("/pedidos/ord-beta");
    await expect(page.getByRole("heading", { name: "Acesso negado" })).toBeVisible();
    await expect(page.getByText("Você não pode visualizar este pedido.")).toBeVisible();
    await expect(page.getByText("Fazenda Sul")).toHaveCount(0);

    await page.goto("/pedidos/ord-inexistente");
    await expect(page.getByText("Pedido não encontrado.")).toBeVisible();
    await expect(page.getByText(AURORA.nome)).toHaveCount(0);
  });
});
