# Product Service API

Responsável pelo gerenciamento do catálogo de produtos.

## 1. Criar Produto
`POST /products`

Cria um novo produto no catálogo.

**Request Body:**
```json
{
  "name": "Notebook Dell XPS 15",
  "description": "Notebook para desenvolvedores",
  "sku": "DELL-XPS-15",
  "categoryId": "123e4567-e89b-12d3-a456-426614174000"
}
Responses:

201 Created: Produto criado com sucesso. Retorna o objeto criado (com ID e status ACTIVE).

400 Bad Request: Dados inválidos (ex: SKU duplicado, campos obrigatórios faltando).

401 Unauthorized: Token não fornecido ou inválido.

2. Buscar Produto por ID
GET /products/{id}

Responses:

200 OK:

JSON
{
  "id": "123e4567-e89b-12d3-a456-426614174001",
  "name": "Notebook Dell XPS 15",
  "description": "Notebook para desenvolvedores",
  "sku": "DELL-XPS-15",
  "categoryId": "123e4567-e89b-12d3-a456-426614174000",
  "status": "ACTIVE"
}
404 Not Found: Produto não existe.

3. Listar Produtos
GET /products?page=0&size=10&status=ACTIVE

Responses:

200 OK: Retorna uma lista paginada de produtos.

4. Atualizar Produto
PUT /products/{id}

Atualiza os dados de um produto existente.

Request Body:

JSON
{
  "name": "Notebook Dell XPS 15 - Atualizado",
  "description": "Nova descrição",
  "categoryId": "123e4567-e89b-12d3-a456-426614174000"
}
Responses:

200 OK: Produto atualizado.

400 Bad Request: Dados inválidos.

404 Not Found: Produto não encontrado.

5. Alterar Status do Produto
PATCH /products/{id}/status

Request Body:

JSON
{
  "status": "INACTIVE"
}
Responses:

204 No Content: Status alterado com sucesso.

404 Not Found: Produto não encontrado.

6. Verificar Existência do Produto
GET /products/{id}/exists

Utilizado por outros microsserviços (ex: Order Service) para validação rápida.

Responses:

200 OK:

JSON
{
  "exists": true
}