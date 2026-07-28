// src/pages/HomePage.tsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { theme } from '../styles/theme';
import PageWrapper from '../components/layout/PageWrapper';

const HomePage: React.FC = () => {

    const navigate = useNavigate();

    return (
        <PageWrapper>
            <Hero>
                <HeroEyebrow>India's favourite ticketing platform</HeroEyebrow>
                <HeroTitle>
                    Book your next <Accent>unforgettable</Accent> experience
                </HeroTitle>
                <HeroSubtitle>
                    Movies, shows, and live events — all in one place. Secure seats in seconds.
                </HeroSubtitle>
                <HeroActions>
                    <PrimaryButton id="hero-browse-movies" onClick={() => navigate('/movies')}>
                        Browse Movies
                    </PrimaryButton>
                    <SecondaryButton id="hero-view-shows" onClick={() => navigate('/shows')}>
                        View Shows
                    </SecondaryButton>
                </HeroActions>
            </Hero>

            <FeaturesGrid>
                <FeatureCard>
                    <FeatureIcon>🎬</FeatureIcon>
                    <FeatureTitle>Latest Movies</FeatureTitle>
                    <FeatureDesc>Browse new releases and coming soon titles across all genres.</FeatureDesc>
                </FeatureCard>
                <FeatureCard>
                    <FeatureIcon>⚡</FeatureIcon>
                    <FeatureTitle>Instant Seat Lock</FeatureTitle>
                    <FeatureDesc>Redis-powered distributed locking ensures your seats are held the moment you pick them.</FeatureDesc>
                </FeatureCard>
                <FeatureCard>
                    <FeatureIcon>🔒</FeatureIcon>
                    <FeatureTitle>Safe & Secure</FeatureTitle>
                    <FeatureDesc>Payments processed securely. Idempotent bookings mean no double charges, ever.</FeatureDesc>
                </FeatureCard>
            </FeaturesGrid>
        </PageWrapper>
    );

};

const Hero = styled.section`
  text-align: center;
  padding: ${theme.spacing.xxxl} 0;
  max-width: 760px;
  margin: 0 auto;
  animation: fadeIn 300ms ease both;
`;

const HeroEyebrow = styled.p`
  font-size: ${theme.font.size.sm};
  font-weight: ${theme.font.weight.semibold};
  text-transform: uppercase;
  letter-spacing: 2px;
  color: ${theme.colors.accent};
  margin-bottom: ${theme.spacing.lg};
`;

const HeroTitle = styled.h1`
  font-size: clamp(2rem, 5vw, 3.2rem);
  font-weight: ${theme.font.weight.bold};
  line-height: 1.15;
  color: ${theme.colors.textPrimary};
  margin-bottom: ${theme.spacing.xl};
`;

const Accent = styled.span`
  color: ${theme.colors.accent};
`;

const HeroSubtitle = styled.p`
  font-size: ${theme.font.size.lg};
  color: ${theme.colors.textSecondary};
  max-width: 520px;
  margin: 0 auto ${theme.spacing.xxl};
  line-height: 1.7;
`;

const HeroActions = styled.div`
  display: flex;
  gap: ${theme.spacing.lg};
  justify-content: center;
  flex-wrap: wrap;
`;

const PrimaryButton = styled.button`
  padding: 14px 36px;
  background: ${theme.colors.accent};
  color: ${theme.colors.textPrimary};
  font-size: ${theme.font.size.lg};
  font-weight: ${theme.font.weight.semibold};
  border-radius: ${theme.radius.button};
  transition: background ${theme.transition.fast}, box-shadow ${theme.transition.fast}, transform ${theme.transition.fast};

  &:hover {
    background: ${theme.colors.accentHover};
    box-shadow: ${theme.shadow.button};
    transform: translateY(-2px);
  }

  &:focus-visible {
    outline: 2px solid ${theme.colors.accent};
    outline-offset: 3px;
  }
`;

const SecondaryButton = styled.button`
  padding: 14px 36px;
  background: transparent;
  color: ${theme.colors.textPrimary};
  font-size: ${theme.font.size.lg};
  font-weight: ${theme.font.weight.medium};
  border: 1px solid ${theme.colors.surfaceBorder};
  border-radius: ${theme.radius.button};
  transition: border-color ${theme.transition.fast}, color ${theme.transition.fast};

  &:hover {
    border-color: ${theme.colors.accent};
    color: ${theme.colors.accent};
  }

  &:focus-visible {
    outline: 2px solid ${theme.colors.accent};
    outline-offset: 3px;
  }
`;

const FeaturesGrid = styled.section`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: ${theme.spacing.xl};
  margin-top: ${theme.spacing.xxxl};
`;

const FeatureCard = styled.div`
  background: ${theme.colors.surfaceElevated};
  border: 1px solid ${theme.colors.surfaceBorder};
  border-radius: ${theme.radius.card};
  padding: ${theme.spacing.xxl};
  transition: border-color ${theme.transition.base}, box-shadow ${theme.transition.base};

  &:hover {
    border-color: ${theme.colors.accent};
    box-shadow: ${theme.shadow.card};
  }
`;

const FeatureIcon = styled.div`
  font-size: 2rem;
  margin-bottom: ${theme.spacing.lg};
`;

const FeatureTitle = styled.h3`
  font-size: ${theme.font.size.xl};
  font-weight: ${theme.font.weight.semibold};
  color: ${theme.colors.textPrimary};
  margin-bottom: ${theme.spacing.sm};
`;

const FeatureDesc = styled.p`
  font-size: ${theme.font.size.md};
  color: ${theme.colors.textSecondary};
  line-height: 1.6;
`;

export default HomePage;
