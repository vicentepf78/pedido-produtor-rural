import { expect } from "@playwright/test";
import { AURORA, UREIA, mockarApis, test } from "./helpers/apiMock";

test.describe("Carrinho", () => {
  test.use({ viewport: { width: 390, height: 844 } });

  test("E2E-029 convidado monta o carrinho, altera, remove e mantém preço após Entrar", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("link", { name: /Carrinho/ }).click();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
    await expect(page.getByText("620,00").first()).toBeVisible();
    await expect(page.locator(".line-total")).toContainText("1.240,00");
    await expect(page.getByText("Total do pedido")).toBeVisible();

    const qtd = page.getByLabel("Qtd");
    await qtd.fill("3");
    await expect(page.locator(".line-total")).toContainText("1.860,00");
    await qtd.fill("2");
    await qtd.fill("4");
    await expect(page.locator(".cart-line")).toHaveCount(1);
    await expect(page.locator(".line-total")).toContainText("2.480,00");

    await page.getByRole("button", { name: "Remover" }).click();
    await expect(page.getByRole("heading", { name: "Carrinho vazio" })).toBeVisible();

    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("link", { name: /Carrinho/ }).click();
    await expect(page.getByText("620,00").first()).toBeVisible();
    await page.locator('[data-od-id="btn-entrar"]').click();
    await page.getByLabel("E-mail").fill("produtor.alfa@example.com");
    await page.getByLabel("Senha").fill("Rural#2026Order");
    await page.locator('[data-od-id="btn-auth-submit"]').click();
    await expect(page.locator('[data-od-id="user-name"]')).toBeVisible();
    await page.getByRole("link", { name: /Carrinho/ }).click();
    await expect(page.getByText("620,00").first()).toBeVisible();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
  });

  test("E2E-031 quantidade inválida, indisponível e mesma linha ao adicionar de novo", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("link", { name: /Carrinho/ }).click();
    const qtd = page.getByLabel("Qtd");
    await qtd.fill("0");
    await expect(page.getByText("Informe uma quantidade inteira positiva.")).toBeVisible();
    await qtd.fill("-2");
    await expect(page.getByText("Informe uma quantidade inteira positiva.")).toBeVisible();

    await page.goto("/catalogo?categoria=Fertilizantes");
    await expect(page.getByRole("heading", { name: UREIA.nome })).toBeVisible();
    await expect(page.getByRole("button", { name: "Indisponível" })).toBeVisible();
    await page.getByRole("link", { name: /Carrinho/ }).click();
    await expect(page.getByRole("heading", { name: UREIA.nome })).toHaveCount(0);

    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("link", { name: /Carrinho/ }).click();
    await expect(page.locator(".cart-line")).toHaveCount(1);
    await expect(page.locator(".line-total")).toContainText("1.240,00");
  });

  test("E2E-032 cem linhas de fixture com totais legíveis", async ({ page }) => {
    const apis = await mockarApis(page);
    apis.povoarCemLinhas();
    await page.goto("/carrinho");
    await expect(page.locator(".cart-line")).toHaveCount(100);
    await expect(page.getByRole("heading", { name: "Produto fixture 100" })).toBeVisible();
    await expect(page.getByText("Total do pedido")).toBeVisible();
    await expect(page.locator("[data-od-id='cart-summary'] .mono")).toContainText("125,00");
  });
});
