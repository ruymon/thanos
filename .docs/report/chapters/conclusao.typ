= Conclusão

O trabalho mostrou que um problema de processamento de imagens pode ser resolvido inteiramente com os conceitos da disciplina. A foto foi modelada como um grafo ponderado, a separação entre objeto e fundo foi formulada como um corte mínimo, e o corte foi calculado pelo algoritmo de Dinitz, construído sobre as buscas em largura e em profundidade. O programa recorta fotos de cerca de cem mil pixels em menos de um segundo.

A solução tem três limitações, todas decorrentes do modelo:

+ a borda é rígida, pois cada pixel pertence inteiramente ao objeto ou ao fundo, de modo que fios de cabelo e pelos se perdem;
+ quando o objeto e o fundo têm cores parecidas, a estimativa por cor falha e são necessários mais riscos;
+ o resultado depende de dois parâmetros que podem exigir ajuste conforme a foto.

A terceira limitação explica por que os produtos comerciais adotam redes neurais: elas dispensam os riscos e os parâmetros, ao custo de um treinamento com milhões de imagens e de não ser possível explicar a decisão tomada para cada pixel. O corte em grafo vai no sentido oposto. Ele exige uma indicação do usuário, mas não precisa de treinamento, e cada pixel do resultado pode ser justificado pelas arestas que o corte atravessou.

Por fim, a separação entre o motor e as interfaces permitiu testar o algoritmo de fluxo em redes pequenas, conferidas à mão, antes de aplicá-lo a imagens. O mesmo pacote de grafos serviria, sem alteração, para outros problemas de fluxo, como a distribuição de tarefas entre pessoas.
