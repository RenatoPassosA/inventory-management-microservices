# System Overview

## Project

Inventory Management Microservices

Este projeto consiste em uma plataforma distribuída para gerenciamento de:

- produtos
- preços
- estoque
- pedidos

O sistema foi projetado utilizando **arquitetura de microsserviços** com **Clean Architecture** aplicada internamente nos serviços de domínio.

---

## Architectural Goals

O objetivo principal do projeto é estudar e aplicar conceitos de arquitetura moderna de software:

- microsserviços desacoplados
- banco de dados por serviço
- comunicação entre serviços via REST
- API Gateway como ponto único de entrada
- Clean Architecture nos serviços de domínio
- orquestração de fluxo de negócio distribuído

---

## Core Services

O sistema é composto pelos seguintes serviços principais:

| Service | Responsibility |
|------|------|
| API Gateway | Entrada única do sistema e roteamento |
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

Os serviços de domínio possuem:

- domínio isolado
- banco de dados próprio
- ciclo de evolução independente

---

## Internal Architecture

Os serviços de domínio seguem uma organização inspirada em Clean Architecture, com separação entre:

- domain
- application
- adapters
- infrastructure

---

### Domain

Contém regras de negócio centrais, entidades e contratos do domínio.

### Application

Contém casos de uso, orquestração da aplicação, commands, results e mapeamentos internos.

### Adapters

Contém controllers, requests, responses e pontos de entrada e saída da aplicação.

### Infrastructure

Contém persistência, configurações, clients REST e implementações técnicas.

---

## API Gateway

O `api-gateway` atua como **camada de borda** do sistema.

Sua responsabilidade é:

- receber requisições externas
- centralizar a entrada do sistema
- rotear chamadas para os microsserviços corretos

O gateway **não contém regra de negócio**, **não persiste dados** e **não substitui a comunicação interna entre os microsserviços**.

---

# Primary Business Flow

O principal fluxo de negócio do sistema é a **criação de pedidos**.

Etapas gerais do fluxo:

1. Cliente envia requisição para o gateway
2. API Gateway recebe a requisição
3. Gateway encaminha para o `order-service`
4. `order-service` valida a requisição e os itens
5. `order-service` consulta o `price-service`
6. `order-service` solicita reserva de estoque ao `inventory-service`
7. Pedido é criado, confirmado e persistido
8. Resposta é retornada ao cliente via gateway

---

## Current Scope

Na versão atual do projeto, o foco está em:

- modelagem de uma arquitetura de microsserviços
- separação de responsabilidades por serviço
- comunicação síncrona via REST
- API Gateway funcional
- fluxo distribuído de criação de pedidos
- testes automatizados
- integração contínua

Funcionalidades como autenticação centralizada podem ser tratadas como evolução futura, mas não fazem parte da entrega atual do projeto.