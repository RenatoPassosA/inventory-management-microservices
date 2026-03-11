# Service Responsibilities

Este documento define claramente as responsabilidades de cada microsserviço do sistema.

Separar responsabilidades é essencial para manter **baixo acoplamento e alta coesão**.

---

# API Gateway

Responsável por:

- entrada única do sistema
- roteamento de requisições
- validação de autenticação
- proteção das APIs internas

O gateway não contém regras de negócio.

---

# Auth Service

Responsável por:

- autenticação de usuários
- geração de tokens JWT
- gerenciamento de usuários
- controle de permissões

Principais funcionalidades:

- registro de usuário
- login
- validação de token

---

# Product Service

Responsável por:

- cadastro de produtos
- atualização de produtos
- consulta de produtos
- listagem de produtos

Este serviço mantém o **catálogo de produtos do sistema**.

---

# Price Service

Responsável por:

- definição de preços
- histórico de preços
- preços promocionais
- consulta de preço atual

Separar preços do produto permite:

- flexibilidade de pricing
- histórico de alterações
- promoções independentes

---

# Inventory Service

Responsável por:

- controle de estoque
- entradas de estoque
- saídas de estoque
- reservas de estoque
- histórico de movimentações

Este serviço garante consistência de disponibilidade de produtos.

---

# Order Service

Responsável por:

- criação de pedidos
- orquestração do fluxo de compra
- validação de produtos
- consulta de preços
- reserva de estoque
- cancelamento de pedidos

O Order Service atua como **orquestrador do fluxo de negócio distribuído**.