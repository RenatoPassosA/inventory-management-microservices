# Product Service API

Responsável pelo gerenciamento do catálogo de produtos.

A camada HTTP é desacoplada da aplicação através de DTOs (`request/response`) e mapeamento para `command/result`.

---

# 1. Criar Produto

POST /products

Cria um novo produto no catálogo.

## Request Body

```json
{
  "name": "Notebook Dell XPS 15",
  "description": "Notebook para desenvolvedores",
  "sku": "DELL-XPS-15",
  "category": "INFORMATICA"
}
```

## Responses

### 201 Created

```json
{
  "id": "uuid",
  "name": "Notebook Dell XPS 15",
  "description": "Notebook para desenvolvedores",
  "sku": "DELL-XPS-15",
  "category": "INFORMATICA",
  "status": "ACTIVE",
  "createdAt": "2026-03-11T12:41:29",
  "updatedAt": "2026-03-11T12:41:29"
}
```

### 400 Bad Request

- campos inválidos
- SKU duplicado

---

# 2. Buscar Produto por ID

GET /products/{id}

## Responses

### 200 OK

```json
{
  "id": "uuid",
  "name": "Notebook Dell XPS 15",
  "description": "Notebook para desenvolvedores",
  "sku": "DELL-XPS-15",
  "category": "INFORMATICA",
  "status": "ACTIVE",
  "createdAt": "...",
  "updatedAt": "..."
}
```

### 404 Not Found

- produto não encontrado

---

# 3. Buscar Produto por SKU

GET /products/sku/{sku}

## Responses

### 200 OK

Retorna o produto correspondente ao SKU.

### 404 Not Found

- produto não encontrado

---

# 4. Listar Produtos (Paginação)

GET /products?page=0&size=10

## Responses

### 200 OK

```json
{
  "content": [
    {
      "id": "uuid",
      "name": "Produto",
      "sku": "SKU-001",
      "category": "CAT",
      "status": "ACTIVE"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

---

# 5. Atualizar Produto

PUT /products/{id}

Atualiza os dados de um produto existente.

## Request Body

```json
{
  "name": "Notebook Atualizado",
  "description": "Nova descrição",
  "category": "ELETRONICOS"
}
```

## Responses

### 200 OK

Produto atualizado com sucesso.

### 400 Bad Request

- dados inválidos

### 404 Not Found

- produto não encontrado

---

# 6. Deletar Produto

DELETE /products/{id}

## Responses

### 204 No Content

Produto removido com sucesso.

### 404 Not Found

- produto não encontrado

---

# Validation

Validações aplicadas:

- name: obrigatório
- sku: obrigatório e único
- category: obrigatório

---

# Error Handling

Os erros são tratados globalmente via `GlobalExceptionHandler`.

Formato padrão:

```json
{
  "status": 400,
  "message": "Erro de validação",
  "errors": [
    {
      "field": "name",
      "message": "must not be blank"
    }
  ]
}
```

---

# Architecture Notes

- Controller utiliza DTOs (`request/response`)
- Application utiliza `command/result`
- Conversão feita via `ProductWebMapper`
- Persistência isolada via `ProductRepository`
- Testes de integração utilizam Testcontainers
