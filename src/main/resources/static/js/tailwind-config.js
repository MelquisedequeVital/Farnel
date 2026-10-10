// Tokens do design system "Editorial Gastronomy" (docs/wireframes/.../editorial_gastronomy/DESIGN.md)
tailwind.config = {
	darkMode: "class",
	theme: {
		extend: {
			colors: {
				"on-tertiary-fixed": "#2b160f", "tertiary-container": "#85675c", "on-background": "#1c1b1b",
				"on-tertiary-container": "#ffeee8", "primary-container": "#2e7d32", "on-error": "#ffffff",
				"inverse-primary": "#88d982", "on-tertiary-fixed-variant": "#5b4137", "tertiary-fixed": "#ffdbce",
				"tertiary-fixed-dim": "#e4beb2", "surface-variant": "#e5e2e1", "on-error-container": "#93000a",
				"outline-variant": "#bfcaba", "on-primary-fixed-variant": "#005312", "secondary-fixed-dim": "#ffb68f",
				"error-container": "#ffdad6", "surface-container-high": "#eae7e7", "surface": "#fcf9f8",
				"surface-container-lowest": "#ffffff", "surface-bright": "#fcf9f8", "error": "#ba1a1a",
				"tertiary": "#6b4f45", "surface-tint": "#1b6d24", "inverse-surface": "#313030", "outline": "#707a6c",
				"primary": "#0d631b", "on-tertiary": "#ffffff", "on-secondary": "#ffffff", "on-primary-fixed": "#002204",
				"on-secondary-fixed-variant": "#773200", "surface-container-low": "#f6f3f2", "secondary-fixed": "#ffdbca",
				"on-primary": "#ffffff", "surface-container-highest": "#e5e2e1", "on-primary-container": "#cbffc2",
				"on-surface": "#1c1b1b", "primary-fixed-dim": "#88d982", "primary-fixed": "#a3f69c",
				"secondary-container": "#fd7613", "on-surface-variant": "#40493d", "on-secondary-fixed": "#331200",
				"surface-container": "#f0eded", "inverse-on-surface": "#f3f0ef", "surface-dim": "#dcd9d9",
				"on-secondary-container": "#5b2500", "background": "#fcf9f8", "secondary": "#9c4400"
			},
			borderRadius: { DEFAULT: "0.25rem", lg: "0.5rem", xl: "0.75rem", full: "9999px" },
			spacing: {
				"space-xl": "2.5rem", "space-xs": "0.25rem", "space-sm": "0.5rem", "space-2xl": "4rem",
				"margin-mobile": "1.5rem", "gutter-compact": "1rem", "space-md": "1rem", "gutter": "2rem",
				"space-lg": "1.5rem", "margin": "4rem", "space-3xl": "6rem"
			},
			fontFamily: {
				"headline-lg-mobile": ["Newsreader"], "display-md": ["Newsreader"], "label-md": ["Hanken Grotesk"],
				"body-sm": ["Hanken Grotesk"], "headline-sm": ["Newsreader"], "body-md": ["Hanken Grotesk"],
				"label-lg": ["Hanken Grotesk"], "headline-lg": ["Newsreader"], "label-sm": ["Hanken Grotesk"],
				"headline-md": ["Newsreader"], "body-lg": ["Hanken Grotesk"], "display-lg": ["Newsreader"]
			},
			fontSize: {
				"headline-lg-mobile": ["28px", { lineHeight: "36px", letterSpacing: "-0.01em", fontWeight: "400" }],
				"display-md": ["44px", { lineHeight: "52px", letterSpacing: "-0.015em", fontWeight: "400" }],
				"label-md": ["11px", { lineHeight: "16px", letterSpacing: "0.1em", fontWeight: "600" }],
				"body-sm": ["13px", { lineHeight: "20px", fontWeight: "400" }],
				"headline-sm": ["20px", { lineHeight: "28px", fontWeight: "500" }],
				"body-md": ["15px", { lineHeight: "24px", fontWeight: "400" }],
				"label-lg": ["13px", { lineHeight: "18px", letterSpacing: "0.08em", fontWeight: "600" }],
				"headline-lg": ["36px", { lineHeight: "44px", letterSpacing: "-0.01em", fontWeight: "400" }],
				"label-sm": ["10px", { lineHeight: "14px", letterSpacing: "0.06em", fontWeight: "500" }],
				"headline-md": ["26px", { lineHeight: "34px", fontWeight: "400" }],
				"body-lg": ["18px", { lineHeight: "28px", fontWeight: "400" }],
				"display-lg": ["56px", { lineHeight: "64px", letterSpacing: "-0.02em", fontWeight: "400" }]
			}
		}
	}
};
