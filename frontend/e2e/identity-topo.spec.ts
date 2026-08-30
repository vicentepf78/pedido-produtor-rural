import { expect, type Page } from "@playwright/test";
import { AURORA, mockarApis, test } from "./helpers/apiMock";

async function adicionarAurora(page: Page) {
  await page.goto("/catalogo");
  await page.getByRole("button", { name: "Adicionar" }).first().click();
  await expect(page.getByLabel(/itens/)).toBeVisible();
}

async function entrarComoAlfa(page: Page) {
  await page.getByLabel("E-mail").fill("produtor.alfa@example.com");
  await page.getByLabel("Senha").fill("Rural#2026Order");
  await page.locator('[data-od-id="btn-auth-submit"]').click();
  await expect(page.locator('[data-od-id="user-name"]')).toBeVisible();
}

async function confirmarSair(page: Page) {
  await page.locator('[data-od-id="btn-sair"]').click();
  await page.locator('[data-od-id="btn-sair"]').click();
  await expect(page.locator('[data-od-id="btn-entrar"]')).toBeVisible();
}

test.describe("Identidade e TopoLoja", () => {
  test.use({ viewport: { width: 390, height: 844 } });

  test("E2E-030 Sair mantém itens de convidado e checkout vazio explica CARRINHO_VAZIO", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/checkout");
    await expect(page.getByText("Adicione ao menos um produto antes do checkout.")).toBeVisible();
    await expect(page.locator('[data-od-id="nav-checkout"]')).toBeVisible();
    await expect(page.locator(".bottom-nav")).toHaveCount(0);

    await adicionarAurora(page);
    await page.locator('[data-od-id="btn-entrar"]').click();
    await entrarComoAlfa(page);
    await expect(page.locator('[data-od-id="user-name"]')).toBeVisible();
    await confirmarSair(page);
    await expect(page.locator('[data-od-id="btn-entrar"]')).toBeVisible();
    await page.getByRole("link", { name: /Carrinho/ }).click();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
  });

  test("E2E-033 Entrar no topo volta à origem com sessão e carrinho", async ({ page }) => {
    await mockarApis(page);
    await adicionarAurora(page);

    const origens = [
      { abrir: "/catalogo", origem: "/catalogo" },
      { abrir: "/carrinho", origem: "/carrinho" },
      { abrir: "/checkout", origem: "/checkout" },
      { abrir: "/meus-pedidos", origem: "/meus-pedidos" },
    ] as const;

    for (const passo of origens) {
      await page.goto(passo.abrir);
      await page.locator('[data-od-id="btn-entrar"]').click();
      await expect(page).toHaveURL(new RegExp(`/entrar\\?origem=${encodeURIComponent(passo.origem).replaceAll("%", "\\%")}`));
      await expect(page.getByRole("heading", { name: "Entrar" })).toBeVisible();
      await expect(page.locator('[data-od-id="link-criar-conta"]')).toBeVisible();
      await entrarComoAlfa(page);
      await expect(page).toHaveURL(new RegExp(`${passo.origem}$`));
      await expect(page.locator('[data-od-id="user-name"]')).toContainText("Produtor Alfa");
      await confirmarSair(page);
      await expect(page.locator('[data-od-id="btn-entrar"]')).toBeVisible();
    }

    await page.goto("/carrinho");
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
  });

  test("E2E-034 credenciais inválidas, vazios, já autenticado e voltar", async ({ page }) => {
    const apis = await mockarApis(page);
    await adicionarAurora(page);
    await page.goto("/entrar?origem=/carrinho");

    await page.locator('[data-od-id="btn-auth-submit"]').click();
    await expect(page.getByText("Informe o e-mail.")).toBeVisible();
    await expect(page.getByText("Informe a senha.")).toBeVisible();

    await page.getByLabel("E-mail").fill("produtor.alfa@example.com");
    await page.getByLabel("Senha").fill("errada");
    await page.locator('[data-od-id="btn-auth-submit"]').click();
    await expect(page.getByText("E-mail ou senha inválidos.")).toBeVisible();
    await expect(page).toHaveURL(/\/entrar/);
    await expect(page.getByText("esqueci a senha", { exact: false })).toHaveCount(0);

    await page.getByRole("button", { name: "Voltar" }).click();
    await expect(page).toHaveURL(/\/carrinho$/);
    await expect(page.locator('[data-od-id="btn-entrar"]')).toBeVisible();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();

    apis.autenticarComo("alfa");
    await page.goto("/entrar?origem=/catalogo");
    await expect(page).toHaveURL(/\/catalogo$/);
    await expect(page.locator('[data-od-id="user-name"]')).toBeVisible();
    await expect(page.getByRole("heading", { name: "Criar conta" })).toHaveCount(0);
  });

  test("E2E-035 operador ignora origem da loja e usa TopoOperador", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/entrar?origem=/checkout");
    await page.getByLabel("E-mail").fill("operador.revenda@example.com");
    await page.getByLabel("Senha").fill("Rural#2026Order");
    await page.locator('[data-od-id="btn-auth-submit"]').click();
    await expect(page).toHaveURL(/\/retaguarda\/pedidos$/);
    await expect(page.locator('[data-od-id="operator-top-bar"]')).toBeVisible();
    await expect(page.getByRole("heading", { name: "Pedidos da revenda" })).toBeVisible();
    await expect(page.locator('[data-od-id="user-name"]')).toBeVisible();
    await expect(page.locator('[data-od-id="btn-sair"]')).toBeVisible();
    await expect(page.locator('[data-od-id="nav-catalogo"]')).toHaveCount(0);
    await expect(page.locator('[data-od-id="nav-carrinho"]')).toHaveCount(0);
    await expect(page.locator('[data-od-id="nav-checkout"]')).toHaveCount(0);
    await expect(page.locator('[data-od-id="nav-pedidos"]')).toHaveCount(0);
  });

  test("E2E-036 cadastro volta à origem e e-mail duplicado aponta para Entrar", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/cadastro?origem=/meus-pedidos");
    await expect(page.getByRole("heading", { name: "Criar conta" })).toBeVisible();
    await expect(page.getByText("esqueci a senha", { exact: false })).toHaveCount(0);
    await expect(page.getByText("Google", { exact: false })).toHaveCount(0);
    await page.getByLabel("Nome").fill("Maria Souza");
    await page.getByLabel("E-mail").fill("maria.souza@example.com");
    await page.getByLabel("Senha").fill("Rural#2026Order");
    await page.locator('[data-od-id="btn-auth-submit"]').click();
    await expect(page).toHaveURL(/\/meus-pedidos$/);
    await expect(page.locator('[data-od-id="user-name"]')).toBeVisible();

    await confirmarSair(page);
    await page.goto("/cadastro?origem=/meus-pedidos");
    await page.getByLabel("Nome").fill("Outro");
    await page.getByLabel("E-mail").fill("produtor.alfa@example.com");
    await page.getByLabel("Senha").fill("Rural#2026Order");
    await page.getByRole("button", { name: "Criar conta" }).click();
    await expect(page.getByText("Este e-mail já está cadastrado. Entre com sua senha ou use outro e-mail.")).toBeVisible();
    await expect(page.locator('[data-od-id="link-entrar"]')).toBeVisible();
    await page.getByRole("button", { name: "Criar conta" }).click();
    await expect(page.getByText("Este e-mail já está cadastrado. Entre com sua senha ou use outro e-mail.")).toBeVisible();
  });

  test("E2E-043 TopoLoja nas telas da loja sem barra no rodapé", async ({ page }) => {
    await mockarApis(page);
    await adicionarAurora(page);
    for (const caminho of ["/catalogo", "/carrinho", "/checkout", "/meus-pedidos"]) {
      await page.goto(caminho);
      await expect(page.locator('[data-od-id="producer-top-bar"]')).toBeVisible();
      await expect(page.getByRole("link", { name: "Catálogo" })).toBeVisible();
      await expect(page.getByRole("link", { name: /Carrinho/ })).toBeVisible();
      await expect(page.getByRole("link", { name: "Checkout" })).toBeVisible();
      await expect(page.getByRole("link", { name: "Pedido" })).toBeVisible();
      await expect(page.locator('[data-od-id="btn-entrar"]')).toBeVisible();
      await expect(page.locator(".bottom-nav")).toHaveCount(0);
    }

    await page.goto("/catalogo");
    await page.locator('[data-od-id="btn-entrar"]').click();
    await entrarComoAlfa(page);
    await expect(page.locator('[data-od-id="user-name"]')).toContainText("Produtor Alfa");
    await expect(page.locator('[data-od-id="btn-entrar"]')).toHaveCount(0);
    await confirmarSair(page);
    await expect(page.locator('[data-od-id="btn-entrar"]')).toBeVisible();
    await page.getByRole("link", { name: /Carrinho/ }).click();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
  });

  test("E2E-044 Pedido sem conta pede Entrar e produtor na retaguarda mantém TopoLoja", async ({ page }) => {
    const apis = await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByRole("link", { name: "Pedido" }).click();
    await expect(page).toHaveURL(/\/meus-pedidos$/);
    await expect(page.getByRole("heading", { name: "Entre para ver seus pedidos" })).toBeVisible();
    await page.locator('[data-od-id="btn-pedidos-entrar"]').click();
    await expect(page).toHaveURL(/\/entrar\?origem=\/meus-pedidos/);
    await entrarComoAlfa(page);
    await expect(page).toHaveURL(/\/meus-pedidos$/);

    await page.locator('[data-od-id="nav-catalogo"]').focus();
    await expect(page.locator('[data-od-id="nav-catalogo"]')).toBeFocused();
    await page.keyboard.press("Enter");
    await expect(page).toHaveURL(/\/catalogo$/);

    apis.autenticarComo("alfa");
    await page.goto("/retaguarda/pedidos");
    await expect(page.getByRole("heading", { name: "Acesso negado" })).toBeVisible();
    await expect(page.locator('[data-od-id="producer-top-bar"]')).toBeVisible();
    await expect(page.locator('[data-od-id="operator-top-bar"]')).toHaveCount(0);
  });

  test("E2E-045 atualizar /catalogo preserva papel e carrinho de convidado", async ({ page }) => {
    const apis = await mockarApis(page);
    await adicionarAurora(page);
    await page.reload();
    await expect(page.locator('[data-od-id="btn-entrar"]')).toBeVisible();
    await page.getByRole("link", { name: /Carrinho/ }).click();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();

    await page.goto("/catalogo");
    await page.locator('[data-od-id="btn-entrar"]').click();
    await entrarComoAlfa(page);
    await expect(page).toHaveURL(/\/catalogo$/);
    await page.reload();
    await expect(page.locator('[data-od-id="user-name"]')).toContainText("Produtor Alfa");

    apis.autenticarComo("operador");
    await page.goto("/catalogo");
    await page.reload();
    await expect(page.locator('[data-od-id="operator-top-bar"]')).toBeVisible();
    await expect(page.getByRole("link", { name: "Pedidos da revenda" })).toBeVisible();
  });
});
