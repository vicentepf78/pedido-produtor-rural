# Pedidos de insumos agrícolas (MVP0)

Loja de uma revenda: o produtor monta o carrinho como convidado, identifica-se
só no checkout e recebe “Pedido recebido”. O operador inspeciona os pedidos
em `/retaguarda/pedidos`.

Este README cobre o ambiente local para testar as telas.

## O que você precisa

- Java 21
- Node.js 20 ou superior
- Docker com Compose
- IntelliJ IDEA (Community ou Ultimate), se for subir pelo IDE

Portas usadas:

| Serviço | Porta |
| --- | --- |
| PostgreSQL | 5432 |
| Backend (Spring Boot) | 8080 |
| Frontend (Vite) | 5173 |

O Vite encaminha `/api` e `/actuator` para `http://localhost:8080`. Abra
sempre o frontend em `http://localhost:5173`, não a porta 8080.

## 1. Subir o PostgreSQL

Na raiz do repositório:

```bash
docker compose up -d
```

Os valores padrão batem com `.env.example`: banco `agriplataforma`, usuário
`agri`, senha `agri`.

Confirme que o container aceita conexões antes de subir o backend.

## 2. Backend e frontend pelo terminal

O perfil `local` é obrigatório no HTTP (`http://localhost`). Sem ele o cookie
de sessão exige `Secure` e o login no navegador falha.

### Backend

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Flyway aplica as migrações na primeira subida. Health:

```bash
curl -s http://localhost:8080/actuator/health
```

A resposta deve conter `"status":"UP"`.

### Frontend

Em outro terminal:

```bash
cd frontend
npm install
npm run dev
```

Abra [http://localhost:5173](http://localhost:5173). A raiz redireciona para
`/catalogo`.

## 3. Backend e frontend pelo IntelliJ

O repositório já aponta o Maven para `backend/pom.xml` (JDK 21).

### Abrir o projeto

1. **File → Open** e escolha a raiz deste repositório.
2. Confie no projeto e aguarde o import do Maven (`backend/pom.xml`).
3. Confirme **File → Project Structure → Project SDK = 21**.

### PostgreSQL

Use o terminal do IntelliJ na raiz (`docker compose up -d`) ou a janela
**Services** se o plugin Docker estiver habilitado.

### Configuração de execução do backend

1. Abra `backend/src/main/java/br/agriplataforma/AgriPlatformApplication.java`.
2. Clique no ícone de play ao lado de `main` e escolha **Modify Run
   Configuration…** (ou **Run → Edit Configurations…**).
3. Preencha:

   | Campo | Valor |
   | --- | --- |
   | Name | `AgriPlatform local` |
   | Module | `agri-platform` (o módulo Maven de `backend`) |
   | Main class | `br.agriplataforma.AgriPlatformApplication` |
   | Active profiles | `local` |

4. Em **Environment variables**, deixe vazio se o Postgres estiver no
   `localhost` com os padrões do Compose. Para sobrescrever:

   ```text
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/agriplataforma;SPRING_DATASOURCE_USERNAME=agri;SPRING_DATASOURCE_PASSWORD=agri
   ```

5. **Run** (Shift+F10) ou **Debug** (Shift+F9).

O log deve mostrar o perfil `local` e o Tomcat em 8080. Se o processo
encerrar com falha de cookie `Secure`, o perfil `local` não entrou na
configuração.

### Frontend no IntelliJ

1. Instale o plugin **JavaScript and TypeScript** (Community) se o IDE não
   reconhecer `package.json`.
2. Abra `frontend/package.json`.
3. Em **npm**, rode o script `install` uma vez e depois o script `dev`.

Ou crie **npm** → **Run Configuration**:

| Campo | Valor |
| --- | --- |
| package.json | `frontend/package.json` |
| Command | `run` |
| Scripts | `dev` |

O console do Vite mostra a URL em `http://localhost:5173`.

Os dois processos precisam ficar rodando ao mesmo tempo: Spring no 8080 e
Vite no 5173.

## 4. Telas para testar

Use o viewport perto de 390×844 se quiser o recorte do MVP0.

| Rota | Quem usa | O que ver |
| --- | --- | --- |
| `/catalogo` | Convidado / produtor | 30 produtos; busca; Ureia indisponível |
| `/carrinho` | Convidado | Quantidade, remover, total |
| `/checkout` | Identificação tardia | Entrar ou criar conta, propriedade, retirada |
| `/meus-pedidos` | Produtor | Lista após confirmar |
| `/pedidos/{id}` | Produtor | “Pedido recebido” e snapshot |
| `/retaguarda/pedidos` | Operador | Lista e detalhe; **sem atalho na nav** — cole a URL |

Jornada mínima do produtor: catálogo → adicionar Aurora → carrinho →
checkout → entrar → propriedade e retirada → **Confirmar pedido**.

A retaguarda não aparece na barra inferior. Digite
`http://localhost:5173/retaguarda/pedidos`.

## 5. Contas de demonstração

Semeadas pelo Flyway. São fixtures de desenvolvimento, não contas reais.

| Papel | E-mail | Senha | Propriedades |
| --- | --- | --- | --- |
| Produtor | `produtor.alfa@example.com` | `Senha#Fixture2026` | Fazenda Norte, Sitio Recanto |
| Produtor | `produtor.beta@example.com` | `Senha#Fixture2026` | Fazenda Sul |
| Operador | `operador.revenda@example.com` | `Senha#Fixture2026` | — |

Conta nova: no checkout, use **Criar conta** e informe o nome da
propriedade se a lista vier vazia.

Sair: no checkout, depois de autenticado, use **Sair**. O carrinho de
convidado permanece.

## 6. Se algo não abrir

- **Login some / cookie não grava:** o backend não está com o perfil
  `local`.
- **Frontend em 5173 e API 404:** o Spring não está no 8080, ou você abriu
  a porta 8080 no navegador em vez do Vite.
- **Health DOWN / falha de datasource:** o Compose não está no ar, ou a
  porta 5432 está ocupada por outro Postgres.
- **Retaguarda “Acesso negado”:** a sessão é de produtor. Saia e entre com
  o operador, ou use uma janela anônima.
- **Catálogo vazio após busca em branco:** apague o campo e saia do foco;
  listagem sem `consulta` mostra os 30 itens.

## 7. Verificação automática (opcional)

Não substitui o teste manual das telas.

```bash
make gate
```

Roda checagens da spec, testes de unidade, integração (Testcontainers) e
Playwright (UI com API mockada).
