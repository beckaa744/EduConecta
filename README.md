# Controller

Esta pasta contém as classes responsáveis por expor os endpoints HTTP da aplicação, utilizando o `HttpServer` nativo do Java.

Cada classe controller recebe as requisições do frontend, aciona a camada `dao` correspondente para obter ou persistir os dados, e devolve a resposta formatada em JSON.