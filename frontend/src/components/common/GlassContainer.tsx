// src/components/common/GlassContainer.tsx
// Replaced: now a plain solid surface card for backward-compat imports.
// GlassContainer is kept so existing pages that still import it don't break
// before they're individually restyled.
import React, { ReactNode, CSSProperties } from 'react';
import styled from 'styled-components';
import { theme } from '../../styles/theme';

interface GlassContainerProps {
    children: ReactNode;
    style?: CSSProperties;
}

const GlassContainer: React.FC<GlassContainerProps> = ({ children, style }) => (
    <Wrapper style={style}>{children}</Wrapper>
);

const Wrapper = styled.div`
  background: ${theme.colors.surfaceElevated};
  border: 1px solid ${theme.colors.surfaceBorder};
  border-radius: ${theme.radius.card};
  padding: ${theme.spacing.xxl};
  max-width: 960px;
  margin: ${theme.spacing.xxl} auto;
  color: ${theme.colors.textPrimary};
`;

export { GlassContainer };
