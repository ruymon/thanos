#import "@preview/cetz:0.3.4"

// Fonte de figuras e tabelas, abaixo do corpo, como pede a ABNT.
#let fonte(body) = align(center, text(size: 9pt)[Fonte: #body])
#let autores = [Os autores (2026).]

#let fig(body, caption, source: autores) = figure(
  stack(spacing: 3mm, body, fonte(source)),
  caption: caption,
)

// Bloco que imita um terminal, com a saída real do programa.
#let terminal(code) = block(
  width: 100%,
  breakable: false,
  fill: luma(246),
  stroke: 0.5pt + luma(200),
  radius: 3pt,
  inset: 9pt,
  align(left, raw(code, block: true)),
)

#let ink = rgb("#1a1a1a")
#let red = rgb("#c92a2a")
#let blue = rgb("#1864ab")
#let gold = rgb("#f1c95b")
#let sky = rgb("#8fc1ea")

// Rede de fluxo pequena, com as capacidades e o corte mínimo.
#let rede-fluxo = cetz.canvas(length: 1cm, {
  import cetz.draw: *
  let node(pos, name, label, fill: white) = {
    circle(pos, radius: 0.36, fill: fill, stroke: 0.7pt + ink, name: name)
    content(name, label)
  }
  node((0, 0), "s", $s$, fill: rgb("#ffe3e3"))
  node((2.8, 1.3), "a", $a$)
  node((2.8, -1.3), "b", $b$)
  node((6.2, 1.3), "c", $c$)
  node((6.2, -1.3), "d", $d$)
  node((9, 0), "t", $t$, fill: rgb("#d0ebff"))
  let edge(from, to, cap, cut: false, at: 50%, anchor: "south") = {
    line(from, to, mark: (end: ">", fill: if cut { red } else { ink }), stroke: (paint: if cut { red } else { ink }, thickness: if cut { 1pt } else { 0.7pt }))
    content((from, at, to), anchor: anchor, padding: 3pt, text(size: 9pt, fill: if cut { red } else { ink }, cap))
  }
  edge("s", "a", [5])
  edge("s", "b", [4], anchor: "north")
  edge("a", "c", [2], cut: true, at: 28%)
  edge("b", "d", [2], cut: true, at: 28%, anchor: "north")
  edge("a", "d", [1], cut: true, at: 22%, anchor: "west")
  edge("b", "c", [1], cut: true, at: 22%, anchor: "west")
  edge("c", "t", [5])
  edge("d", "t", [4], anchor: "north")
  line((4.5, 2.1), (4.5, -2.1), stroke: (paint: red, thickness: 0.8pt, dash: "dashed"))
  content((4.5, 2.35), text(size: 9pt, fill: red)[corte mínimo: $2 + 1 + 1 + 2 = 6$])
  content((1.4, -2.35), text(size: 9pt)[lado de $s$])
  content((7.6, -2.35), text(size: 9pt)[lado de $t$])
})

// Grade de pixels como grafo, com os terminais s e t.
#let grade-grafo = cetz.canvas(length: 1cm, {
  import cetz.draw: *
  let cols = 5
  let rows = 3
  let d = 1.25
  let cx = (cols - 1) * d / 2
  let sp = (cx, rows * d + 0.9)
  let tp = (cx, -1.9)
  for i in range(cols) {
    for j in range(rows) {
      let p = (i * d, j * d)
      line(sp, p, stroke: 0.35pt + red.lighten(45%))
      line(tp, p, stroke: 0.35pt + blue.lighten(45%))
    }
  }
  for i in range(cols) {
    for j in range(rows) {
      let p = (i * d, j * d)
      let border = i == 2
      if i + 1 < cols {
        line(p, ((i + 1) * d, j * d), stroke: (paint: ink, thickness: if border { 0.4pt } else { 1.6pt }))
      }
      if j + 1 < rows {
        line(p, (i * d, (j + 1) * d), stroke: 1.6pt + ink)
      }
    }
  }
  for i in range(cols) {
    for j in range(rows) {
      rect((i * d - 0.24, j * d - 0.24), (i * d + 0.24, j * d + 0.24), fill: if i < 3 { gold } else { sky }, stroke: 0.7pt + ink)
    }
  }
  circle(sp, radius: 0.36, fill: rgb("#ffe3e3"), stroke: 0.7pt + ink, name: "s")
  content("s", $s$)
  circle(tp, radius: 0.36, fill: rgb("#d0ebff"), stroke: 0.7pt + ink, name: "t")
  content("t", $t$)
  content((cx + 0.6, rows * d + 0.9), anchor: "west", text(size: 9pt, fill: red)[fonte: objeto])
  content((cx + 0.6, -1.9), anchor: "west", text(size: 9pt, fill: blue)[sumidouro: fundo])
  content((cols * d - 0.6, d), anchor: "west", text(size: 9pt)[pixels (vértices)])
  content((2.5 * d, -0.62), frame: "rect", fill: white, stroke: none, padding: 2pt, text(size: 8pt)[aresta fina: borda])
})

