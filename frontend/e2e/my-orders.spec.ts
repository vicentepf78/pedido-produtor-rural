import { expect } from "@playwright/test";
import { AURORA, mockarApis, test } from "./helpers/apiMock";

test.describe("Meus pedidos móvel", () => {
  test.use({ viewport: { width: 390, height: 844 } });

  test("E2E-007 revisitar pedido mostra snapshot imutável", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("link", { name: /Checkout/ }).click();
    await page.getByRole("tab", { name: "Criar conta" }).click();
    await page.getByLabel("Nome").fill("Maria Souza");
    await page.getByLabel("E-mail").fill("maria.souza@example.com");
    await page.getByLabel("Senha").fill("Rural#2026Order");
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await expect(page.getByLabel("Propriedade").locator("option", { hasText: "Fazenda Boa Vista" })).toHaveCount(1);
    await page.getByLabel("Propriedade").selectOption({ label: "Fazenda Boa Vista — 420 ha" });
    await page.getByLabel("Preferência de retirada").selectOption({ label: "Retirar na loja (Centro)" });
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await expect(page.getByRole("heading", { name: "Pedido recebido" }).first()).toBeVisible();
    const identificador = (await page.locator(".detail-row dd").first().innerText()).trim();
    expect(identificador).toMatch(/^ord-/);

    await page.getByRole("button", { name: /Meus pedidos/ }).click();
    await expect(page.getByRole("heading", { name: "Meus pedidos" })).toBeVisible();
    await page.locator(`[data-od-id="my-order-${identificador}"]`).click();

    await expect(page.getByText(AURORA.nome)).toBeVisible();
    await expect(page.getByText("Fazenda Boa Vista — 420 ha")).toBeVisible();
    await expect(page.getByText("Retirar na loja (Centro)")).toBeVisible();
    await expect(page.getByText("Total do pedido")).toBeVisible();
    await expect(page.locator("[data-od-id='order-detail-total'] .mono")).toBeVisible();
    await expect(page.locator(".pill.neutral", { hasText: "Recebido" })).toBeVisible();
  });

  test("E2E-008 outro produtor vê acesso negado no link direto", async ({ page }) => {
    const apis = await mockarApis(page);
    apis.autenticarComo("alfa");
    apis.povoarPedidoDemo();
    apis.negarPedido("ord-demo");
    await page.goto("/pedidos/ord-demo");
    await expect(page.getByRole("heading", { name: "Acesso negado" })).toBeVisible();
    await expect(page.getByText("Você não pode visualizar este pedido.")).toBeVisible();
  });
});
