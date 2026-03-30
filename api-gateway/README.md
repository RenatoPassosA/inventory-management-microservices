# API Gateway

## Visão geral

O `api-gateway` é o ponto único de entrada do projeto **Inventory Management Microservices**.

Sua responsabilidade é receber as requisições externas e encaminhá-las para os microsserviços corretos, centralizando o acesso ao sistema e preparando a arquitetura para futuras evoluções, como autenticação, observabilidade e segurança.

## Responsabilidades

O gateway é responsável por:

- centralizar a entrada das requisições
- rotear chamadas para os microsserviços
- servir como base para futuras integrações com autenticação
- facilitar observabilidade e rastreabilidade

O gateway **não** é responsável por:

- regras de negócio
- acesso a banco de dados
- persistência
- validações de domínio
- criação de DTOs de negócio

## Microsserviços roteados

Atualmente, o gateway encaminha requisições para:

- `product-service`
- `price-service`
- `inventory-service`
- `order-service`

## Porta da aplicação

O gateway roda na porta:

```yaml
server.port: 8084