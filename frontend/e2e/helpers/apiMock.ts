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

  const propriedadesAlfa = [
    { id: "55555555-5555-5555-5555-555555555555", nome: "Fazenda Boa Vista — 420 ha" },
    { id: "66666666-6666-6666-6666-666666666666", nome: "Sítio São João — 85 ha" },
  ];
  const propriedadesBeta = [{ id: "99999999-9999-9999-9999-999999999999", nome: "Fazenda Sul" }];
  type PedidoMock = {
    idPedido: string;
    dono: string;
    nomeProdutor: string;
    situacao: string;
    confirmacao: string;
    nomePropriedade: string;
    preferenciaRetirada: string;
    total: string;
    criadoEm: string;
    itens: Array<{ nome: string; unidade: string; quantidade: number; precoUnitario: string; totalLinha: string }>;
  };
  const pedidos: PedidoMock[] = [];
  const negados = new Set<string>();
  let usuarioAtual = "alfa";

  let sessaoAtiva = false;

  await page.route("**/api/v1/autenticacao/cadastro", async (rota) => {
    sessaoAtiva = true;
    usuarioAtual = "novo";
    await rota.fulfill({
      json: { idUsuario: "usr-novo", nome: "Produtor Novo", papeis: ["PRODUTOR"] },
    });
  });

  await page.route("**/api/v1/autenticacao/entrada", async (rota) => {
    const corpo = rota.request().postDataJSON() as { email?: string; senha?: string };
    if (!corpo.email || !corpo.senha) {
      await rota.fulfill({
        status: 401,
        json: { codigo: "CREDENCIAIS_INVALIDAS", mensagem: "E-mail ou senha inválidos." },
      });
      return;
    }
    if (corpo.senha === "errada") {
      await rota.fulfill({
        status: 401,
        json: { codigo: "CREDENCIAIS_INVALIDAS", mensagem: "E-mail ou senha inválidos." },
      });
      return;
    }
    sessaoAtiva = true;
    if (corpo.email.includes("operador")) {
      usuarioAtual = "operador";
      await rota.fulfill({
        json: {
          idUsuario: "usr-operador",
          nome: "Operador Demonstracao",
          papeis: ["OPERADOR_REVENDA"],
        },
      });
      return;
    }
    usuarioAtual = corpo.email.includes("beta") ? "beta" : "alfa";
    await rota.fulfill({
      json: {
        idUsuario: usuarioAtual === "beta" ? "usr-beta" : "usr-alfa",
        nome: usuarioAtual === "beta" ? "Produtor Beta" : "Produtor Alfa",
        papeis: ["PRODUTOR"],
      },
    });
  });

  await page.route("**/api/v1/produtor/propriedades", async (rota) => {
    if (!sessaoAtiva) {
      await rota.fulfill({
        status: 401,
        json: { codigo: "NAO_AUTENTICADO", mensagem: "Entre ou crie uma conta para continuar." },
      });
      return;
    }
    const itens = usuarioAtual === "beta" ? propriedadesBeta : propriedadesAlfa;
    await rota.fulfill({ json: { itens } });
  });

  await page.route(/\/api\/v1\/pedidos(\/[^/?]+)?(\?|$)/, async (rota) => {
    const url = new URL(rota.request().url());
    const id = url.pathname.split("/").filter(Boolean)[3];
    if (rota.request().method() === "POST") {
      if (linhas.length === 0) {
        await rota.fulfill({
          status: 400,
          json: { codigo: "CARRINHO_VAZIO", mensagem: "Adicione ao menos um produto antes do checkout." },
        });
        return;
      }
      const corpo = rota.request().postDataJSON() as { idPropriedade?: string; preferenciaRetirada?: string };
      if (!corpo.idPropriedade || !corpo.preferenciaRetirada) {
        await rota.fulfill({
          status: 400,
          json: {
            codigo: "DADOS_CHECKOUT_OBRIGATORIOS",
            mensagem: "Escolha uma propriedade e a preferência de retirada.",
          },
        });
        return;
      }
      const propriedade =
        [...propriedadesAlfa, ...propriedadesBeta].find((p) => p.id === corpo.idPropriedade)?.nome ?? "Propriedade";
      const total = visao(linhas).total;
      const idPedido = `ord-${pedidos.length + 1}`;
      const pedido: PedidoMock = {
        idPedido,
        dono: usuarioAtual,
        nomeProdutor: usuarioAtual === "beta" ? "Produtor Beta" : "Produtor Alfa",
        situacao: "RECEBIDO",
        confirmacao: "ACEITA",
        nomePropriedade: propriedade,
        preferenciaRetirada: corpo.preferenciaRetirada,
        total,
        criadoEm: "2026-08-30T13:55:00Z",
        itens: linhas.map((linha) => ({
          nome: linha.nome,
          unidade: "Saco",
          quantidade: linha.quantidade,
          precoUnitario: linha.precoUnitario,
          totalLinha: linha.totalLinha,
        })),
      };
      pedidos.push(pedido);
      linhas.length = 0;
      await rota.fulfill({
        json: { idPedido, situacao: "RECEBIDO", confirmacao: "ACEITA", mensagem: "Pedido recebido" },
      });
      return;
    }
    if (rota.request().method() !== "GET") {
      await rota.fallback();
      return;
    }
    if (!id) {
      const doUsuario = pedidos.filter((p) => p.dono === usuarioAtual);
      await rota.fulfill({
        json: { itens: doUsuario, pagina: 1, tamanhoPagina: 10, total: doUsuario.length },
      });
      return;
    }
    if (negados.has(id) || (usuarioAtual === "beta" && pedidos.some((p) => p.idPedido === id && p.dono !== "beta"))) {
      await rota.fulfill({
        status: 403,
        json: { codigo: "ACESSO_PEDIDO_NEGADO", mensagem: "Você não pode visualizar este pedido." },
      });
      return;
    }
    const pedido = pedidos.find((p) => p.idPedido === id);
    if (!pedido) {
      await rota.fulfill({
        status: 403,
        json: { codigo: "ACESSO_PEDIDO_NEGADO", mensagem: "Você não pode visualizar este pedido." },
      });
      return;
    }
    await rota.fulfill({ json: pedido });
  });

  await page.route(/\/api\/v1\/retaguarda\/pedidos(\/[^/?]+)?(\?|$)/, async (rota) => {
    if (rota.request().method() !== "GET") {
      await rota.fallback();
      return;
    }
    if (!sessaoAtiva) {
      await rota.fulfill({
        status: 401,
        json: { codigo: "NAO_AUTENTICADO", mensagem: "Entre ou crie uma conta para continuar." },
      });
      return;
    }
    if (usuarioAtual !== "operador") {
      await rota.fulfill({
        status: 403,
        json: {
          codigo: "ACESSO_NEGADO",
          mensagem: "Somente o operador da revenda pode inspecionar os pedidos da retaguarda.",
        },
      });
      return;
    }
    const url = new URL(rota.request().url());
    const id = url.pathname.split("/").filter(Boolean)[4];
    if (!id) {
      await rota.fulfill({
        json: { itens: pedidos, pagina: 1, tamanhoPagina: 25, total: pedidos.length },
      });
      return;
    }
    const pedido = pedidos.find((item) => item.idPedido === id);
    if (!pedido) {
      await rota.fulfill({
        status: 404,
        json: { codigo: "PEDIDO_NAO_ENCONTRADO", mensagem: "Pedido não encontrado." },
      });
      return;
    }
    await rota.fulfill({ json: pedido });
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
    povoarPedidoDemo() {
      pedidos.push({
        idPedido: "ord-demo",
        dono: "alfa",
        nomeProdutor: "Produtor Alfa",
        situacao: "RECEBIDO",
        confirmacao: "ACEITA",
        nomePropriedade: "Fazenda Boa Vista — 420 ha",
        preferenciaRetirada: "DEPOSITO_PRINCIPAL",
        total: "1240.00",
        criadoEm: "2026-08-28T17:32:00Z",
        itens: [
          {
            nome: AURORA.nome,
            unidade: "Saco",
            quantidade: 2,
            precoUnitario: "620.00",
            totalLinha: "1240.00",
          },
        ],
      });
    },
    negarPedido(id: string) {
      negados.add(id);
    },
    autenticarComo(papel: "alfa" | "beta" | "operador") {
      sessaoAtiva = true;
      usuarioAtual = papel;
    },
    ultimoPedido() {
      return pedidos.at(-1);
    },
  };
}

function visao(linhas: Linha[]) {
  const total = linhas.reduce((soma, linha) => soma + Number(linha.totalLinha), 0);
  return { itens: linhas, total: dinheiro(total) };
}

export const test = base;
