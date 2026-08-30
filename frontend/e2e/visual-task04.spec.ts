import { mkdirSync } from "node:fs";
import { join } from "node:path";
import { expect, type Page, test } from "@playwright/test";
import { mockarApis } from "./helpers/apiMock";

const evidencias = join(
  process.cwd(),
  "../.compozy/tasks/pedidos-insumos-mvp0/evidence/visual/task_04",
);
const wireUrl =
  "file://" +
  join(
    process.cwd(),
    "../.compozy/tasks/pedidos-insumos-mvp0/references/mvp0-pedidos-insumos.html",
  );

function pasta(id: string) {
  const dir = join(evidencias, id);
  mkdirSync(dir, { recursive: true });
  return dir;
}

async function ocultarChromeWireframe(page: Page) {
  await page.addStyleTag({
    content: `
      .demo-bar,
      .wire-label,
      .hidden-regulated,
      [data-od-id="nav-backoffice"],
      div:has(> [data-od-id="nav-backoffice"]) {
        display: none !important;
      }
    `,
  });
}

async function preencherCheckout(alvo: Page) {
  await alvo.getByLabel("E-mail").fill("joao.silva@example.com");
  await alvo.getByLabel("Senha").fill("Rural#2026Order");
  await alvo.getByLabel("Propriedade").selectOption({ label: "Fazenda Boa Vista — 420 ha" });
  await alvo.getByLabel("Preferência de retirada").selectOption({ label: "Retirar na loja (Centro)" });
}

async function esvaziarCheckout(alvo: Page) {
  await alvo.getByLabel("E-mail").fill("");
  await alvo.getByLabel("Senha").fill("");
  await alvo.getByLabel("Propriedade").selectOption({ value: "" });
  await alvo.getByLabel("Preferência de retirada").selectOption({ value: "" });
}

async function clicarChipDemo(page: Page, rotulo: string) {
  await page
    .locator('[data-od-id="demo-state-bar"] button', { hasText: rotulo })
    .evaluate((btn: HTMLButtonElement, nome: string) => {
      const chave = Object.keys(btn).find((item) => item.startsWith("__reactProps"));
      if (!chave) {
        throw new Error(`chip ${nome} sem __reactProps`);
      }
      const props = (btn as unknown as Record<string, { onClick?: () => void }>)[chave];
      if (!props?.onClick) {
        throw new Error(`chip ${nome} sem onClick`);
      }
      props.onClick();
    }, rotulo);
}

async function clicarConfirmacaoWireframe(page: Page) {
  await page.locator('[data-od-id="confirm-order-btn"]').evaluate((btn: HTMLButtonElement) => {
    const chave = Object.keys(btn).find((nome) => nome.startsWith("__reactProps"));
    if (!chave) {
      throw new Error("Confirm button sem __reactProps");
    }
    const props = (btn as unknown as Record<string, { onClick?: () => void }>)[chave];
    if (!props?.onClick) {
      throw new Error("Confirm button sem onClick");
    }
    props.onClick();
  });
}

