import { expect } from "@playwright/test";
import { AURORA, REGULADO_ID, mockarApis, test } from "./helpers/apiMock";

test.describe("Catálogo móvel", () => {
  test.use({ viewport: { width: 390, height: 844 } });

  test("E2E-001 pesquisar semente mostra descrição curta e adiciona ao carrinho", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await expect(page.getByRole("heading", { name: "AgroRevenda Centro" })).toBeVisible();
    await page.getByLabel("Buscar por nome").fill("semente");
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
    await expect(page.getByText(AURORA.descricaoCurta)).toBeVisible();
    await expect(page.getByRole("tab", { name: "Defensivos" })).toHaveCount(0);
    await expect(page.getByRole("button", { name: "Backoffice (operador)" })).toHaveCount(0);
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await expect(page.getByLabel(/itens/)).toBeVisible();
  });

  test("E2E-002 indisponível, busca vazia e link regulado", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await expect(page.getByRole("heading", { name: "Ureia 45% N" })).toBeVisible();
    await expect(page.getByRole("button", { name: "Indisponível" })).toBeDisabled();
    await expect(page.getByText("Sem imagem").first()).toBeVisible();

    await page.getByLabel("Buscar por nome").fill("xyzzy-sem-produto");
    await expect(page.getByRole("heading", { name: "Nenhum resultado" })).toBeVisible();
    await page.getByRole("button", { name: "Limpar busca" }).click();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();

    await page.goto(`/catalogo/${REGULADO_ID}`);
    await expect(page.getByRole("heading", { name: "Acesso negado" })).toBeVisible();
    await expect(page.getByRole("button", { name: "Adicionar" })).toHaveCount(0);
  });
});
