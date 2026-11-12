#import "/figures.typ": *

= Resultados

== Desempenho

A @tab-desempenho apresenta o tamanho do grafo e o tempo de corte para as cinco fotos de exemplo do repositório, com os parâmetros padrão. As medidas foram feitas em um notebook, com Java 25.

#figure(
  stack(
    spacing: 3mm,
    table(
      columns: (auto, auto, auto, auto, auto, auto),
      align: (left, center, center, right, right, right),
      stroke: none,
      inset: (x: 7pt, y: 5pt),
      table.hline(stroke: 0.8pt),
      table.header([*Foto*], [*Original*], [*Cópia de trabalho*], [*Vértices*], [*Arestas*], [*Tempo*]),
      table.hline(stroke: 0.5pt),
      [Astronauta], [512 × 512], [400 × 400], [160.002], [626.648], [0,96 s],
      [Xícara de café], [600 × 400], [400 × 266], [106.402], [416.145], [0,30 s],
      [Filhote], [600 × 400], [400 × 266], [106.402], [417.514], [0,26 s],
      [Pug], [600 × 400], [400 × 266], [106.402], [417.218], [0,34 s],
      [Thanos], [665 × 1000], [266 × 400], [106.402], [416.823], [0,37 s],
      table.hline(stroke: 0.8pt),
    ),
    fonte(autores),
  ),
  caption: [Tamanho do grafo e tempo de corte nas fotos de exemplo],
  placement: none,
) <tab-desempenho>

O número de vértices é o de pixels da cópia de trabalho mais os dois terminais. O número de arestas fica um pouco abaixo de quatro por pixel porque os pixels das bordas da imagem têm menos vizinhos e os pixels riscados têm apenas uma aresta terminal.

Em todos os casos o corte foi encontrado em menos de um segundo, muito abaixo do que o pior caso $O(V^2 E)$ sugere. Esse comportamento é esperado em grafos de imagens, que são regulares e rasos #cite(<boykov2004>).

== Efeito dos parâmetros

A @fig-parametros mostra o recorte da mesma foto, com os mesmos riscos, variando um parâmetro de cada vez.

#fig(
  grid(
    columns: 4,
    gutter: 3mm,
    ..(
      ("p-l0.jpg", $lambda = 0$),
      ("p-l400.jpg", $lambda = 400$),
      ("p-s5.jpg", $sigma = 5$),
      ("p-s100.jpg", $sigma = 100$),
    ).map(((file, label)) => stack(spacing: 2mm, image("/figures/" + file, width: 100%), align(center, text(size: 9pt, label))))
  ),
  [Recortes com valores extremos de $lambda$ e de $sigma$],
) <fig-parametros>

Com $lambda = 0$ as arestas entre vizinhos não pesam, e cada pixel é decidido só pela cor: as partes da armadura com cor de fundo desaparecem. Com $lambda$ alto, separar vizinhos fica caro e o recorte se fecha. Com $sigma$ muito baixo, qualquer variação de cor é tratada como borda, e o corte passa por dentro do objeto. Com $sigma$ muito alto, quase nenhuma variação é tratada como borda, e o corte deixa de acompanhar o contorno, levando partes do fundo.

Os valores intermediários adotados como padrão produziram recortes corretos nas cinco fotos, sem ajuste por foto.