test.describe("Evidência visual task_04", () => {
  test.skip(!process.env.VISUAL_TASK04, "só sob VISUAL_TASK04=1");
  test.setTimeout(120_000);

  test("VC-01 a VC-07", async ({ browser, page }) => {
    const apis = await mockarApis(page);
    apis.autenticarComo("alfa");
    const ref = await browser.newPage({ viewport: { width: 390, height: 844 }, locale: "pt-BR" });
    await ref.addInitScript(() => {
      localStorage.removeItem("mvp0-screen");
    });
    await ref.goto(wireUrl, { waitUntil: "domcontentloaded", timeout: 60_000 });
    await expect(ref.getByRole("heading", { name: "AgroRevenda Centro" })).toBeVisible({
      timeout: 30_000,
    });
    await ocultarChromeWireframe(ref);

    await page.goto("/catalogo");
    await expect(page.getByRole("heading", { name: "AgroRevenda Centro" })).toBeVisible();
    await ref.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("button", { name: "Adicionar" }).first().click();

    await ref.locator('[data-od-id="nav-checkout"]').click();
    await page.goto("/checkout");
    await expect(ref.getByRole("heading", { name: "Checkout" })).toBeVisible();
    await expect(page.getByRole("heading", { name: "Checkout" })).toBeVisible();
    await expect(page.getByLabel("Propriedade").locator("option", { hasText: "Fazenda Boa Vista" })).toHaveCount(1);

    await preencherCheckout(ref);
    await preencherCheckout(page);
    await expect(ref.getByRole("button", { name: "Confirmar pedido" })).toBeEnabled();

    const vc01 = pasta("VC-01");
    await ref.screenshot({ path: join(vc01, "reference.png") });
    await page.screenshot({ path: join(vc01, "implementation.png") });

    await esvaziarCheckout(ref);
    await esvaziarCheckout(page);
    await expect(ref.getByRole("button", { name: "Confirmar pedido" })).toBeDisabled();
    await clicarConfirmacaoWireframe(ref);
    await page.getByRole("button", { name: "Confirmar pedido" }).click();
    await expect(ref.getByText("Selecione uma propriedade.")).toBeVisible();
    await expect(page.getByText("Selecione uma propriedade.")).toBeVisible();
    const vc02 = pasta("VC-02");
    await ref.screenshot({ path: join(vc02, "reference.png") });
    await page.screenshot({ path: join(vc02, "implementation.png") });

    await preencherCheckout(ref);
    await preencherCheckout(page);
    await expect(ref.getByRole("button", { name: "Confirmar pedido" })).toBeEnabled();
    await expect(page.getByRole("button", { name: "Confirmar pedido" })).toBeEnabled();
    await clicarConfirmacaoWireframe(ref);
    await page.locator('[data-od-id="confirm-order-btn"]').click();
    await expect(ref.locator('[data-od-id="confirmation-success"]')).toBeVisible({ timeout: 10_000 });
    await expect(page.getByRole("heading", { name: "Pedido recebido" }).first()).toBeVisible({
      timeout: 10_000,
    });
    const vc03 = pasta("VC-03");
    await ref.screenshot({ path: join(vc03, "reference.png") });
    await page.screenshot({ path: join(vc03, "implementation.png") });

    await ref.getByRole("button", { name: /Meus pedidos/ }).click();
    await page.getByRole("button", { name: /Meus pedidos/ }).click();
    await expect(ref.getByRole("heading", { name: "Meus pedidos" })).toBeVisible();
    await expect(page.getByRole("heading", { name: "Meus pedidos" })).toBeVisible();
    const vc04 = pasta("VC-04");
    await ref.screenshot({ path: join(vc04, "reference.png") });
    await page.screenshot({ path: join(vc04, "implementation.png") });

    await ref.locator('[data-od-id="my-orders-body"] .order-list-item').first().click();
    await page.locator('[data-od-id="my-orders-body"] .order-list-item').first().click();
    await expect(ref.getByText("Total do pedido")).toBeVisible();
    await expect(page.getByText("Total do pedido")).toBeVisible();
    const vc05 = pasta("VC-05");
    await ref.screenshot({ path: join(vc05, "reference.png") });
    await page.screenshot({ path: join(vc05, "implementation.png") });

    await ref.locator('[data-od-id="order-detail-back"]').click();
    await ref.locator('[data-od-id="nav-catalog"]').click();
    await expect(ref.getByRole("heading", { name: "AgroRevenda Centro" })).toBeVisible();
    await clicarChipDemo(ref, "Acesso negado");
    await expect(ref.getByRole("heading", { name: "Acesso negado" })).toBeVisible();
    apis.negarPedido("ord-alheio");
    await page.goto("/pedidos/ord-alheio");
    await expect(page.getByRole("heading", { name: "Acesso negado" })).toBeVisible();
    await expect(page.getByText("Você não pode visualizar este pedido.")).toBeVisible();
    const vc06 = pasta("VC-06");
    await ref.screenshot({ path: join(vc06, "reference.png") });
    await page.screenshot({ path: join(vc06, "implementation.png") });

    await clicarChipDemo(ref, "Resultados");
    await ref.locator('[data-od-id="nav-checkout"]').click();
    await page.goto("/checkout");
    await ref.setViewportSize({ width: 1440, height: 900 });
    await page.setViewportSize({ width: 1440, height: 900 });
    await expect(ref.getByRole("heading", { name: "Checkout" })).toBeVisible();
    await expect(page.getByRole("heading", { name: "Checkout" })).toBeVisible();
    const vc07 = pasta("VC-07");
    await ref.screenshot({ path: join(vc07, "reference.png") });
    await page.screenshot({ path: join(vc07, "implementation.png") });
    await ref.close();
  });
});
