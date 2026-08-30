import { expect } from "@playwright/test";
import { AURORA, mockarApis, test } from "./helpers/apiMock";

test.describe("Checkout", () => {
  test.use({ viewport: { width: 390, height: 844 } });

  test("E2E-037 identidade no checkout, Fazenda Norte e cadastro de propriedade", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("link", { name: /Checkout/ }).click();
    await expect(page.getByRole("tab", { name: "Entrar" })).toBeVisible();
    await expect(page.getByRole("tab", { name: "Criar conta" })).toBeVisible();

    await page.getByLabel("E-mail").fill("produtor.alfa@example.com");
    await page.getByLabel("Senha").fill("Rural#2026Order");
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await expect(page.getByLabel("Propriedade", { exact: true }).locator("option", { hasText: "Fazenda Norte" })).toHaveCount(1);
    await expect(page.getByLabel("Propriedade", { exact: true }).locator("option", { hasText: "Fazenda Sul" })).toHaveCount(0);
    await page.getByLabel("Propriedade", { exact: true }).selectOption({ label: "Fazenda Norte" });
    await page.getByLabel("Preferência de retirada").selectOption({ label: "Retirar na loja (Centro)" });
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await expect(page.getByRole("heading", { name: "Pedido recebido" }).first()).toBeVisible();
    await expect(page.getByText(AURORA.nome)).toBeVisible();
  });

  test("E2E-037c produtor já autenticado pula Entrar no checkout", async ({ page }) => {
    const apis = await mockarApis(page);
    apis.autenticarComo("alfa");
    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("link", { name: /Checkout/ }).click();
    await expect(page.getByRole("tab", { name: "Entrar" })).toHaveCount(0);
    await expect(page.getByLabel("Propriedade", { exact: true })).toBeVisible();
    await expect(page.getByLabel("Propriedade", { exact: true }).locator("option", { hasText: "Fazenda Norte" })).toHaveCount(1);
    await expect(page.getByLabel("Preferência de retirada")).toBeVisible();
  });

  test("E2E-037b registra Fazenda Santa Luzia no próprio checkout", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("link", { name: /Checkout/ }).click();
    await page.getByRole("tab", { name: "Criar conta" }).click();
    await page.getByLabel("Nome", { exact: true }).fill("Maria Souza");
    await page.getByLabel("E-mail").fill("maria.souza@example.com");
    await page.getByLabel("Senha").fill("Rural#2026Order");
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await page.getByLabel("Nome da propriedade").fill("Fazenda Santa Luzia");
    await page.getByLabel("Preferência de retirada").selectOption({ label: "Retirar na loja (Centro)" });
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await expect(page.getByRole("heading", { name: "Pedido recebido" }).first()).toBeVisible();
    await expect(page.getByText("Fazenda Santa Luzia")).toBeVisible();
  });

  test("E2E-038 dados obrigatórios, voltar preserva, sessão expirada e item inelegível", async ({ page }) => {
    const apis = await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("link", { name: /Checkout/ }).click();
    await page.getByLabel("E-mail").fill("produtor.alfa@example.com");
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await expect(page.getByText("Informe a senha.")).toBeVisible();
    await expect(page.getByLabel("Senha")).toBeFocused();
    await expect(page.getByLabel("E-mail")).toHaveValue("produtor.alfa@example.com");

    await page.getByLabel("Senha").fill("Rural#2026Order");
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await expect(page.getByText("Selecione uma propriedade.")).toBeVisible();
    await expect(page.getByLabel("Propriedade", { exact: true })).toBeFocused();

    await page.getByRole("link", { name: /Carrinho/ }).click();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
    await page.getByRole("link", { name: /Checkout/ }).click();
    await expect(page.getByRole("tab", { name: "Entrar" })).toHaveCount(0);
    await expect(page.getByLabel("Propriedade", { exact: true })).toBeVisible();

    await page.getByLabel("Propriedade", { exact: true }).selectOption({ label: "Fazenda Norte" });
    await page.getByLabel("Preferência de retirada").selectOption({ label: "Retirar na loja (Centro)" });
    apis.expirarSessao();
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await expect(page.getByText("Entre ou crie uma conta para continuar.")).toBeVisible();
    await expect(page.getByRole("tab", { name: "Entrar" })).toBeVisible();
    await page.getByRole("link", { name: /Carrinho/ }).click();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();

    await page.getByRole("link", { name: /Checkout/ }).click();
    await page.getByLabel("E-mail").fill("produtor.alfa@example.com");
    await page.getByLabel("Senha").fill("Rural#2026Order");
    apis.marcarProximoPedidoInelegivel();
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await expect(page.getByText("Este produto não pode ser pedido nesta loja.")).toBeVisible();
    await expect(page.getByRole("heading", { name: "Pedido recebido" })).toHaveCount(0);
  });

  test("E2E-039 duas confirmações geram um pedido; sair sem confirmar não cria meio termo", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("link", { name: /Checkout/ }).click();
    await page.getByLabel("E-mail").fill("produtor.alfa@example.com");
    await page.getByLabel("Senha").fill("Rural#2026Order");
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await page.getByLabel("Propriedade", { exact: true }).selectOption({ label: "Fazenda Norte" });
    await page.getByLabel("Preferência de retirada").selectOption({ label: "Retirar na loja (Centro)" });
    await Promise.all([
      page.getByRole("button", { name: "Confirmar pedido" }).click(),
      page.getByRole("button", { name: "Confirmar pedido" }).click(),
    ]);
    await expect(page.getByRole("heading", { name: "Pedido recebido" }).first()).toBeVisible();
    await page.goto("/meus-pedidos");
    await expect(page.locator('[data-od-id^="my-order-"]')).toHaveCount(1);

    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("link", { name: /Checkout/ }).click();
    await page.getByRole("link", { name: /Carrinho/ }).click();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
    await page.goto("/meus-pedidos");
    await expect(page.locator('[data-od-id^="my-order-"]')).toHaveCount(1);
  });
});
