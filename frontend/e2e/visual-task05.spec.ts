import { mkdirSync } from "node:fs";
import { join } from "node:path";
import { expect, type Page, test } from "@playwright/test";
import { mockarApis } from "./helpers/apiMock";

const evidencias = join(
  process.cwd(),
  "../.compozy/tasks/pedidos-insumos-mvp0/evidence/visual/task_05",
);
const wireUrl =
  "file://" +
  join(
    process.cwd(),
    "../.compozy/tasks/pedidos-insumos-mvp0/references/mvp0-pedidos-insumos.html",
  );

const PEDIDO_WIRE = {
  id: "PED-2026-0042",
  producer: "Maria Souza",
  createdAt: "2026-08-28T14:32:00-03:00",
  status: "Recebido",
  confirmation: "Aceita",
  propertyId: "prop-1",
  pickupId: "loja",
  items: [
    { productId: "p-semente-milho", name: "Semente de milho DK697", unit: "sc 60 kg", price: 890, qty: 2 },
    { productId: "p-npk", name: "Fertilizante NPK 20-05-20", unit: "sc 50 kg", price: 215, qty: 4 },
  ],
};

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

async function definirPedidosWireframe(page: Page, pedidos: unknown[]) {
  await page.evaluate((lista) => {
    const raiz = document.getElementById("root");
    if (!raiz) {
      throw new Error("root ausente");
    }
    const chave = Object.keys(raiz).find(
      (item) => item.startsWith("__reactFiber") || item.startsWith("__reactContainer"),
    );
    if (!chave) {
      throw new Error("fiber ausente");
    }
    type Fiber = {
      type?: { name?: string };
      elementType?: { name?: string };
      memoizedState?: Hook | null;
      child?: Fiber | null;
      sibling?: Fiber | null;
    };
    type Hook = {
      memoizedState?: unknown;
      queue?: { dispatch?: (valor: unknown) => void };
      next?: Hook | null;
    };
    const visitados = new Set<object>();
    function encontrarApp(no: Fiber | null | undefined): Fiber | null {
      if (!no || visitados.has(no)) {
        return null;
      }
      visitados.add(no);
      const nome = no.type?.name || no.elementType?.name;
      if (nome === "App") {
        return no;
      }
      return encontrarApp(no.child) || encontrarApp(no.sibling);
    }
    const app = encontrarApp((raiz as unknown as Record<string, Fiber>)[chave]);
    if (!app) {
      throw new Error("App não encontrado");
    }
    let hook = app.memoizedState ?? undefined;
    for (let i = 0; i < 12 && hook; i++) {
      hook = hook.next ?? undefined;
    }
    if (!hook?.queue?.dispatch) {
      throw new Error("setOrders não encontrado");
    }
    hook.queue.dispatch(lista);
  }, pedidos);
}

async function abrirBackofficeWireframe(page: Page) {
  await page.locator('[data-od-id="nav-backoffice"]').evaluate((btn: HTMLButtonElement) => {
    const chave = Object.keys(btn).find((item) => item.startsWith("__reactProps"));
    if (!chave) {
      throw new Error("nav-backoffice sem __reactProps");
    }
    const props = (btn as unknown as Record<string, { onClick?: () => void }>)[chave];
    if (!props?.onClick) {
      throw new Error("nav-backoffice sem onClick");
    }
    props.onClick();
  });
}

test.describe("Evidência visual task_05", () => {
  test.skip(!process.env.VISUAL_TASK05, "só sob VISUAL_TASK05=1");
  test.setTimeout(120_000);

  test("VC-01 a VC-05", async ({ browser, page }) => {
    const apis = await mockarApis(page);
    apis.autenticarComo("operador");
    apis.povoarPedidoDemo();
    const ref = await browser.newPage({ viewport: { width: 390, height: 844 }, locale: "pt-BR" });
    await ref.addInitScript(() => {
      localStorage.removeItem("mvp0-screen");
    });
    await ref.goto(wireUrl, { waitUntil: "networkidle", timeout: 90_000 });
    await expect(ref.getByRole("heading", { name: "AgroRevenda Centro" })).toBeVisible({
      timeout: 60_000,
    });
    await ref.locator('[data-od-id="nav-backoffice"]').click();
    await expect(ref.getByRole("heading", { name: "Pedidos do backoffice" })).toBeVisible();
    await ocultarChromeWireframe(ref);

    await page.goto("/retaguarda/pedidos");
    await expect(page.getByRole("heading", { name: "Pedidos do backoffice" })).toBeVisible();
    await expect(page.getByText("Produtor Alfa")).toBeVisible();

    const vc01 = pasta("VC-01");
    await ref.screenshot({ path: join(vc01, "reference.png") });
    await page.screenshot({ path: join(vc01, "implementation.png") });

    await ref.locator('[data-od-id="backoffice-body"] .order-list-item').first().click();
    await page.locator('[data-od-id="backoffice-body"] .order-list-item').first().click();
    await expect(ref.getByText("Snapshot imutável do pedido — operador não pode editar.")).toBeVisible();
    await expect(page.getByText("Snapshot imutável do pedido — operador não pode editar.")).toBeVisible();
    const vc03 = pasta("VC-03");
    await ref.screenshot({ path: join(vc03, "reference.png") });
    await page.screenshot({ path: join(vc03, "implementation.png") });

    await ref.locator('[data-od-id="order-detail-back"]').click();
    await expect(ref.getByRole("heading", { name: "Pedidos do backoffice" })).toBeVisible();
    await definirPedidosWireframe(ref, []);
    const vazio = await mockarApis(page);
    vazio.autenticarComo("operador");
    await page.goto("/retaguarda/pedidos");
    await expect(page.getByRole("heading", { name: "Nenhum pedido" })).toBeVisible();
    const vc02 = pasta("VC-02");
    await ref.screenshot({ path: join(vc02, "reference.png") });
    await page.screenshot({ path: join(vc02, "implementation.png") });

    await definirPedidosWireframe(ref, [PEDIDO_WIRE]);
    await ref.locator('[data-od-id="nav-catalog"]').click();
    await expect(ref.getByRole("heading", { name: "AgroRevenda Centro" })).toBeVisible();
    await clicarChipDemo(ref, "Acesso negado");
    await expect(ref.getByRole("heading", { name: "Acesso negado" })).toBeVisible();
    const produtor = await mockarApis(page);
    produtor.autenticarComo("alfa");
    await page.goto("/retaguarda/pedidos");
    await expect(page.getByRole("heading", { name: "Acesso negado" })).toBeVisible();
    const vc04 = pasta("VC-04");
    await ref.screenshot({ path: join(vc04, "reference.png") });
    await page.screenshot({ path: join(vc04, "implementation.png") });

    await clicarChipDemo(ref, "Resultados");
    await abrirBackofficeWireframe(ref);
    await expect(ref.getByRole("heading", { name: "Pedidos do backoffice" })).toBeVisible();
    const desktop = await mockarApis(page);
    desktop.autenticarComo("operador");
    desktop.povoarPedidoDemo();
    await page.goto("/retaguarda/pedidos");
    await ref.setViewportSize({ width: 1440, height: 900 });
    await page.setViewportSize({ width: 1440, height: 900 });
    await expect(ref.getByRole("heading", { name: "Pedidos do backoffice" })).toBeVisible();
    await expect(page.getByRole("heading", { name: "Pedidos do backoffice" })).toBeVisible();
    const vc05 = pasta("VC-05");
    await ref.screenshot({ path: join(vc05, "reference.png") });
    await page.screenshot({ path: join(vc05, "implementation.png") });
    await ref.close();
  });
});
