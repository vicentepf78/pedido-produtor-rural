import { test as base, type Page } from "@playwright/test";

export const AURORA = {
  id: "10000000-0000-4000-8000-000000000001",
  nome: "Semente de milho Aurora 20 kg",
  categoria: "Sementes",
  descricaoCurta: "Cultivar para plantio de verão.",
  unidade: "Saco",
  precoUnitario: "620.00",
  disponivel: true,
  urlImagem: "/media/produtos/aurora-milho-20kg.svg",
};

export const UREIA = {
  id: "10000000-0000-4000-8000-000000000013",
  nome: "Ureia 45% N",
  categoria: "Fertilizantes",
  descricaoCurta: "Adubo nitrogenado granulado.",
  unidade: "sc 50 kg",
  precoUnitario: "178.00",
  disponivel: false,
  urlImagem: null as string | null,
};

export const DK697 = {
  id: "10000000-0000-4000-8000-000000000002",
  nome: "Semente de milho DK697",
  categoria: "Sementes",
  descricaoCurta: "Híbrido adaptado ao cerrado, alto vigor.",
  unidade: "sc 60 kg",
  precoUnitario: "890.00",
  disponivel: true,
  urlImagem: null as string | null,
};

export const REGULADO_ID = "10000000-0000-4000-8000-000000000099";

const CATALOGO = [AURORA, UREIA, DK697];

type Linha = {
  idProduto: string;
  nome: string;
  quantidade: number;
  precoUnitario: string;
  totalLinha: string;
};

function dinheiro(valor: number): string {
  return valor.toFixed(2);
}

export async function mockarApis(page: Page) {
  const linhas: Linha[] = [];

  await page.route("**/api/v1/autenticacao/csrf", async (rota) => {
    await rota.fulfill({ json: { token: "csrf-teste" } });
  });

  await page.route("**/api/v1/catalogo/produtos/**", async (rota) => {
    const url = new URL(rota.request().url());
    const id = url.pathname.split("/").pop() ?? "";
    if (id === REGULADO_ID) {
      await rota.fulfill({
        status: 404,
        json: { codigo: "PRODUTO_NAO_ELEGIVEL", mensagem: "Este produto não pode ser pedido no MVP0." },
      });
      return;
    }
    const produto = CATALOGO.find((item) => item.id === id);
    if (!produto) {
      await rota.fulfill({
        status: 404,
        json: { codigo: "PRODUTO_NAO_ELEGIVEL", mensagem: "Este produto não pode ser pedido no MVP0." },
      });
      return;
    }
    await rota.fulfill({ json: produto });
  });

  await page.route("**/api/v1/catalogo/produtos*", async (rota) => {
    const url = new URL(rota.request().url());
    if (url.pathname !== "/api/v1/catalogo/produtos") {
      await rota.fallback();
      return;
    }
    const consulta = url.searchParams.get("consulta");
    let itens = CATALOGO;
    if (consulta !== null && consulta.trim() === "") {
      itens = [];
    } else if (consulta) {
      const termo = consulta.toLowerCase();
      itens = CATALOGO.filter((item) => item.nome.toLowerCase().includes(termo));
    }
    await rota.fulfill({ json: { itens, pagina: 1, tamanhoPagina: 30, total: itens.length } });
  });

  await page.route("**/api/v1/carrinhos/convidado/itens/**", async (rota) => {
    const metodo = rota.request().method();
    const idProduto = rota.request().url().split("/itens/")[1]?.split("?")[0];
    if (metodo === "DELETE" && idProduto) {
      const idx = linhas.findIndex((l) => l.idProduto === idProduto);
      if (idx >= 0) {
        linhas.splice(idx, 1);
      }
      await rota.fulfill({ json: visao(linhas) });
      return;
    }
    if (metodo === "PATCH" && idProduto) {
      const corpo = rota.request().postDataJSON() as { quantidade: number };
      const qtd = Number(corpo.quantidade);
      if (!Number.isInteger(qtd) || qtd < 1) {
        await rota.fulfill({
          status: 400,
          json: { codigo: "QUANTIDADE_INVALIDA", mensagem: "Informe uma quantidade inteira positiva." },
        });
        return;
      }
      const linha = linhas.find((l) => l.idProduto === idProduto);
      if (linha) {
        linha.quantidade = qtd;
        linha.totalLinha = dinheiro(Number(linha.precoUnitario) * qtd);
      }
      await rota.fulfill({ json: visao(linhas) });
      return;
    }
    await rota.fallback();
  });

  await page.route("**/api/v1/carrinhos/convidado/itens", async (rota) => {
    if (rota.request().method() !== "POST") {
      await rota.fallback();
      return;
    }
    const corpo = rota.request().postDataJSON() as { idProduto: string; quantidade: number };
    const produto = CATALOGO.find((item) => item.id === corpo.idProduto);
    if (!produto || !produto.disponivel) {
      await rota.fulfill({
        status: 409,
        json: { codigo: "PRODUTO_INDISPONIVEL", mensagem: "Este produto não está disponível." },
      });
      return;
    }
    const existente = linhas.find((l) => l.idProduto === produto.id);
    if (existente) {
      existente.quantidade += corpo.quantidade;
      existente.totalLinha = dinheiro(Number(existente.precoUnitario) * existente.quantidade);
    } else {
      linhas.push({
        idProduto: produto.id,
        nome: produto.nome,
        quantidade: corpo.quantidade,
        precoUnitario: produto.precoUnitario,
        totalLinha: dinheiro(Number(produto.precoUnitario) * corpo.quantidade),
      });
    }
    await rota.fulfill({ json: visao(linhas) });
  });

  await page.route("**/api/v1/carrinhos/convidado", async (rota) => {
    if (rota.request().method() !== "GET") {
      await rota.fallback();
      return;
    }
    await rota.fulfill({ json: visao(linhas) });
  });

  return {
    povoarCemLinhas() {
      linhas.length = 0;
      for (let i = 0; i < 100; i++) {
        const preco = 1.25;
        linhas.push({
          idProduto: `prod-${i}`,
          nome: `Produto fixture ${i + 1}`,
          quantidade: 1,
          precoUnitario: dinheiro(preco),
          totalLinha: dinheiro(preco),
        });
      }
    },
  };
}

function visao(linhas: Linha[]) {
  const total = linhas.reduce((soma, linha) => soma + Number(linha.totalLinha), 0);
  return { itens: linhas, total: dinheiro(total) };
}

export const test = base;
