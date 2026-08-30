import { mkdirSync } from "node:fs";
import { join } from "node:path";
import { expect, test } from "@playwright/test";
import { mockarApis } from "./helpers/apiMock";

const evidencias = join(
  process.cwd(),
  "../.compozy/tasks/pedidos-insumos-mvp0/evidence/visual/task_03",
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

async function normalizarReferencia(page: import("@playwright/test").Page) {
  await page.addStyleTag({
    content: ".demo-bar, .wire-label, .hidden-regulated { display: none !important; }",
  });
  await page.evaluate(() => {
    document.querySelectorAll('[role="tab"]').forEach((el) => {
      if (el.textContent?.trim() === "Defensivos") {
        el.remove();
      }
    });
    document.querySelector('[data-od-id="nav-backoffice"]')?.closest("div")?.remove();
  });
}

test.describe("Evidência visual task_03", () => {
  test.skip(!process.env.VISUAL_TASK03, "só sob VISUAL_TASK03=1");
  test.setTimeout(90_000);

  test("VC-01 a VC-07", async ({ browser, page }) => {
    await mockarApis(page);
    const ref = await browser.newPage({ viewport: { width: 390, height: 844 }, locale: "pt-BR" });
    await ref.addInitScript(() => {
      localStorage.removeItem("mvp0-screen");
    });
    await ref.goto(wireUrl, { waitUntil: "domcontentloaded", timeout: 60_000 });
    await expect(ref.getByRole("heading", { name: "AgroRevenda Centro" })).toBeVisible({
      timeout: 30_000,
    });
    await normalizarReferencia(ref);

    await page.goto("/catalogo");
    await expect(page.getByRole("heading", { name: "AgroRevenda Centro" })).toBeVisible();

    const vc01 = pasta("VC-01");
    await ref.screenshot({ path: join(vc01, "reference.png") });
    await page.screenshot({ path: join(vc01, "implementation.png") });

    await ref.getByLabel("Buscar por nome").fill("xyzzy-sem-produto");
    await page.getByLabel("Buscar por nome").fill("xyzzy-sem-produto");
    await expect(ref.getByRole("heading", { name: "Nenhum resultado" })).toBeVisible();
    await expect(page.getByRole("heading", { name: "Nenhum resultado" })).toBeVisible();
    const vc02 = pasta("VC-02");
    await ref.screenshot({ path: join(vc02, "reference.png") });
    await page.screenshot({ path: join(vc02, "implementation.png") });

    await ref.getByRole("button", { name: "Limpar busca" }).click();
    await page.getByRole("button", { name: "Limpar busca" }).click();
    await expect(ref.getByRole("heading", { name: "Ureia 45% N" })).toBeVisible();
    await expect(page.getByRole("heading", { name: "Ureia 45% N" })).toBeVisible();
    const vc03 = pasta("VC-03");
    await ref.screenshot({ path: join(vc03, "reference.png") });
    await page.screenshot({ path: join(vc03, "implementation.png") });
    const vc04 = pasta("VC-04");
    await ref.screenshot({ path: join(vc04, "reference.png") });
    await page.screenshot({ path: join(vc04, "implementation.png") });

    await ref.locator('[data-od-id="nav-cart"]').click();
    await page.goto("/carrinho");
    await expect(ref.getByRole("heading", { name: "Carrinho vazio" })).toBeVisible();
    await expect(page.getByRole("heading", { name: "Carrinho vazio" })).toBeVisible();
    const vc05 = pasta("VC-05");
    await ref.screenshot({ path: join(vc05, "reference.png") });
    await page.screenshot({ path: join(vc05, "implementation.png") });

    await ref.locator('[data-od-id="nav-catalog"]').click();
    await ref.getByRole("button", { name: "Adicionar" }).first().click();
    await ref.locator('[data-od-id="nav-cart"]').click();
    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Adicionar" }).first().click();
    await page.getByRole("link", { name: /Carrinho/ }).click();
    await expect(ref.getByText("Total do pedido")).toBeVisible();
    await expect(page.getByText("Total do pedido")).toBeVisible();
    const vc06 = pasta("VC-06");
    await ref.screenshot({ path: join(vc06, "reference.png") });
    await page.screenshot({ path: join(vc06, "implementation.png") });

    await ref.setViewportSize({ width: 1440, height: 900 });
    await page.setViewportSize({ width: 1440, height: 900 });
    await ref.locator('[data-od-id="nav-catalog"]').click();
    await page.goto("/catalogo");
    await expect(ref.getByRole("heading", { name: "AgroRevenda Centro" })).toBeVisible();
    await expect(page.getByRole("heading", { name: "AgroRevenda Centro" })).toBeVisible();
    const vc07 = pasta("VC-07");
    await ref.screenshot({ path: join(vc07, "reference.png") });
    await page.screenshot({ path: join(vc07, "implementation.png") });
    await ref.close();
  });
});
