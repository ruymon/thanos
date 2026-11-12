#import "utils.typ": *

#let pt-months = (
  "janeiro", "fevereiro", "março", "abril", "maio", "junho",
  "julho", "agosto", "setembro", "outubro", "novembro", "dezembro",
)

#let fmt-date-pt(date) = {
  let day = date.display("[day padding:none]")
  let month = pt-months.at(int(date.display("[month padding:none]")) - 1)
  let year = date.display("[year]")
  day + " de " + month + " de " + year
}

#let cover_page(
  logo: "assets/logo-imt-black.svg",
  subject: (),
  title: "",
  authors: (),
  submission-date: none,
  language: "pt",
) = {
    set page(
      margin: (left: 0mm, right: 0mm, top: 0mm, bottom: 0mm),
      numbering: none,
      number-align: center,
    )

    if logo != none {
      place(
        top + right,
        dx: -20mm,
        dy: 20mm,
        image("assets/logo-imt-black.svg", width: 164pt)
      )
    }

  pad(
    left: 64mm,
    top: 124mm,
    right: 20mm,
    stack(

      block(
        fill: black,
        inset: 0.4em,
        stack(
          dir: ltr,
          spacing: 2mm,
          text(subject.code + " | " + subject.name, fill: white, size: 12pt, weight: "black"),
        ),
      ),
     
      v(8mm),
      // Title
      par(leading: 10pt, text(title, size: 31pt, weight: 500)),
      v(8mm),

      line(start: (0pt, 0pt), length: 48pt, stroke: 1mm),
      v(12mm),
            
      grid(
        columns: (1fr,) * calc.min(3, authors.len()), gutter: 6mm,..authors.map(author => stack(spacing: 2mm,
        text(formatName(author.name), size: 12pt, weight: "bold"),
        text(author.collegeId, size: 12pt, fill: luma(70))))
      )
    )
  )

  place(
    center + bottom,
    dy: -20mm,
    box(
      align(
        center,
        text(fmt-date-pt(submission-date), size: 10pt),
      )
    )
  )

}