// Peso da aresta entre vizinhos em função da diferença de cor.
#let curva-peso = cetz.canvas(length: 1cm, {
  import cetz.draw: *
  let w = 8.5
  let h = 3.6
  line((0, 0), (w + 0.4, 0), mark: (end: ">", fill: ink), stroke: 0.7pt + ink)
  line((0, 0), (0, h + 0.5), mark: (end: ">", fill: ink), stroke: 0.7pt + ink)
  line((0, h), (w, h), stroke: (paint: luma(150), thickness: 0.5pt, dash: "dotted"))
  content((-0.25, h), anchor: "east", $lambda$)
  content((-0.25, 0), anchor: "east", text(size: 9pt)[0])
  content((w / 2, -0.8), text(size: 9pt)[diferença de cor $Delta$])
  content((0.15, h + 0.75), anchor: "west", text(size: 9pt)[peso $w$])
  let curve(sigma, paint, label, lx, anchor: "south-west") = {
    let pts = range(0, 121).map(i => {
      let dd = i * 200 / 120
      (i * w / 120, h * calc.exp(-dd * dd / (2 * sigma * sigma)))
    })
    line(..pts, stroke: 1pt + paint)
    let ly = h * calc.exp(-lx * lx / (2 * sigma * sigma))
    content((lx * w / 200 + 0.15, ly + 0.1), anchor: anchor, text(size: 9pt, fill: paint, label))
  }
  curve(15, blue, $sigma = 15$, 26, anchor: "north-west")
  curve(40, red, $sigma = 40$, 42)
  curve(80, rgb("#2b8a3e"), $sigma = 80$, 95)
  for x in (50, 100, 150, 200) {
    line((x * w / 200, 0), (x * w / 200, -0.08), stroke: 0.6pt + ink)
    content((x * w / 200, -0.32), text(size: 8pt, str(x)))
  }
})

// Arquitetura em pacotes: as dependências apontam para dentro.
#let arquitetura = cetz.canvas(length: 1cm, {
  import cetz.draw: *
  let box(a, b, name, title, sub, fill: white, dash: none) = {
    rect(a, b, fill: fill, stroke: (paint: ink, thickness: 0.7pt, dash: dash), radius: 2pt, name: name)
    content((name + ".center"), align(center, stack(spacing: 4pt, text(size: 10pt, weight: "bold", title), text(size: 8pt, sub))))
  }
  box((0, 3.2), (3.4, 4.4), "cli", [cli], [terminal, picocli])
  box((4.3, 3.2), (7.7, 4.4), "ui", [ui], [janela, Swing e FlatLaf])
  box((8.6, 3.2), (12, 4.4), "app", [outra interface], [por exemplo, um aplicativo], dash: "dashed")
  rect((0, -0.5), (12, 2.3), fill: luma(248), stroke: 0.7pt + ink, radius: 2pt)
  content((0.2, 2.05), anchor: "west", text(size: 9pt, weight: "bold")[engine (Java puro)])
  box((0.4, 0), (4, 1.5), "seg", [segmentation], [foto e riscos em máscara])
  box((4.9, 0), (8.2, 1.5), "graph", [graph], [rede de fluxo, corte mínimo], fill: rgb("#fff3bf"))
  box((9.1, 0), (11.6, 1.5), "image", [image], [leitura e escala])
  line("cli", (1.7, 2.3), mark: (end: ">", fill: ink), stroke: 0.7pt + ink)
  line("ui", (6, 2.3), mark: (end: ">", fill: ink), stroke: 0.7pt + ink)
  line("app", (10.3, 2.3), mark: (end: ">", fill: ink), stroke: (paint: ink, thickness: 0.7pt, dash: "dashed"))
  line("seg", "graph", mark: (end: ">", fill: ink), stroke: 0.7pt + ink)
})
