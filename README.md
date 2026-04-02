# Inventory Management Microservices

Projeto pessoal de estudo focado em arquitetura de microsserviços com Java e Spring Boot, aplicando Clean Architecture dentro dos serviços de domínio.

A proposta do sistema é simular um núcleo de operações comuns em cenários de e-commerce e plataformas de gestão, separando responsabilidades entre catálogo de produtos, precificação, estoque e pedidos.

---

## Sobre o projeto

Este projeto foi construído para praticar conceitos importantes de arquitetura moderna de software, como:

- microsserviços desacoplados
- separação de domínio por serviço
- banco de dados por serviço
- comunicação síncrona via REST
- API Gateway como ponto único de entrada
- Clean Architecture aplicada internamente nos serviços
- testes automatizados
- integração entre serviços

Na arquitetura atual, o sistema é composto por:

- **api-gateway**: camada de entrada e roteamento
- **product-service**: catálogo de produtos
- **price-service**: gestão de preços e histórico
- **inventory-service**: controle de estoque
- **order-service**: criação de pedidos e orquestração do fluxo principal

---

## Arquitetura

Os serviços de domínio seguem uma organização inspirada em Clean Architecture, com separação entre:

- `domain`
- `application`
- `adapters`
- `infrastructure`

Essa divisão ajuda a manter:

- regras de negócio isoladas
- baixo acoplamento com framework
- maior testabilidade
- melhor organização para evolução futura

Além disso, a comunicação entre serviços é feita por **REST APIs síncronas**, mantendo o banco de dados isolado por serviço e preservando a responsabilidade de negócio no serviço dono do domínio.

---

## Fluxo principal de negócio

O fluxo principal implementado no projeto é a **criação de pedidos**.

De forma resumida:

1. o cliente envia uma requisição para o gateway
2. o gateway encaminha a chamada para o `order-service`
3. o `order-service` valida o payload e os itens
4. o `order-service` consulta o `price-service` para obter o preço ativo
5. o `order-service` solicita reserva de estoque ao `inventory-service`
6. o pedido é persistido e retornado com status `CONFIRMED`

Esse fluxo foi pensado para demonstrar orquestração distribuída entre microsserviços, com tratamento de falhas de integração e composição de dados de mais de um domínio.

---

## Responsabilidades de cada serviço

### API Gateway

Responsável por receber requisições externas e rotear para o serviço adequado.

### Product Service

Responsável pelo catálogo de produtos, incluindo criação, consulta, atualização, deleção e listagem paginada.

### Price Service

Responsável pelo gerenciamento de preços dos produtos, incluindo versionamento e histórico. Cada produto pode ter vários preços, mas apenas um preço ativo por vez.

### Inventory Service

Responsável pelo controle de estoque, incluindo saldo disponível, saldo reservado, entrada, saída, reserva e liberação de estoque.

### Order Service

Responsável pela criação e consulta de pedidos. Atua como orquestrador do fluxo principal, integrando com os serviços de preço e estoque.

---

## Tecnologias utilizadas

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Maven / Maven Wrapper
- WebClient para comunicação entre serviços
- Swagger / OpenAPI para documentação e testes manuais dos endpoints
- JUnit 5
- Mockito
- MockMvc
- Testcontainers
- Docker / Docker Compose

---

## Estrutura esperada do repositório

```bash
.
├── api-gateway/
├── product-service/
├── price-service/
├── inventory-service/
├── order-service/
├── docs/
├── docker-compose.yml
└── README.md
```

---

## Como clonar o projeto

```bash
git clone https://github.com/RenatoPassosA/inventory-management-microservices
cd inventory-management-microservices
```

Substitua os valores acima pela URL e pelo nome real do seu repositório no GitHub.

---

## Pré-requisitos

Antes de rodar o projeto, tenha instalado na sua máquina:

- **Java 21**
- **Docker**
- **Docker Compose**
- **Git**

Opcionalmente:

- uma IDE como IntelliJ IDEA ou VS Code
- Postman ou Insomnia para testar os endpoints

---

## Como configurar o ambiente

Cada microsserviço possui sua própria configuração de banco e, em alguns casos, URLs internas para comunicação entre serviços.

Por isso, revise os arquivos `application.yml` ou `application.properties` de cada serviço antes de rodar o sistema.

Em geral, você deve verificar:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `server.port`
- URLs dos serviços internos, como:
  - `services.product-service.url`
  - `services.price-service.url`
  - `services.inventory-service.url`

### Banco de dados

A forma mais prática de subir a infraestrutura local é via Docker Compose.

Exemplo de fluxo:

```bash
docker compose up -d
```

