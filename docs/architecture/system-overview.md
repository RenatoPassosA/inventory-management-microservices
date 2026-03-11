# System Overview

## Project

Inventory Management Microservices

Este projeto consiste em uma plataforma distribuída para gerenciamento de:

- produtos
- preços
- estoque
- pedidos
- autenticação de usuários

O sistema foi projetado utilizando **arquitetura de microsserviços** com **Clean Architecture** aplicada internamente em cada serviço.

---

## Architectural Goals

O objetivo principal do projeto é estudar e aplicar conceitos de arquitetura moderna de software:

- microsserviços desacoplados
- banco de dados por serviço
- comunicação entre serviços via REST
- API Gateway como ponto único de entrada
- autenticação baseada em JWT
- Clean Architecture em cada serviço
- orquestração de fluxo de negócio distribuído

---

## Core Services

O sistema é composto pelos seguintes serviços principais:

| Service | Responsibility |
|------|------|
| API Gateway | Entrada única do sistema e roteamento |
| Auth Service | Autenticação e autorização |
| Product Service | Catálogo de produtos |
| Price Service | Gerenciamento de preços |
| Inventory Service | Controle de estoque |
| Order Service | Criação e gerenciamento de pedidos |

---

## Architectural Style

Arquitetura baseada em:

- **Microservices**
- **Domain separation**
- **Database per service**
- **REST communication**
- **Clean Architecture**

Cada serviço possui:

- domínio isolado
- banco de dados próprio
- ciclo de evolução independente

---

## Internal Architecture

Cada microsserviço segue a estrutura:

- domain
- application
- adapters
- infrastructure

---

### Domain
Contém regras de negócio centrais e entidades.

### Application
Contém casos de uso e serviços de aplicação.

### Adapters
Controllers e interfaces externas.

### Infrastructure
Persistência, segurança, REST clients e configurações.

---

# Primary Business Flow

O principal fluxo de negócio do sistema é a **criação de pedidos**.

Etapas:

1. Cliente envia requisição
2. API Gateway recebe a requisição
3. Gateway encaminha para Order Service
4. Order valida produto
5. Order consulta preço
6. Order solicita reserva de estoque
7. Pedido é persistido
8. Resposta é retornada ao cliente