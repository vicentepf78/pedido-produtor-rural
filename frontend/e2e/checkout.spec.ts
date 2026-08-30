import { expect } from "@playwright/test";
import { AURORA, mockarApis, test } from "./helpers/apiMock";

test.describe("Checkout móvel", () => {
  test.use({ viewport: { width: 390, height: 844 } });

  test("E2E-005 criar conta, propriedade, retirada e Pedido recebido em até 1s", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("link", { name: /Carrinho/ }).click();
    await page.getByRole("button", { name: "Ir para checkout" }).click();

    await page.getByRole("tab", { name: "Criar conta" }).click();
    await page.getByLabel("Nome").fill("Maria Souza");
    await page.getByLabel("E-mail").fill("maria.souza@example.com");
    await page.getByLabel("Senha").fill("Rural#2026Order");
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await expect(page.getByLabel("Propriedade").locator("option", { hasText: "Fazenda Boa Vista" })).toHaveCount(1);
    await page.getByLabel("Propriedade").selectOption({ label: "Fazenda Boa Vista — 420 ha" });
    await page.getByLabel("Preferência de retirada").selectOption({ label: "Retirar na loja (Centro)" });

    const inicio = Date.now();
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await expect(page.getByRole("heading", { name: "Pedido recebido" }).first()).toBeVisible();
    await expect(page.getByText(/Identificador/)).toBeVisible();
    expect(Date.now() - inicio).toBeLessThanOrEqual(1000);
    await expect(page.getByText(AURORA.nome)).toBeVisible();
  });

  test("E2E-006 erros de checkout em foco e carrinho preservado", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("link", { name: /Checkout/ }).click();

    await page.getByLabel("E-mail").fill("joao.silva@example.com");
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await expect(page.getByText("Informe a senha.")).toBeVisible();
    await expect(page.getByLabel("Senha")).toBeFocused();
    await expect(page.getByLabel("E-mail")).toHaveValue("joao.silva@example.com");

    await page.getByLabel("Senha").fill("Rural#2026Order");
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await expect(page.getByText("Selecione uma propriedade.")).toBeVisible();
    await expect(page.getByLabel("Propriedade")).toBeFocused();

    await page.getByRole("link", { name: /Carrinho/ }).click();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
    await expect(page.getByText("Total do pedido")).toBeVisible();
  });
});
