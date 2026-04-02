# Price Service API

Responsável pela gestão de preços dos produtos, incluindo histórico e versionamento.

---

## 📌 Observações Arquiteturais

- O controller utiliza:
  - Request/Response (camada HTTP)
- O use case utiliza:
  - Command/Result (camada application)
- A conversão é feita via:
  - PriceWebMapper

---

## 1. Criar preço

POST /prices

Cria um novo preço para um produto.

### Regras:
- O produto deve existir (validação via Product Service)
- Não pode existir outro preço ativo para o produto

---

### Request Body

{
  "productId": "uuid",
  "price": 199.90,
  "currency": "BRL"
}

---

### Responses

#### 201 Created

{
  "id": "uuid",
  "productId": "uuid",
  "price": 199.90,
  "currency": "BRL",
  "active": true,
  "createdAt": "2026-03-18T10:50:21",
  "updatedAt": "2026-03-18T10:50:21"
}

#### 400 Bad Request
- payload inválido

#### 404 Not Found
- produto não existe

#### 409 Conflict
- já existe preço ativo

---

## 2. Buscar preço atual por produto

GET /prices/product/{productId}

Retorna o preço ativo do produto.

---

### Response

#### 200 OK

{
  "id": "uuid",
  "productId": "uuid",
  "price": 199.90,
  "currency": "BRL",
  "active": true,
  "createdAt": "...",
  "updatedAt": "..."
}

#### 404 Not Found
- preço ativo não encontrado

---

## 3. Atualizar preço

PUT /prices/{id}

Atualiza o preço criando uma nova versão.

---

### Comportamento

- desativa o preço atual
- cria um novo preço com:
  - novo ID
  - active = true

---

### Request Body

{
  "price": 200.00,
  "currency": "BRL"
}

---

### Response

#### 200 OK

{
  "id": "novo-uuid",
  "productId": "uuid",
  "price": 200.00,
  "currency": "BRL",
  "active": true,
  "createdAt": "...",
  "updatedAt": "..."
}

#### 404 Not Found
- preço não encontrado
- produto não existe

---

## 4. Histórico de preços

GET /prices/history/{productId}

Retorna todos os preços do produto.

---

### Response

#### 200 OK

{
  "productId": "uuid",
  "prices": [
    {
      "id": "uuid",
      "price": 199.90,
      "active": false
    },
    {
      "id": "uuid",
      "price": 200.00,
      "active": true
    }
  ]
}

#### 404 Not Found
- produto não existe

---

## Integração com Product Service

O Price Service valida a existência do produto via:

- ProductClient
- WebClient

Configuração:

services:
  product-service:
    url: http://localhost:8081

---

## Observação importante

O Price Service não compartilha preços entre produtos.

Modelo atual:

Product (1) → (N) Price

- preço pertence a um único produto
- histórico é mantido
- apenas um ativo por produto
