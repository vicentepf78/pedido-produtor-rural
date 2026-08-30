# PRD — MVP de pedidos digitais de insumos agrícolas

**Status:** rascunho para aprovação  
**Piloto:** uma revenda ou cooperativa no Paraná  
**Plataforma:** web

## Problema

ERPs de revendas concentram regras comerciais, estoque, fiscal e conformidade, mas
frequentemente tornam a montagem de pedidos grandes lenta para produtores, vendedores
e atendentes. O MVP valida uma jornada de catálogo, carrinho e solicitação de pedido
simples, deixando a complexidade operacional na retaguarda.

## Objetivo e métricas de sucesso

- Permitir que produtor, vendedor ou administrador monte uma solicitação com 20 itens
  em até cinco minutos, em teste de usabilidade.
- Medir conversão de carrinhos em solicitações enviadas.

## Público e papéis

| Papel | Responsabilidade no MVP |
| --- | --- |
| Produtor rural | Navega, simula, monta carrinho, cria conta no checkout e envia solicitação. |
| Vendedor/RTV | Monta ou revisa pedidos em nome do produtor e acompanha solicitações. |
| Administrador da revenda | Mantém catálogo comercial, condições de pagamento, depósitos e decide solicitações. |

## Escopo do MVP

### P1 — Catálogo e descoberta

O sistema permite navegar e buscar produtos de uma única revenda/cooperativa piloto.
As categorias iniciais incluem defensivos, sementes, fertilizantes, adjuvantes,
medicamentos veterinários e produtos gerais para lavoura.

- Carrega dados técnicos regulatórios de defensivos por arquivos CSV diários do
  Agrofit/MAPA.
- Permite ao administrador completar ou corrigir dados comerciais, imagens, vídeos,
  descrições, embalagens e visibilidade por produto.
- Permite busca por nome comercial, categoria, cultura, alvo e ingrediente ativo,
  quando aplicável.
- Exibe que o item exige receita agronômica quando essa classificação estiver
  configurada.

### P1 — Carrinho e simulação

Usuários autenticados ou não autenticados podem adicionar itens e quantidades a um
carrinho persistente no navegador.

- A página de produto mostra preço de referência e disponibilidade de referência.
- O carrinho permite itens em grande quantidade e alteração rápida de quantidades.
- O usuário simula condições configuradas pela revenda e vê total, desconto ou
  acréscimo, e quantidade de parcelas.
- A simulação não constitui preço final, aprovação de crédito ou reserva de estoque.

### P1 — Checkout e solicitação de pedido

O usuário inicia identificação apenas ao finalizar o carrinho.

- Usuário novo cria conta por e-mail e senha, informa dados pessoais e propriedades
  rurais necessários ao pedido.
- O usuário seleciona retirada em depósito/filial ou solicita entrega, indicando
  propriedade/endereço e data ou janela pretendida.
- O sistema gera uma **solicitação de pedido** com preço, condição e itens registrados
  em snapshot, no estado `AGUARDANDO_VALIDACAO`.
- Novos produtores e toda solicitação enviada aguardam revisão comercial.
- O produtor e o vendedor acompanham o estado e o motivo de eventual ajuste ou recusa.

### P1 — Operação comercial e reserva

O administrador revisa a solicitação, confirma ou ajusta preço, disponibilidade,
depósito e condição de pagamento.

- A aprovação confirma ou recusa a solicitação, sempre preservando o histórico.
- Somente a aprovação manual cria reserva de estoque.
- A reserva tem prazo configurável pela revenda e informa o depósito de atendimento.
- A data de retirada ou entrega futura permanece sujeita à confirmação até a aprovação.
- O sistema não seleciona lote no carrinho. O lote é informação operacional posterior.

### P1 — Itens sujeitos a receita

Defensivos classificados pela revenda como sujeitos a receita podem ser incluídos no
carrinho e enviados como solicitação.

- A revenda solicita, recebe e valida a receita agronômica após a solicitação.
- O sistema bloqueia confirmação/liberação de item regulado até a validação humana ser
  registrada.
- O MVP não emite, assina nem recomenda receita agronômica.

## Requisitos funcionais e critérios de aceitação

