import { expect } from "@playwright/test";
import {
  AURORA,
  NOMES_FERTILIZANTES,
  NOMES_SEMENTES,
  REGULADO_ID,
  UREIA,
  mockarApis,
  test,
} from "./helpers/apiMock";

function cardCategoria(page: import("@playwright/test").Page, slug: string) {
  return page.locator(`[data-od-id="category-${slug}"]`);
}

test.describe("Catálogo S1", () => {
  test.use({ viewport: { width: 390, height: 844 } });

  test("E2E-001 carrossel fechado, bloco de 10 e sem bottom-nav", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await expect(cardCategoria(page, "todos")).toBeVisible();
    await expect(cardCategoria(page, "sementes")).toBeVisible();
    await expect(cardCategoria(page, "fertilizantes")).toBeVisible();
    await expect(cardCategoria(page, "correcao")).toBeVisible();
    await expect(cardCategoria(page, "todos")).toHaveText("Todos");
    await expect(cardCategoria(page, "sementes")).toHaveText("Sementes");
    await expect(cardCategoria(page, "fertilizantes")).toHaveText("Fertilizantes");
    await expect(cardCategoria(page, "correcao")).toHaveText("Correção");
    await expect(page.locator(".category-card img")).toHaveCount(4);
    await expect(page.locator(".product-card")).toHaveCount(10);
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
    await expect(page.locator(".bottom-nav")).toHaveCount(0);
    await expect(page.getByRole("navigation", { name: "Rodapé" })).toHaveCount(0);
  });

  test("E2E-002 Fertilizantes marca o card e recorta a lista", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await cardCategoria(page, "fertilizantes").click();
    await expect(page.locator('[data-od-id="category-fertilizantes"]')).toHaveClass(/selected/);
    await expect(page.locator('[data-od-id="category-fertilizantes"]')).toHaveAttribute("aria-pressed", "true");
    await expect(page.getByRole("heading", { name: UREIA.nome })).toBeVisible();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toHaveCount(0);
    for (const nome of NOMES_SEMENTES) {
      await expect(page.getByRole("heading", { name: nome })).toHaveCount(0);
    }
  });

  test("E2E-003 sem Defensivos e sem produto regulamentado", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await expect(page.getByRole("listitem", { name: "Defensivos" })).toHaveCount(0);
    await expect(page.getByText("Herbicida glifosato")).toHaveCount(0);
    await page.getByRole("button", { name: "Carregar mais" }).click();
    await page.getByRole("button", { name: "Carregar mais" }).click();
    await expect(page.getByText("Herbicida glifosato")).toHaveCount(0);
    await expect(page.getByRole("listitem", { name: "Defensivos" })).toHaveCount(0);
  });

  test("E2E-004 card de categoria sem foto permanece utilizável", async ({ page }) => {
    await mockarApis(page);
    await page.route("**/media/categorias/correcao.svg", (rota) => rota.fulfill({ status: 404, body: "" }));
    await page.goto("/catalogo");
    const card = page.locator('[data-od-id="category-correcao"]');
    await expect(card).toBeVisible();
    await expect(card.getByText("Correção")).toBeVisible();
    await expect(card.locator("img")).toHaveCount(0);
    await card.click();
    await expect(card).toHaveClass(/selected/);
    await expect(page.getByRole("heading", { name: "Calcário dolomítico 50 kg" })).toBeVisible();
  });

  test("E2E-005 categoria vazia oferece Limpar filtros", async ({ page }) => {
    await mockarApis(page, { categoriaVazia: "Correção" });
    await page.goto("/catalogo");
    await cardCategoria(page, "correcao").click();
    await expect(page.getByRole("heading", { name: "Nenhum resultado" })).toBeVisible();
    await expect(page.getByRole("button", { name: "Carregar mais" })).toHaveCount(0);
    await page.getByRole("button", { name: "Limpar filtros" }).click();
    await expect(page.locator('[data-od-id="category-todos"]')).toHaveClass(/selected/);
    await expect(page.locator(".product-card")).toHaveCount(10);
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
  });

  test("E2E-006 toques rápidos: vale a última categoria", async ({ page }) => {
    const apis = await mockarApis(page);
    apis.atrasarCategoria("Sementes", 800);
    await page.goto("/catalogo");
    await expect(page.locator(".product-card")).toHaveCount(10);
    await cardCategoria(page, "sementes").click();
    await cardCategoria(page, "fertilizantes").click();
    await expect(page.locator('[data-od-id="category-fertilizantes"]')).toHaveClass(/selected/);
    await expect(page.getByRole("heading", { name: UREIA.nome })).toBeVisible();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toHaveCount(0);
    for (const nome of NOMES_SEMENTES) {
      await expect(page.getByRole("heading", { name: nome })).toHaveCount(0);
    }
  });

  test("E2E-007 carrossel estreito tem controle além do gesto", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    const trilho = page.locator(".category-carousel");
    await expect(page.locator('[data-od-id="carousel-prev"]')).toBeVisible();
    await expect(page.locator('[data-od-id="carousel-next"]')).toBeVisible();
    const antes = await trilho.evaluate((el) => el.scrollLeft);
    await page.locator('[data-od-id="carousel-next"]').click();
    await expect.poll(async () => trilho.evaluate((el) => el.scrollLeft)).toBeGreaterThan(antes);
  });

  test("E2E-008 convidado e sessão expirada continuam utilizáveis", async ({ page }) => {
    const apis = await mockarApis(page);
    await page.goto("/catalogo");
    await expect(cardCategoria(page, "todos")).toBeVisible();
    await expect(page.locator(".product-card")).toHaveCount(10);
    apis.autenticarComo("alfa");
    apis.expirarSessao();
    await page.reload();
    await expect(cardCategoria(page, "fertilizantes")).toBeVisible();
    await cardCategoria(page, "fertilizantes").click();
    await expect(page.getByRole("heading", { name: UREIA.nome })).toBeVisible();
  });

  test("E2E-009 deep link regulamentado não revela o item", async ({ page }) => {
    await mockarApis(page);
    await page.goto(`/catalogo/${REGULADO_ID}`);
    await expect(page.getByRole("heading", { name: "Acesso negado" })).toBeVisible();
    await expect(page.getByText("Este produto não pode ser pedido nesta loja.")).toBeVisible();
    await expect(page.getByText("Herbicida")).toHaveCount(0);
    await expect(page.getByText("Defensivos")).toHaveCount(0);
    await expect(page.getByText("42")).toHaveCount(0);
    await expect(page.getByRole("button", { name: "Adicionar" })).toHaveCount(0);
  });

  test("E2E-010 falha ao trocar categoria mantém a lista e permite retry", async ({ page }) => {
    const apis = await mockarApis(page);
    await page.goto("/catalogo");
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
    apis.falharProximaListagem();
    await cardCategoria(page, "fertilizantes").click();
    await expect(page.getByRole("heading", { name: "Não foi possível carregar" })).toBeVisible();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
    await page.getByRole("button", { name: "Tentar novamente" }).click();
    await expect(page.getByRole("heading", { name: UREIA.nome })).toBeVisible();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toHaveCount(0);
  });

  test("E2E-011 conjunto de 30 não inventa categoria", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await expect(page.locator(".category-card")).toHaveCount(4);
    await page.getByLabel("Itens por página").selectOption("50");
    await expect(page.locator(".product-card")).toHaveCount(30);
    await expect(page.getByRole("listitem", { name: "Defensivos" })).toHaveCount(0);
    await expect(page.locator(".category-card")).toHaveCount(4);
  });

  test("E2E-012 busca composta em Fertilizantes + ureia", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await cardCategoria(page, "fertilizantes").click();
    await page.getByLabel("Buscar por nome").fill("ureia");
    await expect(page).toHaveURL(/consulta=ureia/);
    await expect(page.locator(".product-card")).toHaveCount(1);
    await expect(page.getByRole("heading", { name: UREIA.nome })).toBeVisible();
    await expect(page.getByRole("heading", { name: "Fertilizante NPK 20-05-20" })).toHaveCount(0);
    await expect(page.getByRole("heading", { name: AURORA.nome })).toHaveCount(0);
  });

  test("E2E-013 Limpar filtros restaura Todos e a busca vazia", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await cardCategoria(page, "fertilizantes").click();
    await page.getByLabel("Buscar por nome").fill("ureia");
    await expect(page.locator(".product-card")).toHaveCount(1);
    await page.getByRole("button", { name: "Limpar filtros" }).click();
    await expect(page.locator('[data-od-id="category-todos"]')).toHaveClass(/selected/);
    await expect(page.getByLabel("Buscar por nome")).toHaveValue("");
    await expect(page.locator(".product-card")).toHaveCount(10);
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
  });

  test("E2E-014 texto hostil, espaços e texto longo não quebram a loja", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByLabel("Buscar por nome").fill("<img src=x onerror=alert(1)>");
    await expect(page.getByRole("heading", { name: "Nenhum resultado" })).toBeVisible();
    await page.getByRole("button", { name: "Limpar filtros" }).click();
    await page.getByLabel("Buscar por nome").fill("     ");
    await expect(page.locator(".product-card")).toHaveCount(10);
    await page.getByLabel("Buscar por nome").fill("x".repeat(400));
    await expect(page.getByRole("heading", { name: "Nenhum resultado" })).toBeVisible();
    await expect(page.getByRole("heading", { name: "AgroRevenda Centro" })).toBeVisible();
  });

  test("E2E-015 busca em branco respeita só Fertilizantes", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await cardCategoria(page, "fertilizantes").click();
    await page.getByLabel("Buscar por nome").fill("   ");
    await expect(page.getByRole("heading", { name: UREIA.nome })).toBeVisible();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toHaveCount(0);
    await expect(page.locator(".product-card").first()).toBeVisible();
  });

  test("E2E-016 busca sem correspondência mostra Limpar filtros", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByLabel("Buscar por nome").fill("xyzzy-sem-produto");
    await expect(page.getByRole("heading", { name: "Nenhum resultado" })).toBeVisible();
    await expect(page.getByRole("button", { name: "Carregar mais" })).toHaveCount(0);
    await page.getByRole("button", { name: "Limpar filtros" }).click();
    await expect(page.locator(".product-card")).toHaveCount(10);
  });

  test("E2E-017 três blocos 10+10+10 esgotam os 30", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await expect(page.locator(".product-card")).toHaveCount(10);
    await page.getByRole("button", { name: "Carregar mais" }).click();
    await expect(page.locator(".product-card")).toHaveCount(20);
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
    await page.getByRole("button", { name: "Carregar mais" }).click();
    await expect(page.locator(".product-card")).toHaveCount(30);
    await expect(page.getByRole("button", { name: "Carregar mais" })).toHaveCount(0);
  });

  test("E2E-018 trocar tamanho recomeça; busca permanece ao trocar categoria", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByRole("button", { name: "Carregar mais" }).click();
    await expect(page.locator(".product-card")).toHaveCount(20);
    await page.getByLabel("Itens por página").selectOption("15");
    await expect(page.locator(".product-card")).toHaveCount(15);
    await page.getByLabel("Itens por página").selectOption("30");
    await expect(page.locator(".product-card")).toHaveCount(30);
    await page.getByLabel("Itens por página").selectOption("50");
    await expect(page.locator(".product-card")).toHaveCount(30);
    await page.getByLabel("Buscar por nome").fill("ureia");
    await expect(page.getByRole("heading", { name: UREIA.nome })).toBeVisible();
    await cardCategoria(page, "sementes").click();
    await expect(page.getByLabel("Buscar por nome")).toHaveValue("ureia");
    await expect(page.getByRole("heading", { name: "Nenhum resultado" })).toBeVisible();
  });

  test("E2E-019 busca mantém categoria e Carregar mais duplo não duplica", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await cardCategoria(page, "fertilizantes").click();
    await page.getByLabel("Buscar por nome").fill("fertilizante");
    await expect(page.getByRole("heading", { name: "Fertilizante NPK 20-05-20" })).toBeVisible();
    await expect(page.locator('[data-od-id="category-fertilizantes"]')).toHaveClass(/selected/);
    await page.getByLabel("Buscar por nome").fill("");
    await expect(page.getByRole("heading", { name: UREIA.nome })).toBeVisible();
    const botao = page.getByRole("button", { name: "Carregar mais" });
    await Promise.all([botao.click(), botao.click()]);
    await expect(page.locator(".product-card")).toHaveCount(NOMES_FERTILIZANTES.length);
    const ids = await page.locator(".product-card").evaluateAll((els) => els.map((el) => el.getAttribute("data-od-id")));
    expect(new Set(ids).size).toBe(ids.length);
    expect(ids.length).toBe(NOMES_FERTILIZANTES.length);
  });

  test("E2E-020 dois textos rápidos valem o último estável", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByLabel("Buscar por nome").fill("semente");
    await page.getByLabel("Buscar por nome").fill("ureia");
    await expect(page.getByRole("heading", { name: UREIA.nome })).toBeVisible();
    await expect(page.getByRole("heading", { name: AURORA.nome })).toHaveCount(0);
    await expect(page.locator(".product-card")).toHaveCount(1);
  });

  test("E2E-021 voltar do detalhe preserva filtros", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await cardCategoria(page, "fertilizantes").click();
    await page.getByLabel("Buscar por nome").fill("ureia");
    await page.getByLabel("Itens por página").selectOption("15");
    await expect(page).toHaveURL(/consulta=ureia/);
    await expect(page.getByRole("heading", { name: UREIA.nome })).toBeVisible();
    await page.getByRole("link", { name: UREIA.nome }).click();
    await expect(page.getByRole("heading", { name: UREIA.nome })).toBeVisible();
    await page.getByRole("link", { name: "Voltar ao catálogo" }).click();
    await expect(page.locator('[data-od-id="category-fertilizantes"]')).toHaveClass(/selected/);
    await expect(page.getByLabel("Buscar por nome")).toHaveValue("ureia");
    await expect(page.getByLabel("Itens por página")).toHaveValue("15");
  });

  test("E2E-022 primeira visita: Todos, busca vazia, tamanho 10", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await expect(page.locator('[data-od-id="category-todos"]')).toHaveClass(/selected/);
    await expect(page.getByLabel("Buscar por nome")).toHaveValue("");
    await expect(page.getByLabel("Itens por página")).toHaveValue("10");
    await expect(page.locator(".product-card")).toHaveCount(10);
  });

  test("E2E-023 controle oferece 10, 15, 30 e 50", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    const controle = page.getByLabel("Itens por página");
    await expect(controle.locator("option")).toHaveText(["10", "15", "30", "50"]);
    await expect(controle).toHaveValue("10");
  });

  test("E2E-024 tamanho 50 lista os 30 sem Carregar mais", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByLabel("Itens por página").selectOption("50");
    await expect(page.locator(".product-card")).toHaveCount(30);
    await expect(page.getByRole("button", { name: "Carregar mais" })).toHaveCount(0);
  });

  test("E2E-025 último bloco parcial acrescenta só o restante", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await cardCategoria(page, "sementes").click();
    await expect(page.locator(".product-card")).toHaveCount(10);
    await page.getByRole("button", { name: "Carregar mais" }).click();
    await expect(page.locator(".product-card")).toHaveCount(12);
    await expect(page.getByRole("button", { name: "Carregar mais" })).toHaveCount(0);
  });

  test("E2E-026 interrupção ao Carregar mais preserva itens", async ({ page }) => {
    const apis = await mockarApis(page);
    await page.goto("/catalogo");
    await expect(page.locator(".product-card")).toHaveCount(10);
    apis.falharProximaListagem();
    await page.getByRole("button", { name: "Carregar mais" }).click();
    await expect(page.getByRole("heading", { name: "Não foi possível carregar" })).toBeVisible();
    await expect(page.locator(".product-card")).toHaveCount(10);
    await expect(page.getByRole("heading", { name: AURORA.nome })).toBeVisible();
    await page.getByRole("button", { name: "Tentar novamente" }).click();
    await expect(page.locator(".product-card")).toHaveCount(20);
  });

  test("E2E-027 tamanho 24 é recusado e permanece 10", async ({ page }) => {
    await mockarApis(page);
    const pedidos: string[] = [];
    page.on("request", (req) => {
      if (req.url().includes("/api/v1/catalogo/produtos?") && !req.url().includes("/produtos/")) {
        pedidos.push(req.url());
      }
    });
    await page.goto("/catalogo?tamanhoPagina=24");
    await expect(page.locator(".product-card")).toHaveCount(10);
    await expect(page.getByLabel("Itens por página")).toHaveValue("10");
    await expect(page.getByLabel("Itens por página").locator("option[value='24']")).toHaveCount(0);
    expect(pedidos.every((url) => !url.includes("tamanhoPagina=24"))).toBeTruthy();
  });

  test("E2E-028 recorte vazio não mostra Carregar mais", async ({ page }) => {
    await mockarApis(page);
    await page.goto("/catalogo");
    await page.getByLabel("Buscar por nome").fill("xyzzy-sem-produto");
    await expect(page.getByRole("heading", { name: "Nenhum resultado" })).toBeVisible();
    await expect(page.getByRole("button", { name: "Carregar mais" })).toHaveCount(0);
    await expect(page.getByRole("button", { name: "Limpar filtros" })).toBeVisible();
  });
});
