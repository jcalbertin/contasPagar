# Instruções para execução do projeto

### Compilando o projeto e gerando via docker

> mvn clean install
> 
> docker-compose up --build
> 
> Acesse o endereço http://localhost:8080/swagger-ui/index.html
> 
> Para parar o container e destruir os recursos criados, execute o comando abaixo:
> 
>  docker compose down

### Documentacao da API de COntas a Pagar

http://localhost:8080/api-docs
http://localhost:8080/swagger-ui/index.html

### Gerando um token válido para testes da API

> curl --location 'http://localhost:8080/api/auth/token' \
--header 'Content-Type: application/json' \
--data '{
"username": "joe"
}'

será retornado um token que tem que ser inserido no header das requsições (ou no Authorize do Swagger)

> {
"token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb3NlIiwiaWF0IjoxNzM2MzgyM ... exemplo de token_"
}

#### Exemplos de requisições

- Criar uma conta a pagar

> curl --location 'http://localhost:8080/api/v1/accounts' \
--header 'accept: application/json' \
--header 'Authorization: Bearer TOKEN_GERADO' \
--header 'Content-Type: application/json' \
--data '{
"id": 1,
"dataVencimento": "2025-12-31",
"dataPagamento": "2025-12-30",
"valor": 1545.50,
"descricao": "Manutenção predial",
"situacao": "Cancelada"
}'

- Obter uma conta a pagar pelo ID

> curl --location 'http://localhost:8080/api/v1/accounts/2' \
--header 'Authorization: Bearer TOKEN_GERADO'

- Busca paginada de contas a pagar conforme criterios de busca (com ou sem descrição)

> curl --location 'http://localhost:8080/api/v1/accounts?startDate=2025-01-08&endDate=2025-01-08&page=0&size=10&sort=ASC' \
--header 'Authorization: Bearer TOKEN_GERADO'

- Atualizar uma conta a pagar

> curl --location --request PUT 'http://localhost:8080/api/v1/accounts/2' \
--header 'Authorization: Bearer TOKEN_GERADO' \
--header 'Content-Type: application/json' \
--data '{
"dataVencimento": "2025-01-08",
"dataPagamento": "2025-01-08",
"valor": 0,
"descricao": "string",
"situacao": "PENDENTE",
"createdAt": "2025-01-08T18:05:49.546Z",
"updatedAt": "2025-01-08T18:05:49.546Z"
}'

- Atualizar a situacao de uma conta pelo ID

> curl --location --request PATCH 'http://localhost:8080/api/v1/accounts/5/status' \
--header 'Authorization: Bearer TOKEN_GERADO' \
--header 'Content-Type: application/json' \
--data '{
"situacao": "Cancelada"
}'

- Obter total pago conforme um periodo de datas de pagamento

> curl --location 'http://localhost:8080/api/v1/accounts/total-pago?startDate=2025-02-20&endDate=2025-03-30' \
--header 'accept: application/json' \
--header 'Authorization: Bearer TOKEN_GERADO'

- Importar dados de contas a pagar a partir de um arquivo .csv

> curl --location 'http://localhost:8080/api/v1/accounts/import' \
--header 'Authorization: Bearer TOKEN_GERADO' \
--form 'file=@"arquivo_dados_100k.csv"'