// src/styles/theme.ts

export const theme = {

    colors: {
        // Surfaces
        base: '#0d0d0d',
        surface: '#111111',
        surfaceElevated: '#1c1c1e',
        surfaceBorder: '#2a2a2a',

        // Accent
        accent: '#f84464',
        accentDeep: '#e50914',
        accentHover: '#ff2d55',

        // Text
        textPrimary: '#ffffff',
        textSecondary: '#a0a0a0',
        textMuted: '#6b6b6b',

        // Seat states — all solid, zero transparency
        seatAvailableBg: '#1a3a2a',
        seatAvailableBorder: '#2ecc71',
        seatSelectedBg: '#3a0f1a',
        seatSelectedBorder: '#f84464',
        seatLockedBg: '#3a2a00',
        seatLockedBorder: '#f39c12',
        seatBookedBg: '#1a1a1a',
        seatBookedBorder: '#555555',

        // Status
        success: '#2ecc71',
        error: '#e50914',
        warning: '#f39c12',
    },

    spacing: {
        xs: '4px',
        sm: '8px',
        md: '12px',
        lg: '16px',
        xl: '24px',
        xxl: '32px',
        xxxl: '48px',
    },

    radius: {
        card: '12px',
        button: '8px',
        badge: '4px',
        seat: '6px',
        full: '9999px',
    },

    font: {
        family: "'Outfit', 'Inter', sans-serif",
        size: {
            xs: '0.75rem',
            sm: '0.875rem',
            md: '1rem',
            lg: '1.125rem',
            xl: '1.25rem',
            xxl: '1.5rem',
            xxxl: '2rem',
            display: '2.5rem',
        },
        weight: {
            regular: 400,
            medium: 500,
            semibold: 600,
            bold: 700,
        },
    },

    shadow: {
        card: '0 2px 12px rgba(0, 0, 0, 0.4)',
        cardHover: '0 8px 24px rgba(0, 0, 0, 0.6)',
        button: '0 2px 8px rgba(248, 68, 100, 0.25)',
    },

    transition: {
        fast: '120ms ease',
        base: '200ms ease',
        slow: '350ms ease',
    },

} as const;

export type Theme = typeof theme;
