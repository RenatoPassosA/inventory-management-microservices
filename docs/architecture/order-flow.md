# Order Creation Flow

Este documento descreve o principal fluxo de negócio do sistema atualmente implementado.

O fluxo representa a **criação de um pedido** em uma arquitetura distribuída com `api-gateway`, `order-service`, `price-service` e `inventory-service`.

---

# Step 1 — Request

O cliente envia uma requisição para criar um pedido por meio do gateway:

```http
POST /orders
```

Contendo os itens e suas quantidades.

Exemplo:

```json
{
  "items": [
    {
      "productId": "3124d0d6-bc4e-4494-b372-47798a441f9e",
      "quantity": 2
    },
    {
      "productId": "9abf5f4e-d882-4f92-a2e8-f4c4f54c6d10",
      "quantity": 1
    }
  ]
}
```

---

# Step 2 — Gateway

A requisição chega ao **API Gateway**, que atua como ponto único de entrada do sistema.

Na versão atual do projeto, o gateway:

- recebe a requisição externa
- identifica a rota correspondente
- encaminha a chamada para o `order-service`

> Nesta etapa, o gateway ainda não aplica autenticação, pois essa funcionalidade foi deixada como evolução futura do projeto.

---

# Step 3 — Order Service Receives the Request

O `order-service` recebe a requisição encaminhada pelo gateway e inicia a orquestração do fluxo de negócio.

Esse serviço é o responsável por coordenar as validações necessárias para criação do pedido.

---

# Step 4 — Product and Item Validation

Antes de processar o pedido, o `order-service` valida a estrutura da requisição recebida.

As principais validações incluem:

- comando não nulo
- lista de itens não vazia
- item não nulo
- `productId` não nulo
- `quantity` maior que zero

Se alguma dessas validações falhar:

- o pedido falha
- a criação não é concluída

---

# Step 5 — Price Retrieval

Para cada item do pedido, o `order-service` consulta o `price-service` para obter o preço ativo do produto.

Essa consulta ocorre por meio do client interno do serviço.

Exemplo conceitual de chamada:

```http
GET /prices/product/{productId}
```

Se algum preço não estiver disponível:

- o pedido falha
- a criação não é concluída

---

# Step 6 — Stock Reservation

Após montar os itens com seus respectivos preços, o `order-service` solicita a reserva de estoque ao `inventory-service`.

No fluxo atual, essa operação ocorre por produto, utilizando o endpoint:

```http
PATCH /inventories/product/{productId}/reserve-stock
```

com uma requisição contendo a quantidade a reservar.

Se não houver estoque suficiente:

- o pedido falha
- a resposta de erro é propagada ao cliente via gateway

---

# Step 7 — Order Persistence and Confirmation

Se todas as validações forem bem-sucedidas e a reserva de estoque ocorrer com sucesso:

- o pedido é criado
- o pedido é confirmado
- o pedido é persistido no banco de dados do `order-service`

No fluxo atual implementado, o pedido utiliza o status:

- `CONFIRMED`

O pedido armazenará informações como:

- itens
- quantidades
- preços unitários
- valor total
- status

---

# Step 8 — Response

O `order-service` retorna a resposta ao cliente por meio do gateway.

A resposta contém os dados consolidados do pedido, como por exemplo:

- id do pedido
- status
- itens
- total

Exemplo conceitual:

```json
{
  "orderId": "8c7b7e65-4d79-4f70-a6dd-9ac8728dd4f0",
  "status": "CONFIRMED",
  "totalAmount": 299.90,
  "items": [
    {
      "productId": "3124d0d6-bc4e-4494-b372-47798a441f9e",
      "quantity": 2,
      "unitPrice": 99.95,
      "totalPrice": 199.90
    },
    {
      "productId": "9abf5f4e-d882-4f92-a2e8-f4c4f54c6d10",
      "quantity": 1,
      "unitPrice": 100.00,
      "totalPrice": 100.00
    }
  ]
}
```

> O conteúdo exato da resposta pode variar conforme o contrato exposto pelo `order-service`.

---

# Failure Scenarios

Principais cenários de falha no fluxo:

- requisição inválida
- item inválido
- `productId` nulo
- quantidade menor ou igual a zero
- preço não encontrado
- estoque insuficiente
- falha de comunicação entre serviços

Nesses casos, o `order-service` deve retornar um erro consistente ao cliente.

Dependendo do momento em que a falha ocorrer, o pedido pode nem chegar a ser persistido.