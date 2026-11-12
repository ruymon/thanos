#import "/figures.typ": *

= Introdução

Este relatório apresenta o trabalho semestral da disciplina ECM306, dividido em duas partes: uma pesquisa sobre grafos e suas aplicações e uma aplicação em Java que resolve um problema com grafo.

O problema escolhido foi a remoção do fundo de uma fotografia. Ferramentas comerciais fazem isso com redes neurais treinadas em milhões de imagens. Aqui o mesmo resultado é obtido só com estruturas de dados: a foto é transformada em um grafo, e separar o objeto do fundo passa a ser o problema clássico de encontrar o corte mínimo de uma rede de fluxo, na formulação de Boykov e Jolly (#cite(<boykov2001>, form: "year")).

O programa resultante, chamado Thanos, recebe uma foto e dois riscos feitos pelo usuário, um sobre o objeto e outro sobre o fundo, e devolve o objeto recortado em um arquivo PNG transparente. A @fig-entrada-saida mostra a entrada e a saída.

#fig(
  grid(
    columns: 3,
    gutter: 4mm,
    image("/figures/thanos-photo.jpg", height: 62mm),
    image("/figures/thanos-scribbled.jpg", height: 62mm),
    image("/figures/thanos-cutout.jpg", height: 62mm),
  ),
  [Foto original, riscos do usuário e recorte produzido],
  source: [Os autores (2026), sobre foto de Super Festivals, licença CC BY 2.0.],
) <fig-entrada-saida>

Os objetivos do trabalho foram:

+ estudar os conceitos de grafos necessários para o problema, em especial redes de fluxo;
+ modelar a remoção de fundo como um problema de corte mínimo;
+ implementar a solução em Java sem usar bibliotecas de grafos ou de visão computacional;
+ organizar o código de forma que o algoritmo possa ser lido e testado isoladamente.

O código-fonte está disponível em Monteiro e Ingham (#cite(<thanos2026>, form: "year")).
