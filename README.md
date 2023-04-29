# Projeto de Backend para Redes Sociais

Este projeto é um servidor backend simples para uma aplicação de redes sociais, construído usando Java, Spring Boot e MongoDB.

![Java](https://img.shields.io/badge/-Java-red?logo=java&style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/-Spring%20Boot-green?logo=spring&style=for-the-badge)
![MongoDB](https://img.shields.io/badge/-MongoDB-blue?logo=mongodb&style=for-the-badge)

## Funcionalidades

- Cadastro de novos usuários
- Criar, ler, atualizar e deletar publicações

## Pré-requisitos

- Java 17
- MongoDB
- Maven

## Getting Started

1. Clone o repositório:
```
git clone https://github.com/bc-fullstack-03/Alan-Franco-Backend.git
```

2. Navegue até o diretório do projeto:
```
cd Alan-Franco-Backend
```

3. Build the project:
```
./mvnw clean install
```

4. Execute o projeto:
```
./mvnw spring-boot:run
```

O servidor deve estar em execução em `http://localhost:8080/api`.

## Endpoints da API

- Cadastro de usuários: `POST /api/users/register`
- Obter todas as publicações: `GET /api/posts`
- Criar uma nova publicação: `POST /api/posts`
- Obter uma publicação por ID: `GET /api/posts/{id}`
- Atualizar uma publicação por ID: `PUT /api/posts/{id}`
- Deletar uma publicação por ID: `DELETE /api/posts/{id}`

## Licença

Este projeto está licenciado sob a Licença MIT. Consulte [MIT](https://choosealicense.com/licenses/mit/) para obter mais informações.

