# Service Communication

A comunicação entre microsserviços ocorre inicialmente via **REST APIs síncronas**.

Cada serviço expõe endpoints HTTP para consumo pelos demais serviços.

---

# Communication Principles

- baixo acoplamento
- contratos de API bem definidos
- independência de banco de dados
- validação via APIs

---

# Service Interactions

## Order → Product

Validação de existência de produto.

Endpoints utilizados:

- `GET /products/{id}`
- `GET /products/{id}/exists`

---

## Order → Price

Consulta preço atual.

Endpoint sugerido:

- `GET /prices/products/{productId}/current`

---

## Order → Inventory

Reserva de estoque.

Endpoint sugerido:

- `POST /inventory/reservations`
- `DELETE /inventory/reservations/{reservationId}`

---

## Price → Product

Validação de produto ao cadastrar preço.

Endpoint sugerido:

- `GET /products/{id}/exists`

---

## Inventory → Product

Validação de produto ao registrar estoque.

Endpoint sugerido:

- `GET /products/{id}/exists`

---

# Future Evolution

Possíveis evoluções futuras:

- comunicação assíncrona
- event-driven architecture
- message brokers
- sagas distribuídas

## Error Handling

Cada serviço deve retornar erros padronizados com:

- status HTTP adequado
- mensagem clara
- timestamp
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