| ID | Requisito |
| --- | --- |
| CAT-01 | O sistema deve importar e atualizar defensivos a partir de CSV diário do Agrofit. |
| CAT-02 | O administrador deve complementar os dados comerciais de cada produto. |
| CAT-03 | O usuário deve localizar produtos por navegação e busca. |
| CART-01 | O usuário deve adicionar, editar e remover itens do carrinho sem treinamento. |
| CART-02 | O sistema deve recalcular instantaneamente o total de cada condição simulada. |
| ORDER-01 | O sistema deve pedir identificação/cadastro apenas ao finalizar o carrinho. |
| ORDER-02 | O sistema deve criar solicitação imutável em relação ao snapshot exibido no envio. |
| ORDER-03 | O administrador deve aprovar, solicitar ajuste ou recusar uma solicitação. |
| FUL-01 | O sistema deve permitir retirada ou solicitação de entrega e registrar depósito/data. |
| FUL-02 | O sistema deve criar reserva somente após aprovação manual, com prazo configurável. |
| COMP-01 | O sistema deve bloquear confirmação de produto regulado sem validação humana de receita. |
| AUD-01 | O sistema deve registrar autor, data/hora e motivo nas mudanças de estado. |

### Critérios de aceitação P1

1. QUANDO o usuário pesquisar por produto, categoria, cultura, alvo ou ingrediente
   ativo existente, ENTÃO o sistema DEVE mostrar somente produtos visíveis no catálogo
   do piloto.
2. QUANDO o usuário alterar a quantidade ou condição de pagamento de um carrinho,
   ENTÃO o sistema DEVE mostrar total, desconto ou acréscimo e número de parcelas
   correspondentes à simulação.
3. QUANDO usuário não autenticado iniciar o checkout, ENTÃO o sistema DEVE solicitar
   login ou cadastro antes de enviar a solicitação.
4. QUANDO uma solicitação for enviada, ENTÃO o sistema DEVE registrar itens, preços de
   referência, condição simulada, modalidade logística e data/janela pretendida em
   snapshot.
5. QUANDO o administrador aprovar a solicitação, ENTÃO o sistema DEVE registrar
   depósito e prazo de reserva e notificar a mudança de estado.
6. QUANDO uma solicitação contiver item sujeito a receita sem validação registrada,
   ENTÃO o sistema NÃO DEVE permitir sua confirmação ou liberação.
7. QUANDO a reserva expirar, ENTÃO o sistema DEVE alterar seu estado e manter trilha
   de auditoria sem apagar o pedido.

## Fora do escopo

- Marketplace com múltiplas empresas vendedoras.
- Integração com ERP, WMS, gateway de pagamento, crédito, fiscal ou mensageria.
- Pagamento online, análise de crédito, barter, conciliação e cobrança.
- Emissão de NF, simples faturamento, remessas, CT-e, MDF-e ou roteirização.
- Reserva de lote, picking, inventário em tempo real ou WMS completo.
- Emissão, assinatura ou validação automática de receita agronômica.
- Aplicativo nativo para iOS ou Android.

## Premissas confirmadas

| Decisão | Escolha |
| --- | --- |
| Arquitetura | Monólito modular. |
| Stack | Java/Spring no backend, React no frontend e PostgreSQL. |
| Cliente inicial | Uma revenda/cooperativa piloto. |
| UF piloto | Paraná. |
| Fonte de defensivos | Arquivos CSV diários gratuitos do Agrofit. |
| Catálogo comercial | Cadastro manual complementar pela revenda. |
| Preço antes do login | Preço de referência. |
| Checkout | Simulação e solicitação para aprovação comercial; sem pagamento online. |
| Estoque | Reserva somente depois da aprovação manual. |
| Mensageria | Sem broker no MVP. |

## Riscos e validações obrigatórias

- Validar com jurídico, responsável técnico e órgão estadual aplicável no Paraná o
  fluxo de receita, venda digital, entrega futura e retenção de documentos.
- Validar o licenciamento de fotos, vídeos e conteúdo comercial fornecidos por
  fabricantes ou revendas.
- Definir política comercial de divergência entre preço de referência e preço aprovado.
- Definir prazo de resposta comercial e política de expiração de reserva antes do
  piloto.

## Decisão de atualização Agrofit

O MVP fará importação automática diária do CSV Agrofit. Falhas mantêm a última versão
válida e alertam o administrador por e-mail, WhatsApp e painel de monitoramento.
