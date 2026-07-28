// src/styles/GlobalStyle.ts
import { createGlobalStyle } from 'styled-components';
import { theme } from './theme';

export const GlobalStyle = createGlobalStyle`

  @import url('https://fonts.googleapis.com/css2?family=Outfit:wght@400;500;600;700&display=swap');

  *, *::before, *::after {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
  }

  html {
    font-size: 16px;
    scroll-behavior: smooth;
  }

  body {
    font-family: ${theme.font.family};
    font-size: ${theme.font.size.md};
    font-weight: ${theme.font.weight.regular};
    background-color: ${theme.colors.base};
    color: ${theme.colors.textPrimary};
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    line-height: 1.6;
    min-height: 100vh;
  }

  #root {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }

  a {
    color: ${theme.colors.accent};
    text-decoration: none;
    transition: color ${theme.transition.fast};

    &:hover {
      color: ${theme.colors.accentHover};
    }
  }

  button {
    font-family: ${theme.font.family};
    cursor: pointer;
    border: none;
    background: none;
  }

  input, select, textarea {
    font-family: ${theme.font.family};
    font-size: ${theme.font.size.md};
  }

  ul, ol {
    list-style: none;
  }

  img {
    max-width: 100%;
    display: block;
  }

  /* Styled scrollbar for supporting browsers */
  ::-webkit-scrollbar {
    width: 6px;
    height: 6px;
  }

  ::-webkit-scrollbar-track {
    background: ${theme.colors.surface};
  }

  ::-webkit-scrollbar-thumb {
    background: ${theme.colors.accent};
    border-radius: ${theme.radius.full};
  }

  scrollbar-width: thin;
  scrollbar-color: ${theme.colors.accent} ${theme.colors.surface};

  /* Shimmer animation for skeleton loaders */
  @keyframes shimmer {
    0% { background-position: -400px 0; }
    100% { background-position: 400px 0; }
  }

  @keyframes fadeIn {
    from { opacity: 0; transform: translateY(8px); }
    to   { opacity: 1; transform: translateY(0); }
  }

  @keyframes pulse {
    0%, 100% { opacity: 1; }
    50%       { opacity: 0.6; }
  }
`;
