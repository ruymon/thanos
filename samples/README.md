# Samples

One folder per photo. Each holds the same three files:

| File | What it is |
|---|---|
| `photo.*` | The original photo |
| `scribbles.png` | The hints: red over the subject, blue over the background |
| `cutout.png` | What Thanos produces from the two, with the default parameters |

Every folder has its own `README.md` with the command ready to copy. For example:

```bash
./thanos cut samples/thanos/photo.jpg --scribbles samples/thanos/scribbles.png --out thanos.png
```

| Folder | Photo | Why it is here | Source |
|---|---|---|---|
| `astronaut/` | Portrait of astronaut Eileen Collins | A person against a busy background with similar colours | NASA, public domain (via scikit-image) |
| `coffee/` | Espresso cup on a wooden table | An object made of several parts: cup, saucer, spoon | Pikolo Espresso Bar, CC0 (via scikit-image) |
| `dog/` | Black puppy on a wooden floor | Strong contrast: cuts cleanly with few scribbles | André Spieker, Unsplash License (via Lorem Picsum) |
| `pug/` | Pug wrapped in a blanket in the woods | A light subject against dark, textured foliage | Matthew Wiebe, Unsplash License (via Lorem Picsum) |
| `thanos/` | Thanos cosplay in front of a blue backdrop | The project's namesake, with blue armour against a blue background | Super Festivals, CC BY 2.0 (via Wikimedia Commons) |

The Wikimedia Commons photo was resized to at most 1000 px on the longer side.
