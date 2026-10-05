---
name: Editorial Gastronomy
colors:
  surface: '#fcf9f8'
  surface-dim: '#dcd9d9'
  surface-bright: '#fcf9f8'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f3f2'
  surface-container: '#f0eded'
  surface-container-high: '#eae7e7'
  surface-container-highest: '#e5e2e1'
  on-surface: '#1c1b1b'
  on-surface-variant: '#40493d'
  inverse-surface: '#313030'
  inverse-on-surface: '#f3f0ef'
  outline: '#707a6c'
  outline-variant: '#bfcaba'
  surface-tint: '#1b6d24'
  primary: '#0d631b'
  on-primary: '#ffffff'
  primary-container: '#2e7d32'
  on-primary-container: '#cbffc2'
  inverse-primary: '#88d982'
  secondary: '#9c4400'
  on-secondary: '#ffffff'
  secondary-container: '#fd7613'
  on-secondary-container: '#5b2500'
  tertiary: '#6b4f45'
  on-tertiary: '#ffffff'
  tertiary-container: '#85675c'
  on-tertiary-container: '#ffeee8'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#a3f69c'
  primary-fixed-dim: '#88d982'
  on-primary-fixed: '#002204'
  on-primary-fixed-variant: '#005312'
  secondary-fixed: '#ffdbca'
  secondary-fixed-dim: '#ffb68f'
  on-secondary-fixed: '#331200'
  on-secondary-fixed-variant: '#773200'
  tertiary-fixed: '#ffdbce'
  tertiary-fixed-dim: '#e4beb2'
  on-tertiary-fixed: '#2b160f'
  on-tertiary-fixed-variant: '#5b4137'
  background: '#fcf9f8'
  on-background: '#1c1b1b'
  surface-variant: '#e5e2e1'
typography:
  display-lg:
    fontFamily: Newsreader
    fontSize: 56px
    fontWeight: '400'
    lineHeight: 64px
    letterSpacing: -0.02em
  display-md:
    fontFamily: Newsreader
    fontSize: 44px
    fontWeight: '400'
    lineHeight: 52px
    letterSpacing: -0.015em
  headline-lg:
    fontFamily: Newsreader
    fontSize: 36px
    fontWeight: '400'
    lineHeight: 44px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Newsreader
    fontSize: 28px
    fontWeight: '400'
    lineHeight: 36px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Newsreader
    fontSize: 26px
    fontWeight: '400'
    lineHeight: 34px
  headline-sm:
    fontFamily: Newsreader
    fontSize: 20px
    fontWeight: '500'
    lineHeight: 28px
  body-lg:
    fontFamily: Hanken Grotesk
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Hanken Grotesk
    fontSize: 15px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Hanken Grotesk
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Hanken Grotesk
    fontSize: 13px
    fontWeight: '600'
    lineHeight: 18px
    letterSpacing: 0.08em
  label-md:
    fontFamily: Hanken Grotesk
    fontSize: 11px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.1em
  label-sm:
    fontFamily: Hanken Grotesk
    fontSize: 10px
    fontWeight: '500'
    lineHeight: 14px
    letterSpacing: 0.06em
spacing:
  gutter: 2rem
  gutter-compact: 1rem
  margin: 4rem
  margin-mobile: 1.5rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2.5rem
  space-2xl: 4rem
  space-3xl: 6rem
---

## Brand & Style

The design system is rooted in the heritage and intentionality of high-end culinary editorial culture. Built for a curated subscription platform of wholesome, authentic meals, it steers deliberately away from generic tech clichés—rejecting rounded cards, floating ambient drop shadows, and multi-colored pill badges.

The visual tone reflects an artisanal culinary monograph:
- **Atmosphere:** Spacious, grounded, tranquil, and distinctly sensory.
- **Aesthetic Movement:** Modern Editorial Minimalism combined with structured, architectural grid lines. Layouts feel like oversized art books or architectural food journals rather than an e-commerce dashboard.
- **Visual Rhythm:** Full-bleed horizontal imagery, sharp linear grid divisions (0.5px to 1px hairline rules), strict typographic hierarchy, and expansive natural margins.

## Colors

The palette embraces organic provenance, earthy equilibrium, and culinary warmth. 

- **Primary (`#2E7D32` - Deep Culinary Green):** Evokes fresh harvest, botanical integrity, and culinary discipline. Used for primary interactive actions, key category anchors, and structural navigation accents.
- **Secondary (`#EF6C00` - Burnt Citrus):** Applied sparingly to stimulate appetite, guide the eye toward conversion moments (e.g., subscription activation, seasonal alerts), and highlight precise typographic tags.
- **Tertiary (`#8D6E63` - Raw Earth / Ochre Wash):** A secondary accent dedicated to provenance tags, grain details, and seasonal harvest notations.
- **Surfaces & Canvases:**
  - Base canvas: `#FBFBFA` (warm porcelain).
  - Layered surface / contrasting sections: `#F4F4F1` (pressed linen).
  - Hairline dividers: `#E2E2DC` (subtle bone gray).
