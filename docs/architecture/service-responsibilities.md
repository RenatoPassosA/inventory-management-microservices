# Service Responsibilities

Este documento define as responsabilidades de cada componente principal do sistema.

A separação de responsabilidades é essencial para manter **baixo acoplamento**, **alta coesão** e uma arquitetura distribuída mais fácil de evoluir.

---

# API Gateway

Responsável por:

- atuar como ponto único de entrada do sistema
- receber requisições de clientes externos
- rotear chamadas para o microsserviço correto
- centralizar o acesso externo às APIs
- servir como base para futuras políticas transversais

O gateway **não contém regras de negócio**, **não persiste dados** e **não substitui a comunicação interna entre microsserviços**.

> Nesta versão atual do projeto, o gateway ainda não aplica autenticação.

---

# Product Service

Responsável por:

- cadastro de produtos
- atualização de produtos
- consulta de produtos por id
- listagem de produtos

Este serviço mantém o **catálogo de produtos do sistema**.

Seu domínio é voltado à existência, identificação e manutenção dos produtos disponíveis para os demais fluxos da aplicação.

---

# Price Service

Responsável por:

- cadastro de preços
- atualização de preços
- consulta de preço por produto
- manutenção do histórico de preços

Separar preços do produto permite:

- flexibilidade na evolução da política de preços
- histórico de alterações
- desacoplamento entre catálogo e precificação

O `price-service` é consultado pelo `order-service` para compor o valor do pedido.

---

# Inventory Service

Responsável por:

- criação do registro de estoque de um produto
- consulta de estoque
- adição de estoque
- remoção de estoque
- reserva de estoque
- liberação de estoque reservado

Este serviço garante o controle de disponibilidade dos produtos no sistema.

No fluxo atual do projeto, ele expõe operações por produto, como:

- adicionar estoque
- remover estoque
- reservar estoque
- liberar estoque reservado

---

# Order Service

Responsável por:

- criação de pedidos
- orquestração do fluxo principal de compra
- composição dos itens do pedido
- consulta de preços por produto
- solicitação de reserva de estoque
- persistência do pedido

O `order-service` atua como **orquestrador do fluxo de negócio distribuído**.

Na implementação atual, ele coordena principalmente a integração com:

- `price-service`, para obter o preço dos produtos
- `inventory-service`, para reservar estoque antes da confirmação do pedido