Esse passo deve iniciar os bancos necessários para os microsserviços.

Depois disso, confira se:

- os containers estão ativos
- as portas do PostgreSQL estão corretas
- os bancos esperados foram criados

---

## Como rodar os serviços

Você pode iniciar os serviços individualmente.

Em cada pasta de serviço, execute:

```bash
./mvnw spring-boot:run
```

Exemplo:

```bash
cd product-service
./mvnw spring-boot:run
```

Repita o processo para os demais serviços:

- `product-service`
- `price-service`
- `inventory-service`
- `order-service`
- `api-gateway`

Se estiver no Windows usando PowerShell, pode ser necessário executar:

```powershell
.\mvnw spring-boot:run
```

---

## Ordem sugerida para subir localmente

Para evitar problemas de integração, uma ordem segura é:

1. subir os bancos com Docker Compose
2. iniciar `product-service`
3. iniciar `price-service`
4. iniciar `inventory-service`
5. iniciar `order-service`
6. iniciar `api-gateway`

Isso facilita a inicialização dos serviços que dependem de outros endpoints internos.

---

## Testando com Swagger

O projeto possui suporte a **Swagger / OpenAPI**, permitindo explorar e testar manualmente os endpoints de forma visual pelo navegador.

Depois de iniciar cada serviço, você pode acessar a interface Swagger para:

- visualizar os endpoints disponíveis
- inspecionar contratos de request e response
- executar chamadas sem precisar montar requisições manualmente no início
- validar rapidamente o comportamento das APIs durante os estudos

Em projetos Spring Boot, normalmente o Swagger fica exposto em rotas como:

```text
/swagger-ui.html
```

ou

```text
/swagger-ui/index.html
```

Exemplo local, dependendo da porta configurada do serviço:

```text
http://localhost:8081/swagger-ui/index.html
```

Você pode repetir isso para os demais microsserviços, ajustando a porta correspondente.

---

## Como rodar os testes

Os testes podem ser executados por serviço.

Dentro da pasta de cada microsserviço:

```bash
./mvnw test
```

Exemplo:

```bash
cd product-service
./mvnw test
```

Se quiser forçar limpeza antes da execução:

```bash
./mvnw clean test
```

Os testes do projeto cobrem cenários unitários e de integração, utilizando ferramentas como JUnit 5, Mockito, MockMvc e Testcontainers.

Quando houver testes de integração com banco real em container, certifique-se de que o Docker esteja em execução.

---

## Exemplos de uso do sistema

Alguns fluxos que podem ser testados manualmente:

- cadastrar um produto
- cadastrar o preço desse produto
- criar o registro de estoque
- adicionar quantidade ao estoque
- criar um pedido consumindo preço e reservando estoque

Esse encadeamento ajuda a demonstrar a comunicação entre os serviços na prática.

---

## Objetivo do projeto

Este projeto foi desenvolvido com foco em estudo, prática arquitetural e fortalecimento de portfólio.

Mais do que apenas expor CRUDs, a proposta foi exercitar:

- desenho de responsabilidades por domínio
- integração entre serviços
- organização de código em camadas
- testes automatizados
- uso de banco por serviço
- fluxo distribuído de negócio

---

## Próximas evoluções possíveis

Como evolução futura, o projeto pode receber:

- autenticação e autorização centralizadas
- observabilidade e tracing
- mensageria para fluxos assíncronos
- circuit breaker e resiliência
- containerização completa de todos os serviços
- deploy em nuvem

---

Para mais detalhes técnicos sobre a arquitetura, os serviços, os fluxos do sistema e os endpoints das APIs, consulte a documentação disponível na pasta `docs/`.

## Documentação

A documentação do projeto está disponível na pasta `docs/`.

### Arquitetura
- [Visão Geral do Sistema](./docs/architecture/system-overview.md)
- [Responsabilidades dos Serviços](./docs/architecture/service-responsibilities.md)
- [Fluxo de Pedidos](./docs/architecture/order-flow.md)
- [Comunicação entre Serviços](./docs/architecture/communication.md)

### APIs
- [Endpoints do Product Service](./docs/api/product-service-endpoints.md)
- [Endpoints do Price Service](./docs/api/price-service-endpoints.md)
- [Endpoints do Inventory Service](./docs/api/inventory-service-endpoints.md)
- [Endpoints do Order Service](./docs/api/order-service-endpoints.md)

### Domínio
- [Domínio de Produto](./docs/domain/product-domain.md)
- [Domínio de Preço](./docs/domain/price-domain.md)
- [Domínio de Estoque](./docs/domain/inventory-domain.md)
- [Domínio de Pedido](./docs/domain/order-domain.md)

