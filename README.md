# Golden Raspberry Awards

API e interface para consultar os indicados e vencedores da categoria Pior Filme do Golden Raspberry Awards. Na subida, a aplicação lê o CSV, grava os filmes em um H2 em memória e expõe o menor e o maior intervalo entre prêmios consecutivos de um mesmo produtor.

## Stack

- Java 26
- Spring Boot 4.1.2
- Angular 22.2
- Node.js 26
- H2 em memória
- Maven 3.10 (ou o Maven Wrapper incluído no backend)

## Backend

Requisitos: JDK 26 (o projeto também compila em JDKs suportados pelo Spring Boot 4.1, a partir do 17, desde que o `pom.xml` use essa versão).

```bash
cd backend
./mvnw test
./mvnw spring-boot:run
```

No Windows:

```bat
cd backend
mvnw.cmd test
mvnw.cmd spring-boot:run
```

A API sobe em `http://localhost:8080`.

### Endpoints

| Método | Caminho | Descrição |
| --- | --- | --- |
| `GET` | `/api/movies` | Lista os indicados. Aceita `?winner=true` ou `?winner=false`. |
| `GET` | `/api/movies/{id}` | Detalhe de um filme. Responde `404` quando o id não existe. |
| `GET` | `/api/producers/award-intervals` | Menor e maior intervalo entre vitórias consecutivas. |

Exemplo:

```json
{
  "min": [
    {
      "producer": "Joel Silver",
      "interval": 1,
      "previousWin": 1990,
      "followingWin": 1991
    }
  ],
  "max": [
    {
      "producer": "Matthew Vaughn",
      "interval": 13,
      "previousWin": 2002,
      "followingWin": 2015
    }
  ]
}
```

O intervalo usa apenas prêmios consecutivos. Um produtor com vitórias em 1990, 1991 e 2010 gera os intervalos 1 e 19, e não o vão de 20 anos entre a primeira e a última. Empates entram todos nas listas `min` e `max`. Produtores separados por vírgula ou por `and` são pessoas diferentes. Indicações sem `yes` na coluna `winner` não contam como prêmio.

O CSV padrão fica em `backend/src/main/resources/Movielist.csv`. Outro arquivo pode ser apontado com `awards.csv-location` (por exemplo `classpath:datasets/outro.csv`).

Console do H2: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:awards`
- usuário: `sa`
- senha: vazia

## Testes de integração

Os testes sobem a aplicação de verdade e chamam a API por HTTP.

```bash
cd backend
./mvnw test
```

`AwardIntervalsIntegrationTest` usa o CSV da proposta. `AlternateDatasetIntegrationTest` usa outro conjunto, com empate, vitórias no mesmo ano e uma indicação que não pode entrar na conta.

## Frontend

Requisitos: Node.js 22.22, 24.15 ou 26 ou mais recente, conforme o Angular 22.

Com a API já em execução:

```bash
cd frontend
npm install
npm start
```

A interface abre em `http://localhost:4200` e encaminha `/api` para `http://localhost:8080`.

Para gerar os arquivos estáticos:

```bash
npm run build
```

## Registro do uso de IA

O histórico desta implementação está em [docs/registro-ia.md](docs/registro-ia.md).
