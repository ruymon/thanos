#import "/template.typ": *

#show: project.with(
  title: "Remoção de fundo de imagens por corte mínimo em grafos",
  subject: (
    code: "ECM306",
    name: "Tópicos Avançados em Estrutura de Dados",
  ),
  authors: (
    (name: "Amanda Bialer Ingham", collegeId: "24.01621-7"),
    (name: "Ruy Diniz Monteiro", collegeId: "24.00846-0"),
  ),
  submission-date: datetime(year: 2026, month: 11, day: 12),
)

// Vírgula decimal em modo matemático, sem espaço depois dela.
#show math.equation: it => {
  show ",": math.class("normal", ",")
  it
}
#set math.equation(numbering: "(1)")

// Figuras e tabelas no padrão ABNT: título acima, fonte abaixo.
#set figure.caption(position: top)
#show figure.caption: it => block(
  width: 100%,
  below: 4mm,
  align(center, text(size: 10pt)[#it.supplement #context it.counter.display(it.numbering)#" - "#it.body]),
)
#set figure(placement: auto)
#set place(clearance: 9mm)
#show table: set par(justify: false)
#show table: set text(size: 9.5pt)
#show raw.where(block: true): set text(size: 8pt)
#show raw.where(block: true): set par(justify: false, leading: 5pt)

#include "chapters/introducao.typ"
#pagebreak(weak: true)

#include "chapters/grafos.typ"
#pagebreak(weak: true)

#include "chapters/modelagem.typ"
#pagebreak(weak: true)

#include "chapters/implementacao.typ"
#pagebreak(weak: true)

#include "chapters/resultados.typ"
#pagebreak(weak: true)

#include "chapters/conclusao.typ"
#pagebreak(weak: true)

#bibliography("bibliography.bib", title: "Referências", style: "associacao-brasileira-de-normas-tecnicas")