- **Typography & Ink:**
  - Primary text: `#1A1A1A` (rich carbon black).
  - Secondary text: `#5A5A55` (deep muted slate).
  - Disabled or caption: `#8E8E88`.

## Typography

The pairing combines the literary gravitas of **Newsreader** (used for titles, editorial intros, and recipe dish names) with the crisp, modern legibility of **Hanken Grotesk** (applied across running body copy, nutritional facts, system controls, and meta information).

- **Headlines:** Use Newsreader in regular or medium weights. Italic variations are reserved strictly for provenance, season notes, or artisan names (e.g., *“Colheita de Outono da Mantiqueira”*).
- **Labels:** Rendered exclusively in uppercase Hanken Grotesk with expanded letter spacing (tracking between `0.06em` and `0.1em`) to simulate fine book publishing indexes.

## Layout & Spacing

The architecture prioritizes a desktop-first, widescreen reading experience:

- **Grid Framework:** A 12-column structural grid system bounded by generous outer margins (`4rem` on desktop). Column divisions are frequently marked by 1px vertical or horizontal borderlines rather than open gaps.
- **Rhythm & White Space:** Sections breathe using `space-2xl` and `space-3xl`, creating a deliberate gallery cadence.
- **Horizontal Carousels:** Dish collections, weekly menus, and farm origins flow off-edge in seamless horizontal tracks without scrollbars, punctuated by pinned index numbers and hairline progress indicators.

## Elevation & Depth

This design system is strictly flat and tactile, eliminating box shadows, blurred scrims, and floating cards.

- **Depth through Layering:** Contrast is achieved solely via tonal separation: placing `#FBFBFA` surfaces over `#F4F4F1` containers, or juxtaposing crisp photography directly against raw linen canvases.
- **Hairline Borders:** Hierarchy and containment rely on 1px solid boundaries (`#E2E2DC`). Hovering over actionable items turns the border from `#E2E2DC` to `#1A1A1A` with zero positional displacement or elevation changes.
- **Overlapping Content:** Text blocks occasionally overlap photographic margins by a fixed grid unit to reinforce editorial layout techniques common in print broadsheets.

## Shapes

The geometric identity is completely architectural, sharp, and unapologetic. 

- **Edge Radius:** 0px globally across all elements (buttons, inputs, image containers, dialogs, and dividers). 
- **Aspect Ratios:** Photography adheres to rigid proportions: 4:3 for dish portraits, 16:9 for landscape culinary processes, and 1:1 for ingredient spotlights.
- **Line Accents:** Single-pixel orthogonal strokes terminate cleanly at column edges.

## Components

### Buttons
- **Primary:** Flat block in `#2E7D32` with crisp white text (`Hanken Grotesk`, semi-bold, uppercase, letter-spaced). Height: 48px. Zero border radius, zero shadow. Hover state: transitions to `#1B5E20`.
- **Secondary:** Transparent background with a 1px solid `#1A1A1A` outline and dark text. Hover state: inverts to full `#1A1A1A` background with white text.
- **Text / Editorial Action:** Underlined text link with a 1px baseline gap and a subtle rightward directional arrow (`→`).

### Content Dividers & Cards
- **Meal & Recipe Presentation:** Not housed in traditional elevated cards. Instead, each meal is represented as a structured column module: a high-resolution, unrounded image on top, separated by a thin 1px horizontal line, followed by the dish name in `Newsreader`, dietary tags in plain typography, and pricing/subscription actions below.
- **Carousel Strips:** Continuous edge-to-edge horizontal scrolls separated by 1px vertical borders. No arrows in bubble badges; navigation uses raw editorial text controls (`[ ANTERIOR / PRÓXIMO ]`).

### Chips & Badges
- Replaced by text-only classifications or minimal boxed tags with a 1px border (`#E2E2DC`), transparent background, and small uppercase typography. No colorful pill fills or rounded corners.

### Form Inputs & Selectors
- **Text Fields:** Minimalist baseline-only or sharp rectangular boxes with 1px border (`#E2E2DC`). On focus: stroke becomes `#2E7D32` without glow or offset rings.
- **Checkboxes & Radios:** Sharp square indicators (0px radius). Checked state fills the box with solid `#2E7D32` containing a stark geometric check or interior square.

### Subscription & Plan Selectors
- Tabular, side-by-side editorial columns bordered by 1px lines. The active plan is marked by a top horizontal stripe in `#EF6C00` (2px) and an inverted high-contrast header block.