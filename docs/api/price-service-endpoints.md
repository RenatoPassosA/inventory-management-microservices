### 📄 `docs/api/price-service-endpoints.md`

```markdown
# Price Service API

Responsável pela precificação dos produtos e histórico de valores.

## 1. Definir Novo Preço
`POST /prices`

Define um novo preço para um produto (o preço anterior passa a ser inativo e compõe o histórico).

**Request Body:**
```json
{
  "productId": "123e4567-e89b-12d3-a456-426614174001",
  "amount": 12500.00,
  "currency": "BRL",
  "validFrom": "2023-10-01T00:00:00Z"
}
Responses:

201 Created: Preço cadastrado com sucesso.

400 Bad Request: Dados inválidos ou productId nulo.

422 Unprocessable Entity: O productId informado não existe (validação assíncrona ou síncrona com o Product Service falhou).

2. Buscar Preço Atual do Produto
GET /prices/products/{productId}/current

Utilizado pelo Order Service para calcular o total do pedido.

Responses:

200 OK:

JSON
{
  "priceId": "abc-123",
  "productId": "123e4567-e89b-12d3-a456-426614174001",
  "amount": 12500.00,
  "currency": "BRL"
}
404 Not Found: Preço não encontrado para o produto informado.