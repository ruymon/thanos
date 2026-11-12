#import "/figures.typ": *

= Modelagem do problema

A modelagem segue Boykov e Jolly (#cite(<boykov2001>, form: "year")) e é descrita a seguir em cinco passos.

== Passo 1: a foto vira um grafo

Cada pixel é um vértice. Cada pixel é ligado por uma aresta aos vizinhos de cima, de baixo, da esquerda e da direita. Acrescentam-se dois vértices que não são pixels: a fonte $s$, que representa o objeto, e o sumidouro $t$, que representa o fundo. Todo pixel é ligado a ambos. A @fig-grade mostra o grafo de uma imagem de 5 por 3 pixels.

#fig(grade-grafo, [Grafo de uma imagem pequena, com a fonte e o sumidouro]) <fig-grade>

== Passo 2: peso entre pixels vizinhos

A aresta entre dois pixels vizinhos $p$ e $q$ recebe um peso que mede o quanto eles se parecem:

$ w(p, q) = lambda dot e^(-Delta^2 / (2 sigma^2)), quad "com" quad Delta^2 = (R_p - R_q)^2 + (G_p - G_q)^2 + (B_p - B_q)^2 $ <eq-peso>

em que $Delta$ é a distância entre as duas cores no espaço RGB. Pixels de cores iguais recebem o peso máximo $lambda$. Quanto mais diferentes as cores, menor o peso, como mostra a @fig-curva. O resultado é que as arestas ficam fracas exatamente nas bordas dos objetos.

#fig(curva-peso, [Peso da aresta em função da diferença de cor, para três valores de $sigma$]) <fig-curva>

A curva não é uma reta por dois motivos. Diferenças pequenas vêm de ruído e de iluminação, não de bordas, e por isso quase não reduzem o peso. Diferenças grandes já indicam uma borda, e a partir daí o peso se mantém próximo de zero. Os dois parâmetros são ajustáveis: $lambda$ controla a altura da curva e $sigma$, a largura.

== Passo 3: os riscos do usuário

O usuário risca alguns pixels do objeto e alguns do fundo. Um pixel riscado como objeto recebe uma aresta de capacidade praticamente infinita com $s$, e um pixel riscado como fundo, com $t$. Essas arestas nunca são cortadas, o que fixa esses pixels no lado correto.

== Passo 4: os demais pixels

Para os pixels sem risco, as ligações com $s$ e $t$ vêm da cor. Com os pixels riscados constroem-se dois histogramas de cores, um do objeto e um do fundo, com 16 faixas por canal. A capacidade das arestas de um pixel $p$ de cor $c_p$ é:

$ w(s, p) = -ln P(c_p | "fundo"), quad quad w(p, t) = -ln P(c_p | "objeto") $ <eq-terminal>

Um pixel cuja cor é comum no objeto tem probabilidade alta de ser objeto, logo a sua aresta com $t$ é fraca e barata de cortar, enquanto a aresta com $s$ é forte. A @fig-palpite mostra essa estimativa para a foto de exemplo. Sozinha ela erra: partes da armadura têm a cor do fundo.

#fig(
  grid(
    columns: 2,
    gutter: 6mm,
    image("/figures/thanos-scribbled.jpg", height: 62mm),
    image("/figures/thanos-heat.png", height: 62mm),
  ),
  [Riscos do usuário e estimativa por cor: vermelho indica objeto, azul indica fundo],
) <fig-palpite>

== Passo 5: o corte mínimo é o recorte

Qualquer corte dessa rede separa os pixels em dois grupos, os que continuam ligados a $s$ e os que continuam ligados a $t$. A capacidade do corte soma duas parcelas: o custo de contrariar a estimativa por cor de cada pixel e o custo de separar pixels vizinhos parecidos.

$ E = sum_(p) D_p + sum_((p, q)) w(p, q) dot [L_p != L_q] $ <eq-energia>

em que $D_p$ é a aresta terminal cortada do pixel $p$ e $L_p$ é o lado em que ele ficou. O corte mínimo equilibra as duas parcelas: um pixel com cor de fundo, mas cercado por pixels do objeto, permanece no objeto porque separá-lo dos vizinhos custaria mais. O lado de $s$ no corte mínimo é o objeto recortado.
