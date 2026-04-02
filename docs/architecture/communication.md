# Service Communication

A comunicação entre microsserviços ocorre via **REST APIs síncronas**.

Cada serviço expõe endpoints HTTP para consumo pelos demais serviços quando necessário.

O sistema também possui um **API Gateway** como ponto único de entrada para clientes externos, enquanto a comunicação interna entre microsserviços continua sendo feita diretamente entre os serviços responsáveis pelo fluxo de negócio.

---

# Communication Principles

- baixo acoplamento
- contratos de API bem definidos
- independência de banco de dados
- comunicação via HTTP entre serviços
- responsabilidade de negócio mantida no serviço dono do domínio

---

# External Access and Internal Communication

## External Access

Clientes externos devem acessar o sistema preferencialmente por meio do gateway.

Exemplos de rotas expostas:

- `/products/**`
- `/prices/**`
- `/inventories/**`
- `/orders/**`

## Internal Communication

A existência do gateway não substitui a comunicação interna entre microsserviços.

Quando necessário, um serviço chama diretamente outro serviço para executar parte do fluxo distribuído.

Exemplos:
- `order-service` → `price-service`
- `order-service` → `inventory-service`
- `price-service` → `product-service`
- `inventory-service` → `product-service`

---

# Service Interactions

## Order → Product

Validação de existência de produto durante o fluxo de pedido.

Endpoints possíveis, conforme a implementação adotada:

- `GET /products/{id}`
- `GET /products/{id}/exists`

---

## Order → Price

Consulta de preço por produto para composição do pedido.

Endpoint utilizado:

- `GET /prices/product/{productId}`

---

## Order → Inventory

Validação de disponibilidade e reserva de estoque durante a criação do pedido.

O `order-service` se comunica com o `inventory-service` para verificar estoque e efetivar a reserva necessária para o fluxo do pedido.

---

## Price → Product

Validação de produto ao cadastrar ou atualizar preço.

Endpoints possíveis:

- `GET /products/{id}`
- `GET /products/{id}/exists`

---

## Inventory → Product

Validação de produto ao registrar ou consultar estoque.

Endpoints possíveis:

- `GET /products/{id}`
- `GET /products/{id}/exists`

---

# Error Handling

Cada serviço deve retornar erros padronizados com:

- status HTTP adequado
- mensagem clara
- timestamp
- path da requisição
- código de erro interno quando necessário

Exemplo de estrutura:

```json
{
  "timestamp": "2026-03-11T10:00:00Z",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Product not found",
  "path": "/products/99"
}