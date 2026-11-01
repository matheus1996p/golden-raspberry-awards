# Registro de interações com o agente

A proposta pede que o uso de ferramentas de inteligência artificial fique registrado no repositório. Este arquivo resume a sessão em que o projeto foi implementado.

## Pedido

Siga o teste técnico descrito em `Avaliação Outsera - Back-End.pdf`, usando o `Movielist.csv` enviado junto. O desenvolvimento deve ser em Java e Angular, nas versões estáveis mais recentes que funcionem juntas.

Implemente uma API RESTful, no nível 2 de maturidade de Richardson, para consultar os indicados e os vencedores da categoria Pior Filme do Golden Raspberry Awards.

Requisitos do sistema e da API:

1. Ao iniciar, leia o CSV e grave os filmes em um banco em memória, com um SGBD embarcado. Nenhuma instalação externa de banco pode ser necessária.
2. Exponha a leitura da lista de indicados e vencedores.
3. Exponha o produtor com o maior intervalo entre dois prêmios consecutivos e o produtor que conquistou dois prêmios no menor intervalo. A resposta deve seguir exatamente o formato da página 2 do PDF:

```json
{
  "min": [
    {
      "producer": "Producer 1",
      "interval": 1,
      "previousWin": 2008,
      "followingWin": 2009
    }
  ],
  "max": [
    {
      "producer": "Producer 1",
      "interval": 99,
      "previousWin": 1900,
      "followingWin": 1999
    }
  ]
}
```

4. O cálculo não pode depender deste CSV. Na avaliação serão usados outros conjuntos, com cenários diferentes. Considere prêmios consecutivos do mesmo produtor, inclusive empates, e ignore indicações que não sejam vitória.
5. Separe os produtores da coluna do CSV quando houver mais de um nome na mesma célula.

Requisitos não funcionais:

1. Implemente somente testes de integração. Eles devem garantir que o resultado está de acordo com os dados da proposta e também com um cenário diferente do arquivo original.
2. Inclua um README com as instruções para subir o projeto e para rodar os testes de integração.
3. Mantenha neste repositório um registro das interações com o agente.
4. O código deve ficar no nível de uma revisão tradicional: claro, preciso e sem atalhos que só funcionem para o arquivo de exemplo.

No frontend Angular, consuma essa API e apresente os intervalos mínimo e máximo e a lista de indicados e vencedores.

## O que foi lido antes de escrever código

- O PDF, com a carga do CSV na subida, o formato `min`/`max` (`producer`, `interval`, `previousWin`, `followingWin`), banco em memória, somente testes de integração, README e este registro.
- O CSV completo, para confirmar o separador `;`, o marcador `yes` e a forma como vários produtores aparecem na mesma célula (`vírgula` e `and`).

## Decisões

- Backend em Java 26 e Spring Boot 4.1.2. O Spring Boot 4.1.2 declara compatibilidade até o Java 27; o JDK 26 era a versão mais nova disponível para instalação nesta máquina.
- Frontend em Angular 22.2, com Node.js 26, que está na faixa de Node aceita por essa versão do Angular.
- Banco H2 em memória, sem instalação externa. O CSV entra por um `ApplicationRunner` na inicialização.
- `GET /api/producers/award-intervals` devolve o formato da página 2 do PDF. `GET /api/movies` e `GET /api/movies/{id}` cobrem a leitura da lista de indicados e vencedores. Só há GET nesses recursos, com `200`, `404` e `405`.
- O intervalo considera vitórias consecutivas do mesmo produtor, não o vão entre a primeira e a última. Empates são todos devolvidos.
- Nomes de produtores são separados por vírgula e pela palavra `and`. Uma indicação sem `yes` não é prêmio.
- Os testes de integração sobem a aplicação e chamam a API. Um deles usa o CSV da proposta. O outro usa um CSV menor, para o resultado não ficar preso a um único arquivo.
- A interface Angular mostra os dois intervalos e a lista, com busca, filtro de vencedores e a página de um filme.

## Como reproduzir

As instruções de execução e de teste estão no `README.md` da raiz.
