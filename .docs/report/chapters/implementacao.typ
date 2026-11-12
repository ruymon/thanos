#import "/figures.typ": *

= Implementação

A aplicação foi escrita em Java 17, sem ferramenta de build, e compilada diretamente com `javac`. O projeto tem 34 arquivos e 2.315 linhas de código, das quais 1.178 formam o motor.

== Arquitetura

O código está dividido em um motor e duas interfaces que o consomem, como mostra a @fig-arquitetura. O motor não usa nenhuma biblioteca externa, pois construí-lo era o objetivo do trabalho. As bibliotecas aparecem só nas interfaces: picocli para interpretar a linha de comando e FlatLaf para a aparência da janela Swing.

#fig(arquitetura, [Pacotes da aplicação e suas dependências]) <fig-arquitetura>

O motor tem três pacotes. O pacote `graph` contém a rede de fluxo e os algoritmos de fluxo máximo, e não sabe o que é uma imagem: recebe vértices, arestas e capacidades e devolve um corte. O pacote `segmentation` conhece imagens: monta a rede a partir dos pixels e dos riscos e converte o corte em uma máscara. O pacote `image` lê, grava e redimensiona imagens.

As dependências apontam sempre para o motor. O terminal não conhece a janela, e nenhuma interface conhece o pacote `graph`. Um teste automático lê as importações de todos os arquivos e falha se essa regra for violada. Com essa separação, uma nova interface, como um aplicativo para celular, usaria o mesmo motor sem alterá-lo.

== Representação do grafo

A rede de fluxo é armazenada em quatro vetores de inteiros, em uma lista de adjacência compacta: `firstEdge` guarda a primeira aresta de cada vértice, `nextEdge` encadeia as arestas de um mesmo vértice, `target` guarda o destino de cada aresta e `capacity`, a capacidade residual. Essa forma evita criar um objeto por aresta, o que importa em um grafo com centenas de milhares delas.

Cada aresta é inserida junto com a sua aresta reversa, em posições consecutivas. Assim, a reversa da aresta `e` é sempre `e ^ 1`, e enviar fluxo se resume a subtrair da capacidade de uma e somar à da outra.

As capacidades são inteiras. Os pesos das equações #ref(<eq-peso>, supplement: none) e #ref(<eq-terminal>, supplement: none) são multiplicados por 100 e arredondados, o que elimina erros de ponto flutuante na comparação de capacidades, e o fluxo total é acumulado em `long`.

== Algoritmo de fluxo máximo

Os algoritmos implementam uma mesma interface, `MaxFlowSolver`, com um único método que recebe a rede, a fonte e o sumidouro e devolve o corte mínimo. Há duas implementações:

- `DinicMaxFlow`, o algoritmo de Dinitz, usado pelo programa. A BFS preenche um vetor de níveis. A DFS é iterativa, com pilha explícita, para não esgotar a pilha de chamadas em caminhos longos, e mantém para cada vértice a posição da próxima aresta a examinar, de modo que arestas esgotadas não são revisitadas na mesma fase.
- `EdmondsKarpMaxFlow`, mais simples e mais lento, usado como referência nos testes.

Ao final, uma busca a partir da fonte sobre as arestas com capacidade residual marca os vértices do lado $S$. Esses vértices são os pixels do objeto.

== Decisões de projeto

O corte é calculado sobre uma cópia da foto com no máximo 400 pixels no lado maior. A máscara resultante é ampliada de volta ao tamanho original e a sua borda é suavizada. Com isso, o tempo de resposta fica abaixo de um segundo, o que permite ajustar os parâmetros de forma interativa.

Os valores padrão dos parâmetros, $lambda = 200$ e $sigma = 40$, foram escolhidos por experimentação nas fotos de exemplo.

== Testes

O projeto tem 32 testes automáticos em JUnit. Os principais verificam que:

+ a rede de exemplo de Cormen et al. (#cite(<cormen2012>, form: "year")) tem fluxo máximo 23 nas duas implementações;
+ Dinitz e Edmonds-Karp produzem o mesmo valor em 200 redes geradas ao acaso;
+ uma imagem sintética é recortada no contorno esperado;
+ a linha de comando aceita os parâmetros e informa os erros de uso;
+ as regras de dependência entre pacotes são respeitadas.

== Uso

O programa é iniciado pelo comando `thanos`. Sem argumentos, ele apresenta a ajuda:

#terminal("$ ./thanos
Usage: thanos [-hV] <command>
One snap and the background is gone.

Removes the background of a photo by finding the minimum cut
of a graph where every pixel is a vertex.

Options:
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.

Commands:
  cut  Cut the subject out of a photo and save it as a transparent PNG.
  ui   Open the window: scribble, snap, refine, export.")

O comando `cut` recorta uma foto pelo terminal. Como não há como desenhar no terminal, os riscos são fornecidos em um arquivo de imagem, com vermelho sobre o objeto e azul sobre o fundo. Ao terminar, o programa informa o tamanho do grafo, o valor do corte mínimo e o tempo:

#terminal("$ ./thanos cut samples/thanos/photo.jpg --scribbles samples/thanos/scribbles.png
Photo             665 x 1000 px
Cut on            266 x 400 px
Graph             106,402 vertices, 416,823 edges
Smoothness        200.0
Edge sensitivity  40.0
Minimum cut       57,511,818
Subject           48.2% of the photo
Time              0.37 s
Saved             samples/thanos/photo-cutout.png")

Os dois parâmetros do modelo são opções do comando, cada uma com dois nomes: `--lambda` ou `--smoothness` para $lambda$, e `--sigma` ou `--edge-sensitivity` para $sigma$.

O comando `ui` abre a janela mostrada na @fig-janela. O usuário abre uma foto, risca o objeto e o fundo com o mouse e aciona o botão Snap. O recorte aparece ao lado, e os dois controles deslizantes recalculam o corte a cada ajuste.

#fig(
  image("/figures/janela.png", width: 100%),
  [Janela da aplicação: riscos à esquerda e recorte à direita],
  source: [Os autores (2026), sobre foto da NASA, em domínio público.],
) <fig-janela>
