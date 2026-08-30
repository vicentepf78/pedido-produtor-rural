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

const MENSAGEM_INELEGIVEL = "Este produto não pode ser pedido nesta loja.";
const TAMANHOS_VALIDOS = new Set([10, 15, 30, 50]);
const CATEGORIAS_VALIDAS = new Set(["Sementes", "Fertilizantes", "Correção"]);

function item(
  id: string,
  nome: string,
  categoria: string,
  descricaoCurta: string,
  unidade: string,
  precoUnitario: string,
  disponivel: boolean,
  urlImagem: string | null = null,
) {
  return { id, nome, categoria, descricaoCurta, unidade, precoUnitario, disponivel, urlImagem };
}

const CATALOGO = [
  AURORA,
  DK697,
  item("10000000-0000-4000-8000-000000000003", "Semente de soja BRS 538", "Sementes", "Ciclo médio para o Cerrado.", "sc 40 kg", "540.00", true),
  item("10000000-0000-4000-8000-000000000004", "Semente de soja Intacta 25 kg", "Sementes", "Cultivar com proteção a lagartas.", "Saco", "710.00", true, "/media/produtos/soja.svg"),
  item("10000000-0000-4000-8000-000000000005", "Semente de trigo BRS 264", "Sementes", "Trigo de sequeiro para o Cerrado.", "sc 40 kg", "380.00", true),
  item("10000000-0000-4000-8000-000000000006", "Semente de feijão carioca 20 kg", "Sementes", "Variedade de mesa, ciclo curto.", "Saco", "420.00", true),
  item("10000000-0000-4000-8000-000000000007", "Semente de sorgo granífero 10 kg", "Sementes", "Opção de safrinha em áreas de milho.", "Saco", "195.00", true),
  item("10000000-0000-4000-8000-000000000008", "Semente de milho pipoca 5 kg", "Sementes", "Híbrido para consumo interno.", "Saco", "86.00", true),
  item("10000000-0000-4000-8000-000000000009", "Semente de braquiária 10 kg", "Sementes", "Formação de pastagem em solo médio.", "Saco", "240.00", true),
  item("10000000-0000-4000-8000-000000000010", "Semente de capim mombaça 5 kg", "Sementes", "Forrageira de alta produção de massa.", "Saco", "175.00", true),
  item("10000000-0000-4000-8000-000000000011", "Semente de crotalária 10 kg", "Sementes", "Adubação verde e cobertura de solo.", "Saco", "132.00", true),
  item("10000000-0000-4000-8000-000000000012", "Semente de aveia preta 20 kg", "Sementes", "Cobertura de inverno e pastejo.", "Saco", "210.00", true),
  UREIA,
  item("10000000-0000-4000-8000-000000000014", "Fertilizante NPK 20-05-20", "Fertilizantes", "Formulação balanceada para cobertura.", "sc 50 kg", "215.00", true, "/media/produtos/npk.svg"),
  item("10000000-0000-4000-8000-000000000015", "Fertilizante NPK 04-14-08", "Fertilizantes", "Fórmula de plantio para grãos.", "sc 50 kg", "198.00", true),
  item("10000000-0000-4000-8000-000000000016", "Fertilizante NPK 20-00-20", "Fertilizantes", "Cobertura nitrogenada e potássica.", "sc 50 kg", "205.00", true),
  item("10000000-0000-4000-8000-000000000017", "Sulfato de amônio 21%", "Fertilizantes", "Fonte de nitrogênio e enxofre.", "sc 50 kg", "156.00", true),
  item("10000000-0000-4000-8000-000000000018", "Cloreto de potássio 60%", "Fertilizantes", "Fonte de potássio para grãos.", "sc 50 kg", "268.00", true),
  item("10000000-0000-4000-8000-000000000019", "Superfosfato simples", "Fertilizantes", "Fósforo e enxofre para correção.", "sc 50 kg", "142.00", true),
  item("10000000-0000-4000-8000-000000000020", "Superfosfato triplo", "Fertilizantes", "Alta concentração de fósforo.", "sc 50 kg", "189.00", true),
  item("10000000-0000-4000-8000-000000000021", "MAP 11-52-00", "Fertilizantes", "Fosfato monoamônico para plantio.", "sc 50 kg", "312.00", true),
  item("10000000-0000-4000-8000-000000000022", "Nitrato de cálcio", "Fertilizantes", "Nitrogênio nítrico com cálcio.", "sc 25 kg", "224.00", true),
  item("10000000-0000-4000-8000-000000000023", "Fertilizante foliar 10 L", "Fertilizantes", "Complemento nutricional via folha.", "Galão", "98.00", true),
  item("10000000-0000-4000-8000-000000000024", "Organomineral 25 kg", "Fertilizantes", "Mistura orgânica para solo.", "Saco", "76.00", true),
  item("10000000-0000-4000-8000-000000000025", "Calcário dolomítico 50 kg", "Correção", "Correção de acidez com magnésio.", "sc 50 kg", "28.00", true),
  item("10000000-0000-4000-8000-000000000026", "Calcário calcítico 50 kg", "Correção", "Correção de acidez com cálcio.", "sc 50 kg", "26.00", true),
  item("10000000-0000-4000-8000-000000000027", "Gesso agrícola 40 kg", "Correção", "Fornece cálcio e enxofre ao perfil.", "sc 40 kg", "34.00", true),
  item("10000000-0000-4000-8000-000000000028", "Fosfato natural 50 kg", "Correção", "Fonte de fósforo de liberação lenta.", "sc 50 kg", "48.00", true),
  item("10000000-0000-4000-8000-000000000029", "Enxofre agrícola 25 kg", "Correção", "Elementar para correção de deficiência.", "Saco", "62.00", true),
  item("10000000-0000-4000-8000-000000000030", "Cálcio e magnésio 20 kg", "Correção", "Complemento de bases no solo.", "Saco", "55.00", true),
];

