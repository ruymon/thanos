#import "/cover.typ": cover_page

#let project(
  logo: "assets/logo-imt-black.svg",
  subject: (),
  title: "",
  authors: (),
  submission-date: none,
  language: "pt",
  body,
) = {
  let THESIS_HEADING_EXTRA_TOP_MARGIN = 70pt
  let PAGE_MARGIN_TOP = 37mm
  
  set document(
    author: authors.map(a => a.name), 
    title: title,
    date: submission-date
  )
  
  set page(
    margin: (left: 31.5mm, right: 31.5mm, top: PAGE_MARGIN_TOP, bottom: 56mm),
    numbering: "1",
    number-align: right,
    binding: left,
    header-ascent: 24pt,
    header: context {
      // Before
      let selector_before = selector(heading.where(level: 1)).before(here())
      let level_before = int(counter(selector_before).display())
      let headings_before = query(selector_before)

      if headings_before.len() == 0 {
        return
      }

      // After
      let selector_after = selector(heading.where(level: 1)).after(here())
      let level_after = level_before + 1
      let headings_after = query(selector_after)

      if headings_after.len() == 0 {
        return
      }

      // Get headings
      let heading_before = headings_before.last()
      let heading_after = headings_after.first()

      // Decide on heading
      let heading = heading_before
      let level = level_before

      if heading_after.location().page() == here().page() {
        if heading_after.location().position().y == (THESIS_HEADING_EXTRA_TOP_MARGIN + PAGE_MARGIN_TOP) or heading_after.location().position().y == PAGE_MARGIN_TOP {
          // Next header is first element of page
          return
        } else {
          heading = heading_after
          level = level_after
        }
      }

      set text(size: 11.5pt)
      grid(
        rows: 2,
        gutter: 5pt,
        if heading.numbering != none {
          emph(str(level) + " " + heading.body)
        } else {
          emph(heading.body)
        },
        line(length: 100%, stroke: 0.7pt),
      )
    }
  )
  set par(leading: 9pt)
  set text(font: "Libertinus Serif", lang: language, size: 10.85pt)
  set heading(
    numbering: "1.1", 
  )

  /**
  * Configure Headings
  */
  
  // Base sizes
  let font_size = 10pt
  let top_margin = 0pt  
  let bottom_margin = 0pt


  /**
  * Configure H1
  */
  let font_size = 21pt
  let top_margin = 25pt
  let bottom_margin = 45pt
  
  show heading.where(level: 1): set block(above: top_margin, below: bottom_margin)
  show heading.where(level: 1): set text(size: font_size, weight: 600)
  show heading.where(level: 1): it => it

  
  /**
  * Configure H2
  */
  font_size = 13pt
  top_margin = 30pt
  bottom_margin = 25pt
  show heading.where(level: 2): set block(above: top_margin, below: bottom_margin)
  show heading.where(level: 2): set text(size: font_size)

  /**
  * Configure H3
  */
  font_size = 11pt
  top_margin = 20pt
  bottom_margin = 15pt

  show heading.where(level: 3): set block(above: top_margin, below: bottom_margin)
  show heading.where(level: 3): set text(size: font_size)
  

  cover_page(
    title: title,
    authors: authors,
    logo: logo,
    subject: subject,
    submission-date: submission-date,
    language: language,
  )

  // Reset page numbering and set it to numbers
  set page(
    numbering: "1",
  )
  counter(page).update(1)

  // Main body.
  set par(
    first-line-indent: 8mm,
    spacing: 8mm,
    justify: true,
  )

  // Table of contents.
  outline(depth: 3, indent: auto)
  pagebreak()

  body
}