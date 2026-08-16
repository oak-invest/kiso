# Kiso logo system

Generated SVG assets for Kiso.

## Palette

- Kiso Blue: `#3157F6`
- Ink: `#071120`
- Dark background: `#050A12`
- White: `#FFFFFF`
- Black: `#000000`

## Typography

Recommended font: Sora.
Fallback stack used in SVG: `Sora, Inter, Avenir Next, Arial, sans-serif`.

## Naming

- `kiso_logo_horizontal_*`
- `kiso_logo_vertical_*`
- `kiso_logomark_*`
- `kiso_wordmark_*`
- `kiso_favicon_*`

Variants:
- `light`: white/transparent usage with dark text and blue mark
- `dark`: dark background with white text and blue mark
- `black`: monochrome black
- `white`: monochrome white on a dark preview background

Note: the `white` SVG files include a dark background, so they are visible when opened directly. If you need transparent white-only assets, remove the first `<rect>` background.