export type OpcoesMock = {
  categoriaVazia?: string;
};

export const NOMES_SEMENTES = CATALOGO.filter((p) => p.categoria === "Sementes").map((p) => p.nome);
export const NOMES_FERTILIZANTES = CATALOGO.filter((p) => p.categoria === "Fertilizantes").map((p) => p.nome);

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

export async function mockarApis(page: Page, opcoes: OpcoesMock = {}) {
  const linhas: Linha[] = [];
  let falharProximaListagem = false;
  const atrasosCategoria = new Map<string, number>();

  await page.route("**/api/v1/autenticacao/csrf", async (rota) => {
    await rota.fulfill({ json: { token: "csrf-teste" } });
  });

  await page.route("**/api/v1/catalogo/produtos/**", async (rota) => {
    const url = new URL(rota.request().url());
    const id = url.pathname.split("/").pop() ?? "";
    if (id === REGULADO_ID) {
      await rota.fulfill({
        status: 404,
        json: { codigo: "PRODUTO_NAO_ELEGIVEL", mensagem: MENSAGEM_INELEGIVEL },
      });
      return;
    }
    const produto = CATALOGO.find((item) => item.id === id);
    if (!produto) {
      await rota.fulfill({
        status: 404,
        json: { codigo: "PRODUTO_NAO_ELEGIVEL", mensagem: MENSAGEM_INELEGIVEL },
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
    if (falharProximaListagem) {
      falharProximaListagem = false;
      await rota.abort("failed");
      return;
    }
    const tamanhoBruto = url.searchParams.get("tamanhoPagina");
    const tamanhoPagina = tamanhoBruto === null ? 10 : Number(tamanhoBruto);
    if (!TAMANHOS_VALIDOS.has(tamanhoPagina)) {
      await rota.fulfill({
        status: 400,
        json: { codigo: "TAMANHO_PAGINA_INVALIDO", mensagem: "Escolha 10, 15, 30 ou 50 itens por página." },
      });
      return;
    }
    const pagina = Math.max(1, Number(url.searchParams.get("pagina") ?? "1") || 1);
    const categoria = url.searchParams.get("categoria");
    if (categoria && categoria !== "Todos" && !CATEGORIAS_VALIDAS.has(categoria)) {
      await rota.fulfill({
        status: 400,
        json: { codigo: "CATEGORIA_INVALIDA", mensagem: "Categoria não disponível nesta loja." },
      });
      return;
    }
    const atraso = categoria ? atrasosCategoria.get(categoria) : undefined;
    if (atraso) {
      await new Promise((resolver) => setTimeout(resolver, atraso));
    }
    let itens = CATALOGO.slice();
    if (opcoes.categoriaVazia && categoria === opcoes.categoriaVazia) {
      itens = [];
    } else if (categoria && categoria !== "Todos") {
      itens = itens.filter((p) => p.categoria === categoria);
    }
    const consulta = url.searchParams.get("consulta");
    if (consulta && consulta.trim() !== "") {
      const termo = consulta.toLowerCase();
      itens = itens.filter((p) => p.nome.toLowerCase().includes(termo));
    }
    const total = itens.length;
    const inicio = (pagina - 1) * tamanhoPagina;
    await rota.fulfill({
      json: {
        itens: itens.slice(inicio, inicio + tamanhoPagina),
        pagina,
        tamanhoPagina,
        total,
      },
    });
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
  const emailsCadastrados = new Set(["produtor.alfa@example.com"]);
  let cadastros = 0;

  function visaoSessao() {
    if (!sessaoAtiva) {
      return { autenticado: false };
    }
    if (usuarioAtual === "operador") {
      return {
        autenticado: true,
        idUsuario: "usr-operador",
        nome: "Operador Demonstracao",
        email: "operador.revenda@example.com",
        papeis: ["OPERADOR_REVENDA"],
      };
    }
    if (usuarioAtual === "beta") {
      return {
        autenticado: true,
        idUsuario: "usr-beta",
        nome: "Produtor Beta",
        email: "produtor.beta@example.com",
        papeis: ["PRODUTOR"],
      };
    }
    if (usuarioAtual === "novo") {
      return {
        autenticado: true,
        idUsuario: "usr-novo",
        nome: "Produtor Novo",
        email: "produtor.novo@example.com",
        papeis: ["PRODUTOR"],
      };
    }
    return {
      autenticado: true,
      idUsuario: "usr-alfa",
      nome: "Produtor Alfa",
      email: "produtor.alfa@example.com",
      papeis: ["PRODUTOR"],
    };
  }

  await page.route("**/api/v1/autenticacao/sessao", async (rota) => {
    await rota.fulfill({ json: visaoSessao() });
  });

  await page.route("**/api/v1/autenticacao/saida", async (rota) => {
    sessaoAtiva = false;
    await rota.fulfill({ status: 204, body: "" });
  });

  await page.route("**/api/v1/autenticacao/cadastro", async (rota) => {
    const corpo = rota.request().postDataJSON() as { nome?: string; email?: string; senha?: string };
    if (corpo.email && emailsCadastrados.has(corpo.email)) {
      await rota.fulfill({
        status: 409,
        json: {
          codigo: "EMAIL_DUPLICADO",
          mensagem: "Este e-mail já está cadastrado. Entre com sua senha ou use outro e-mail.",
        },
      });
      return;
    }
    cadastros += 1;
    if (cadastros > 1 && corpo.email === emailsCadastrados.values().next().value) {
      await rota.fulfill({
        status: 409,
        json: {
          codigo: "EMAIL_DUPLICADO",
          mensagem: "Este e-mail já está cadastrado. Entre com sua senha ou use outro e-mail.",
        },
      });
      return;
    }
    if (corpo.email) {
      emailsCadastrados.add(corpo.email);
    }
    sessaoAtiva = true;
    usuarioAtual = "novo";
    await rota.fulfill({
      json: {
        idUsuario: "usr-novo",
        nome: corpo.nome ?? "Produtor Novo",
        email: corpo.email ?? "produtor.novo@example.com",
        papeis: ["PRODUTOR"],
      },
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
          email: corpo.email,
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
        email: corpo.email,
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
    if (rota.request().method() === "POST") {
      const corpo = rota.request().postDataJSON() as { nome?: string };
      await rota.fulfill({
        json: { id: "aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa", nome: corpo.nome ?? "Propriedade" },
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
    expirarSessao() {
      sessaoAtiva = false;
    },
    falharProximaListagem() {
      falharProximaListagem = true;
    },
    atrasarCategoria(categoria: string, ms: number) {
      atrasosCategoria.set(categoria, ms);
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
