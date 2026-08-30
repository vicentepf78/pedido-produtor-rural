import { expect } from "@playwright/test";
import { AURORA, mockarApis, test } from "./helpers/apiMock";

test.describe("Carrinho móvel", () => {
  test.use({ viewport: { width: 390, height: 844 } });

  test("E2E-003 alterar quantidade, remover e 100 linhas", async ({ page }) => {
    const apis = await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("link", { name: /Carrinho/ }).click();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
    const qtd = page.getByLabel("Qtd");
    await qtd.fill("3");
    await expect(page.getByText("Total do pedido")).toBeVisible();
    await expect(page.locator(".line-total")).toContainText("1.860,00");
    await page.getByRole("button", { name: "Remover" }).click();
    await expect(page.getByRole("heading", { name: "Carrinho vazio" })).toBeVisible();

    apis.povoarCemLinhas();
    await page.reload();
    await expect(page.locator(".cart-line")).toHaveCount(100);
    await expect(page.getByRole("heading", { name: "Produto fixture 100" })).toBeVisible();
  });

  test("E2E-004 checkout bloqueado no vazio e recarregar preserva carrinho", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/carrinho");
    await expect(page.getByRole("heading", { name: "Carrinho vazio" })).toBeVisible();
    await expect(page.getByRole("button", { name: "Ir para checkout" })).toBeDisabled();

    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("link", { name: /Carrinho/ }).click();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
    await page.reload();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
    await expect(page.getByRole("button", { name: "Ir para checkout" })).toBeEnabled();
  });
});
