# Meteora Backend

Backend em **Java 17 + Spring Boot 3** para a loja Meteora (o site estático em Bootstrap 5),
cobrindo todos os pontos apontados na análise de lacunas do front-end.

## Como rodar

Pré-requisitos: Java 17+ e Maven (ou use o `./mvnw` se adicionar o wrapper).

```bash
cd meteora-backend
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`, com um banco **H2 em arquivo** (`./data/meteora.mv.db`),
criado e populado automaticamente na primeira execução (ver `seed/DataSeeder.java`) com as
mesmas 6 categorias e 6 produtos que hoje estão fixos no `index.html`.

Console do banco (dev): `http://localhost:8080/h2-console` — JDBC URL `jdbc:h2:file:./data/meteora`, usuário `sa`, sem senha.

Para produção, existe `application-prod.properties` (PostgreSQL) — ative com
`--spring.profiles.active=prod` e configure as variáveis `DATABASE_URL`, `DATABASE_USERNAME`,
`DATABASE_PASSWORD`, `JWT_SECRET` e `CORS_ALLOWED_ORIGINS` (veja a seção "Deploy" abaixo).

**Usuário admin criado automaticamente:**
- e-mail: `admin@meteora.com.br`
- senha: `admin123`
(troque a senha em produção — está aqui só para você testar o painel administrativo)

## Como cada lacuna da análise foi coberta

| Lacuna identificada no front-end | Onde foi resolvida |
|---|---|
| 1. Persistência de dados | JPA + Hibernate, entidades em `model/`, banco H2 (dev) / PostgreSQL (prod) |
| 2. Backend/API | Spring Boot REST, controllers em `controller/` |
| 3. Carrinho de compras | `CartController` + `CartService` (`/api/cart`) |
| 4. Busca de produtos | `ProductController` (`GET /api/products?busca=...`) |
| 5. Autenticação de usuários | `AuthController` + JWT (`security/`) (`/api/auth/registrar`, `/api/auth/login`) |
| 6. Checkout e pagamento | `CheckoutController` + `CheckoutService` + `PaymentService` (mock de Pix/cartão/boleto) |
| 7. Newsletter | `NewsletterController` (`POST /api/newsletter/subscribe`) |
| 8. Gestão de estoque | Campo `estoque` em `Product`, baixado no checkout, endpoint de ajuste no admin |
| 9. Painel administrativo | `controller/admin/*` — CRUD de produtos/categorias, gestão de pedidos (`ROLE_ADMIN`) |
| 10. Páginas que faltavam (detalhe de produto, por categoria) | `GET /api/products/{id}`, `GET /api/products?categoria=slug` |
| 11. Segurança e infraestrutura | Spring Security + JWT, senha com BCrypt, CORS configurável, validação de entrada (Bean Validation), tratamento global de erros (`GlobalExceptionHandler`) |

## Principais endpoints

### Públicos
- `GET /api/products` — lista/busca/filtra (`?busca=camiseta`, `?categoria=camisetas`, paginação `?page=0&size=12`)
- `GET /api/products/{id}` — detalhe de um produto
- `GET /api/categories` — lista categorias
- `POST /api/newsletter/subscribe` — inscreve e-mail, retorna cupom de boas-vindas
- `POST /api/auth/registrar` / `POST /api/auth/login` — retornam um JWT

### Autenticados (enviar `Authorization: Bearer <token>`)
- `GET/POST/PUT/DELETE /api/cart` e `/api/cart/itens/{produtoId}` — carrinho do usuário logado
- `POST /api/checkout` — finaliza a compra (endereço + forma de pagamento), baixa estoque e "processa" o pagamento
- `GET /api/orders` e `GET /api/orders/{id}` — histórico de pedidos do usuário

### Administrativos (`ROLE_ADMIN`)
- `POST/PUT/DELETE /api/admin/products/{id}`, `PATCH /api/admin/products/{id}/estoque`
- `POST/PUT /api/admin/categories/{id}`
- `GET /api/admin/orders`, `PATCH /api/admin/orders/{id}/status`

## Observações importantes

- **Pagamento**: `PaymentService` é um *mock* — simula aprovação de Pix/cartão e geração de
  boleto, sem chamar nenhum gateway real. Para produção, troque a implementação pela integração
  real (Mercado Pago, Stripe, PagSeguro etc.) mantendo a mesma interface.
- **CORS**: configurável via `CORS_ALLOWED_ORIGINS` (`SecurityConfig`). Em dev, já libera
  `localhost:5500`/`5173`/`3000` por padrão. **Em produção é obrigatório** definir essa variável
  com o endereço real do seu front-end publicado — ex. Vercel: `https://seu-projeto.vercel.app`
  (aceita curinga para preview deployments: `https://seu-projeto-*.vercel.app`) ou GitHub Pages:
  `https://seu-usuario.github.io`. Sem isso, o backend rejeita as requisições do front-end.
- **Segredo do JWT**: definido via variável de ambiente `JWT_SECRET`; o valor padrão em
  `application.properties` é só para desenvolvimento local.
- Este backend é uma API REST "headless": ele não substitui o `index.html`, e sim fornece os dados
  e as operações que o front-end (estático ou reescrito) deve consumir via `fetch`/`axios`.

## Deploy (GitHub + Render)

O GitHub por si só não executa aplicações Java — ele só guarda o código. Para o backend ficar
no ar, use um serviço que rode o `.jar`. O Render **não tem runtime nativo para Java**, então
este projeto builda via **Docker** (o `Dockerfile` na raiz já cuida disso — multi-stage: builda
com Maven, roda só com o JRE) e vem pronto com `render.yaml` para deploy automático:

1. Suba este repositório para o GitHub.
2. No [Render](https://render.com), clique em **New + → Blueprint** e aponte para o repositório.
   Ele lê o `render.yaml` sozinho: cria o serviço web (buildando o `Dockerfile`), um banco
   PostgreSQL gratuito, e já gera o `JWT_SECRET` automaticamente.
3. Depois do primeiro deploy, vá em **Environment** e preencha `CORS_ALLOWED_ORIGINS` com o
   endereço do seu front-end publicado (ex.: `https://seu-projeto.vercel.app`).
4. Anote a URL pública que o Render gerou (ex.: `https://meteora-backend.onrender.com`) — é ela
   que vai em `API_BASE_URL_PRODUCAO` no `config.js` do front-end.

**Sobre o plano gratuito do Render**: o serviço web gratuito "dorme" após um período sem
requisições, então a primeira chamada depois disso demora alguns segundos para acordar (normal,
não é bug). O banco PostgreSQL gratuito expira após 90 dias — se isso acontecer, crie um novo
banco no painel do Render e reconecte as variáveis `DATABASE_*`.

Se preferir outro serviço (Railway, Fly.io etc.), o mesmo `Dockerfile` funciona neles também —
a maioria constrói a imagem automaticamente ao detectar o arquivo na raiz do repositório.
