#import "/figures.typ": *

= Grafos e suas aplicações

== Conceitos

Um grafo $G = (V, E)$ é formado por um conjunto $V$ de vértices e um conjunto $E$ de arestas, em que cada aresta liga dois vértices. Quando as arestas têm sentido, o grafo é dirigido. Quando cada aresta carrega um número, o grafo é ponderado, e esse número é o peso da aresta #cite(<cormen2012>).

As duas representações usuais em memória são a matriz de adjacência, que ocupa espaço $O(V^2)$ e responde em tempo constante se dois vértices são vizinhos, e a lista de adjacência, que ocupa $O(V + E)$ e é a escolha natural para grafos esparsos #cite(<sedgewick2011>). Um grafo de pixels é esparso, pois cada pixel tem no máximo quatro vizinhos, de modo que este trabalho usa uma variação da lista de adjacência.

Os dois percursos básicos são a busca em largura (BFS), que visita os vértices em ordem de distância à origem, e a busca em profundidade (DFS), que segue um caminho até o fim antes de retroceder. Ambos custam $O(V + E)$ e são os blocos de construção do algoritmo usado na aplicação.

== Aplicações

Grafos modelam qualquer situação em que importam as relações entre elementos. A @tab-aplicacoes resume as aplicações clássicas estudadas na pesquisa.

#figure(
  stack(
    spacing: 3mm,
    table(
      columns: (auto, 1fr, auto),
      align: left,
      stroke: none,
      inset: (x: 6pt, y: 5pt),
      table.hline(stroke: 0.8pt),
      table.header([*Problema*], [*Modelo em grafo*], [*Algoritmo*]),
      table.hline(stroke: 0.5pt),
      [Rota mais curta], [Vértices são locais, pesos são distâncias], [Dijkstra],
      [Rede de menor custo], [Árvore geradora mínima], [Kruskal, Prim],
      [Ordem de tarefas], [Grafo dirigido acíclico de dependências], [Ordenação topológica],
      [Distribuição de tarefas], [Emparelhamento em grafo bipartido], [Fluxo máximo],
      [Capacidade de uma rede], [Rede de fluxo com fonte e sumidouro], [Edmonds-Karp, Dinitz],
      [Segmentação de imagens], [Pixels são vértices, corte separa regiões], [Corte mínimo],
      table.hline(stroke: 0.8pt),
    ),
    fonte[Os autores (2026), com base em Cormen et al. (#cite(<cormen2012>, form: "year")) e Kleinberg e Tardos (#cite(<kleinberg2006>, form: "year")).],
  ),
  caption: [Aplicações clássicas de grafos],
) <tab-aplicacoes>

As duas últimas linhas são a mesma técnica. A segmentação de imagens é apresentada por Kleinberg e Tardos (#cite(<kleinberg2006>, form: "year")) como aplicação direta de fluxo em redes, e foi o caminho seguido neste trabalho por fugir dos exemplos de mapas e rotas.

== Redes de fluxo e corte mínimo

Uma rede de fluxo é um grafo dirigido em que cada aresta $(u, v)$ tem uma capacidade $c(u, v) >= 0$, com dois vértices especiais: a fonte $s$ e o sumidouro $t$. Um fluxo atribui a cada aresta um valor que não excede a capacidade e que se conserva em todo vértice intermediário. O problema do fluxo máximo pede o maior valor total que pode sair de $s$ e chegar a $t$.

Um corte $(S, T)$ divide os vértices em dois grupos, com $s$ em $S$ e $t$ em $T$. A capacidade do corte é a soma das capacidades das arestas que saem de $S$ e entram em $T$. O problema do corte mínimo pede o corte de menor capacidade.

Os dois problemas são equivalentes. O teorema do fluxo máximo e corte mínimo, de Ford e Fulkerson (#cite(<ford1956>, form: "year")), afirma que o valor do fluxo máximo é igual à capacidade do corte mínimo. A @fig-rede mostra uma rede em que ambos valem 6: as quatro arestas centrais ficam saturadas e formam o corte.

#fig(rede-fluxo, [Rede de fluxo com as capacidades e o corte mínimo]) <fig-rede>

A consequência prática é que não é preciso examinar os $2^(|V|)$ cortes possíveis. Basta calcular o fluxo máximo e, ao final, fazer uma busca a partir de $s$ usando só arestas não saturadas: os vértices alcançados formam o lado $S$ do corte mínimo #cite(<cormen2012>).

A @tab-algoritmos compara os algoritmos estudados. Todos repetem a mesma ideia de encontrar um caminho de $s$ a $t$ com capacidade disponível e enviar fluxo por ele. A diferença está em como os caminhos são escolhidos.

#figure(
  stack(
    spacing: 3mm,
    table(
      columns: (auto, 1fr, auto),
      align: left,
      stroke: none,
      inset: (x: 6pt, y: 5pt),
      table.hline(stroke: 0.8pt),
      table.header([*Algoritmo*], [*Escolha dos caminhos*], [*Pior caso*]),
      table.hline(stroke: 0.5pt),
      [Ford-Fulkerson], [Qualquer caminho com capacidade disponível], [$O(E dot f)$],
      [Edmonds-Karp], [O caminho mais curto, por BFS], [$O(V E^2)$],
      [Dinitz], [Todos os caminhos mais curtos de uma só vez], [$O(V^2 E)$],
      table.hline(stroke: 0.8pt),
    ),
    fonte[Ford e Fulkerson (#cite(<ford1956>, form: "year")), Edmonds e Karp (#cite(<edmonds1972>, form: "year")) e Dinic (#cite(<dinic1970>, form: "year")). $f$ é o valor do fluxo máximo.],
  ),
  caption: [Algoritmos de fluxo máximo],
) <tab-algoritmos>

O algoritmo de Dinitz, publicado em 1970 e conhecido na literatura como algoritmo de Dinic #cite(<dinitz2006>), foi o adotado. Ele trabalha em fases, e cada fase tem dois passos:

+ uma BFS a partir de $s$ atribui a cada vértice um nível, que é a sua distância até a fonte;
+ uma DFS envia fluxo apenas por arestas que vão de um nível para o seguinte, até que nenhum caminho reste. O resultado é chamado de fluxo bloqueante.

As fases se repetem enquanto a BFS alcançar $t$. Como a distância de $s$ a $t$ aumenta a cada fase, há no máximo $|V| - 1$ fases.
