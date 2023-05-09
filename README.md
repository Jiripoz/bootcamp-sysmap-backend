# Projeto de Backend para Redes Sociais

Este projeto é um servidor backend para uma API de redes sociais, construído usando Java, Spring Boot, MongoDB e executado utilizando docker.

![Java](https://img.shields.io/badge/-Java-red?logo=java&style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/-Spring%20Boot-green?logo=spring&style=for-the-badge)
![MongoDB](https://img.shields.io/badge/-MongoDB-blue?logo=mongodb&style=for-the-badge)
![Docker](https://img.shields.io/badge/-Docker-blue?logo=docker&style=for-the-badge)
## Funcionalidades

- Cadastro de novos usuários
- Criar, ler, atualizar e deletar publicações

## Pré-requisitos

- Docker

## Getting Started

1. Clone o repositório:
```
git clone https://github.com/bc-fullstack-03/Alan-Franco-Backend.git
```

2. Navegue até o diretório do projeto:
```
cd Alan-Franco-Backend
```

3. Construa a imagem Docker:
```
docker build -t alan-franco-sysmap-backend .
```

4. Execute o projeto com o Docker Compose:
```
docker-compose up
```
5. Execute o seguinte comando no terminal para poder registrar usuários:
```
docker exec -it alan-franco-backend-mongo mongosh --authenticationDatabase admin social_media_db --eval "db.roles.insertMany([{ name: 'USER' },{ name: 'MODERATOR' },{ name: 'ADMIN' }])"

```

O servidor deve estar em execução em `http://localhost:8080/api`.

## Endpoints da API

- Cadastro de usuários: `POST /api/users/register`
- Obter todas as publicações: `GET /api/posts`
- Publicação - Criar Nova: `POST /api/posts`
- Publicação - Obter por ID: `GET /api/posts/{postid}`
- Publicação - Atualizar por ID: `PUT /api/posts/{postid}`
- Publicação - Deletar por ID: `DELETE /api/posts/{postid}`
- Feed - Posts recentes de quem você segue: `GET /api/foryou`
- Feed - Posts feitos por você: `GET /api/home`
- Feed - Posts feitos por usuário com ID: `GET /api/{userid}`
- Comentário - Criar: `POST /api/posts/{postid}/comments`
- Comentário - Deletar: `DELETE /api/posts/{postid}/comments/{commentid}` 
- Likes/Deslikes em publicações: `POST /api/posts/{postid}/like`

## Licença

Este projeto está licenciado sob a Licença MIT. Consulte [MIT](https://choosealicense.com/licenses/mit/) para obter mais informações.

## Imagens do projeto

⌛ Loading... 