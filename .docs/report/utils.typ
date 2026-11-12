#let formatName(name) = {
  let words = name.split(" ")

  if(words.len() < 2){
    return name
  }
  
  let firstName = words.remove(0)
  let lastName = words.pop()

  let condesedSurnames = ()
  
  for surname in words {
    condesedSurnames.push(surname.first()+".")
  }

  let formatedName = (firstName, condesedSurnames, lastName).flatten().join(" ")
  
  return formatedName
  
}

#let boxedEquation(..args) = {
  set align(center)
  show math.equation: it => box(it, stroke: black, inset: 4mm)

  text(..args)
}