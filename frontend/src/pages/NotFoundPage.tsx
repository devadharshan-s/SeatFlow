// src/pages/NotFoundPage.tsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { theme } from '../styles/theme';

const NotFoundPage: React.FC = () => {

    const navigate = useNavigate();

    return (
        <PageBg>
            <Content>
                <ErrorCode>404</ErrorCode>
                <Title>Page not found</Title>
                <Subtitle>Looks like this seat doesn't exist.</Subtitle>
                <HomeBtn id="go-home-404" onClick={() => navigate('/')}>
                    Back to Home
                </HomeBtn>
            </Content>
        </PageBg>
    );

};

const PageBg = styled.div`
  min-height: 100vh;
  background: ${theme.colors.base};
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: ${theme.spacing.xl};
`;

const Content = styled.div`
  animation: fadeIn 300ms ease both;
`;

const ErrorCode = styled.div`
  font-size: clamp(6rem, 15vw, 10rem);
  font-weight: ${theme.font.weight.bold};
  color: ${theme.colors.accent};
  line-height: 1;
  opacity: 0.15;
  margin-bottom: ${theme.spacing.xl};
`;

const Title = styled.h1`
  font-size: ${theme.font.size.xxxl};
  font-weight: ${theme.font.weight.bold};
  color: ${theme.colors.textPrimary};
  margin-bottom: ${theme.spacing.sm};
`;

const Subtitle = styled.p`
  font-size: ${theme.font.size.lg};
  color: ${theme.colors.textSecondary};
  margin-bottom: ${theme.spacing.xxl};
`;

const HomeBtn = styled.button`
  padding: 12px 32px;
  background: ${theme.colors.accent};
  color: ${theme.colors.textPrimary};
  font-size: ${theme.font.size.md};
  font-weight: ${theme.font.weight.semibold};
  border-radius: ${theme.radius.button};
  transition: background ${theme.transition.fast}, box-shadow ${theme.transition.fast};

  &:hover {
    background: ${theme.colors.accentHover};
    box-shadow: ${theme.shadow.button};
  }

  &:focus-visible {
    outline: 2px solid ${theme.colors.accent};
    outline-offset: 3px;
  }
`;

export default NotFoundPage;
