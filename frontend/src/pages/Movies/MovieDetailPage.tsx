// src/pages/Movies/MovieDetailPage.tsx
import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { getMovieById } from '../../api/movie';
import { theme } from '../../styles/theme';
import PageWrapper from '../../components/layout/PageWrapper';
import SkeletonCard from '../../components/common/SkeletonCard';
import ErrorCard from '../../components/common/ErrorCard';

interface PersonDTO {
    personId: string;
    name: string;
    age: number;
}

interface MovieCastDTO {
    person: PersonDTO;
}

interface MovieResponseDTO {
    movieId: string;
    title: string;
    genres: string[];
    runtime: number;
    language: string;
    CBFC: string;
    cast: MovieCastDTO[];
}

const MovieDetailPage: React.FC = () => {

    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [movie, setMovie] = useState<MovieResponseDTO | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    const fetchMovie = async () => {
        if (!id) return;
        setLoading(true);
        setError(null);
        try {
            const response = await getMovieById(id);
            setMovie(response.data ?? response);
        } catch {
            setError('Could not load movie details. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchMovie();
    }, [id]);

    if (loading) {
        return (
            <PageWrapper>
                <SkeletonCard height="280px" />
                <div style={{ marginTop: theme.spacing.xl }}>
                    <SkeletonCard height="120px" />
                </div>
            </PageWrapper>
        );
    }

    if (error) {
        return (
            <PageWrapper>
                <ErrorCard message={error} onRetry={fetchMovie} />
            </PageWrapper>
        );
    }

    if (!movie) {
        return (
            <PageWrapper>
                <ErrorCard message="Movie not found." />
            </PageWrapper>
        );
    }

    return (
        <PageWrapper>
            <HeroSection>
                <HeroBg />
                <HeroContent>
                    <HeroInitial>{movie.title.charAt(0)}</HeroInitial>
                    <HeroMeta>
                        <HeroTitle>{movie.title}</HeroTitle>
                        <GenreRow>
                            {movie.genres.map((g) => (
                                <GenreChip key={g}>{g}</GenreChip>
                            ))}
                        </GenreRow>
                        <InfoRow>
                            <InfoItem><InfoLabel>Language</InfoLabel>{movie.language}</InfoItem>
                            <InfoItem><InfoLabel>Runtime</InfoLabel>{movie.runtime} min</InfoItem>
                            <InfoItem><InfoLabel>Rating</InfoLabel><CbfcBadge>{movie.CBFC}</CbfcBadge></InfoItem>
                        </InfoRow>
                        <BookButton
                            id={`book-tickets-${movie.movieId}`}
                            onClick={() => navigate(`/shows?movieId=${movie.movieId}`)}
                        >
                            Book Tickets →
                        </BookButton>
                    </HeroMeta>
                </HeroContent>
            </HeroSection>

            {movie.cast && movie.cast.length > 0 && (
                <CastSection>
                    <SectionTitle>Cast</SectionTitle>
                    <CastList>
                        {movie.cast.map((cm, i) => (
                            <CastChip key={i}>{cm.person.name}</CastChip>
                        ))}
                    </CastList>
                </CastSection>
            )}
        </PageWrapper>
    );

};

const HeroSection = styled.div`
  position: relative;
  border-radius: ${theme.radius.card};
  overflow: hidden;
  background: ${theme.colors.surfaceElevated};
  border: 1px solid ${theme.colors.surfaceBorder};
  margin-bottom: ${theme.spacing.xxl};
`;

const HeroBg = styled.div`
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1a0a0e 0%, #2d0f18 60%, transparent 100%);
  z-index: 0;
`;

const HeroContent = styled.div`
  position: relative;
  z-index: 1;
  display: flex;
  gap: ${theme.spacing.xxl};
  padding: ${theme.spacing.xxl};
  align-items: flex-start;
`;

const HeroInitial = styled.div`
  min-width: 120px;
  height: 160px;
  background: rgba(248, 68, 100, 0.1);
  border: 1px solid rgba(248, 68, 100, 0.3);
  border-radius: ${theme.radius.card};
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 4rem;
  font-weight: ${theme.font.weight.bold};
  color: ${theme.colors.accent};
`;

const HeroMeta = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${theme.spacing.lg};
`;

const HeroTitle = styled.h1`
  font-size: ${theme.font.size.display};
  font-weight: ${theme.font.weight.bold};
  color: ${theme.colors.textPrimary};
  line-height: 1.1;
`;

const GenreRow = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${theme.spacing.sm};
`;

const GenreChip = styled.span`
  font-size: ${theme.font.size.sm};
  font-weight: ${theme.font.weight.medium};
  background: rgba(248, 68, 100, 0.12);
  color: ${theme.colors.accent};
  border: 1px solid rgba(248, 68, 100, 0.25);
  border-radius: ${theme.radius.full};
  padding: 4px 14px;
`;

const InfoRow = styled.div`
  display: flex;
  gap: ${theme.spacing.xl};
  flex-wrap: wrap;
`;

const InfoItem = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: ${theme.font.size.lg};
  font-weight: ${theme.font.weight.semibold};
  color: ${theme.colors.textPrimary};
`;

const InfoLabel = styled.span`
  font-size: ${theme.font.size.xs};
  font-weight: ${theme.font.weight.medium};
  color: ${theme.colors.textMuted};
  text-transform: uppercase;
  letter-spacing: 1px;
`;

const CbfcBadge = styled.span`
  font-size: ${theme.font.size.sm};
  font-weight: ${theme.font.weight.bold};
  background: ${theme.colors.warning};
  color: #000;
  border-radius: ${theme.radius.badge};
  padding: 2px 10px;
`;

const BookButton = styled.button`
  align-self: flex-start;
  margin-top: ${theme.spacing.sm};
  padding: 12px 32px;
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

const CastSection = styled.section`
  background: ${theme.colors.surfaceElevated};
  border: 1px solid ${theme.colors.surfaceBorder};
  border-radius: ${theme.radius.card};
  padding: ${theme.spacing.xxl};
`;

const SectionTitle = styled.h2`
  font-size: ${theme.font.size.xxl};
  font-weight: ${theme.font.weight.semibold};
  color: ${theme.colors.textPrimary};
  margin-bottom: ${theme.spacing.lg};
`;

const CastList = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${theme.spacing.sm};
`;

const CastChip = styled.span`
  font-size: ${theme.font.size.sm};
  color: ${theme.colors.textSecondary};
  background: ${theme.colors.surface};
  border: 1px solid ${theme.colors.surfaceBorder};
  border-radius: ${theme.radius.full};
  padding: 6px 16px;
`;

export default MovieDetailPage;
