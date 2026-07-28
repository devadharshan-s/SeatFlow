// src/components/layout/PageWrapper.tsx
import React, { ReactNode } from 'react';
import styled from 'styled-components';
import { theme } from '../../styles/theme';
import Navbar from './Navbar';

interface PageWrapperProps {
    children: ReactNode;
    fullWidth?: boolean;
}

const PageWrapper: React.FC<PageWrapperProps> = ({ children, fullWidth = false }) => {

    return (
        <>
            <Navbar />
            <Main>
                <Inner $fullWidth={fullWidth}>{children}</Inner>
            </Main>
        </>
    );

};

const Main = styled.main`
  flex: 1;
  padding-top: 60px; /* Navbar height offset */
`;

const Inner = styled.div<{ $fullWidth: boolean }>`
  max-width: ${({ $fullWidth }) => ($fullWidth ? '100%' : '1280px')};
  margin: 0 auto;
  padding: ${theme.spacing.xxl} ${theme.spacing.xl};
  animation: fadeIn 250ms ease both;
`;

export default PageWrapper;
