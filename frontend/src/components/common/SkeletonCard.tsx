// src/components/common/SkeletonCard.tsx
import React from 'react';
import styled, { keyframes } from 'styled-components';
import { theme } from '../../styles/theme';

interface SkeletonCardProps {
    height?: string;
    width?: string;
}

const SkeletonCard: React.FC<SkeletonCardProps> = ({ height = '200px', width = '100%' }) => {

    return <Skeleton $height={height} $width={width} />;

};

const shimmer = keyframes`
  0%   { background-position: -400px 0; }
  100% { background-position:  400px 0; }
`;

const Skeleton = styled.div<{ $height: string; $width: string }>`
  height: ${({ $height }) => $height};
  width: ${({ $width }) => $width};
  border-radius: ${theme.radius.card};
  background: linear-gradient(
    90deg,
    ${theme.colors.surfaceElevated} 25%,
    #2a2a2a 50%,
    ${theme.colors.surfaceElevated} 75%
  );
  background-size: 800px 100%;
  animation: ${shimmer} 1.4s infinite linear;
`;

export default SkeletonCard;
