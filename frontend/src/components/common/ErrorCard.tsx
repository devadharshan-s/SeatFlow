// src/components/common/ErrorCard.tsx
import React from 'react';
import styled from 'styled-components';
import { theme } from '../../styles/theme';

interface ErrorCardProps {
    message: string;
    onRetry?: () => void;
}

const ErrorCard: React.FC<ErrorCardProps> = ({ message, onRetry }) => {

    return (
        <Wrapper>
            <Icon>⚠</Icon>
            <Title>Something went wrong</Title>
            <Message>{message}</Message>
            {onRetry && <RetryButton id="error-retry-btn" onClick={onRetry}>Try Again</RetryButton>}
        </Wrapper>
    );

};

const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: ${theme.spacing.lg};
  padding: ${theme.spacing.xxxl};
  background: ${theme.colors.surfaceElevated};
  border: 1px solid ${theme.colors.error};
  border-radius: ${theme.radius.card};
  max-width: 480px;
  margin: ${theme.spacing.xxxl} auto;
  text-align: center;
  animation: fadeIn 250ms ease both;
`;

const Icon = styled.span`
  font-size: 2.5rem;
  color: ${theme.colors.error};
`;

const Title = styled.h2`
  font-size: ${theme.font.size.xl};
  font-weight: ${theme.font.weight.semibold};
  color: ${theme.colors.textPrimary};
`;

const Message = styled.p`
  font-size: ${theme.font.size.md};
  color: ${theme.colors.textSecondary};
  line-height: 1.5;
`;

const RetryButton = styled.button`
  margin-top: ${theme.spacing.sm};
  padding: 10px 28px;
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
    outline-offset: 2px;
  }
`;

export default ErrorCard;
