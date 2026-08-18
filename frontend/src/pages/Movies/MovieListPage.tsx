// src/pages/Movies/MovieListPage.tsx
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { getAllMovies } from '../../api/movie';
import { theme } from '../../styles/theme';
import PageWrapper from '../../components/layout/PageWrapper';
import SkeletonCard from '../../components/common/SkeletonCard';
import ErrorCard from '../../components/common/ErrorCard';

interface Movie {
    movieId: string;
    title: string;
    genres: string[];
    runtime: number;
    language: string;
    CBFC?: string;
}

const MovieListPage: React.FC = () => {

    const [movies, setMovies] = useState<Movie[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    const fetchMovies = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await getAllMovies();
            setMovies(response.data ?? response.content ?? response ?? []);
        } catch {
            setError('Could not load movies. Please check your connection and try again.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchMovies();
    }, []);

    return (
        <PageWrapper>
            <PageHeader>
                <PageTitle>Now Showing</PageTitle>
                <PageSubtitle>Pick a movie to browse available showtimes</PageSubtitle>
            </PageHeader>

            {loading && (
                <Grid>
                    {Array.from({ length: 6 }).map((_, i) => (
                        <SkeletonCard key={i} height="220px" />
                    ))}
                </Grid>
            )}

            {!loading && error && (
                <ErrorCard message={error} onRetry={fetchMovies} />
            )}

            {!loading && !error && movies.length === 0 && (
                <EmptyState>No movies found. Check back soon!</EmptyState>
            )}

            {!loading && !error && movies.length > 0 && (
                <Grid>
                    {movies.map((movie) => (
                        <MovieCard
                            key={movie.movieId}
                            id={`movie-card-${movie.movieId}`}
                            onClick={() => navigate(`/movies/${movie.movieId}`)}
                        >
                            <CardTop>
                                <MovieInitial>{movie.title.charAt(0)}</MovieInitial>
                            </CardTop>
                            <CardBody>
                                <MovieTitle>{movie.title}</MovieTitle>
                                <GenreTags>
                                    {(movie.genres || []).slice(0, 3).map((g) => (
                                        <GenreTag key={g}>{g}</GenreTag>
                                    ))}
                                </GenreTags>
                                <MetaRow>
                                    <MetaBadge>{movie.language}</MetaBadge>
                                    <MetaText>{movie.runtime} min</MetaText>
                                    {movie.CBFC && <CbfcBadge>{movie.CBFC}</CbfcBadge>}
                                </MetaRow>
                            </CardBody>
                            <CardFooter>
                                <BookBtn id={`book-btn-${movie.movieId}`}>Book Now →</BookBtn>
                            </CardFooter>
                        </MovieCard>
                    ))}
                </Grid>
            )}
        </PageWrapper>
    );

};

const PageHeader = styled.div`
  margin-bottom: ${theme.spacing.xxl};
`;

const PageTitle = styled.h1`
  font-size: ${theme.font.size.xxxl};
  font-weight: ${theme.font.weight.bold};
  color: ${theme.colors.textPrimary};
  margin-bottom: ${theme.spacing.sm};
`;

const PageSubtitle = styled.p`
  font-size: ${theme.font.size.md};
  color: ${theme.colors.textSecondary};
`;

const Grid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: ${theme.spacing.xl};
`;

const MovieCard = styled.div`
  background: ${theme.colors.surfaceElevated};
  border: 1px solid ${theme.colors.surfaceBorder};
  border-radius: ${theme.radius.card};
  overflow: hidden;
  cursor: pointer;
  transition: transform ${theme.transition.base}, border-color ${theme.transition.base}, box-shadow ${theme.transition.base};
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-4px);
    border-color: ${theme.colors.accent};
    box-shadow: ${theme.shadow.cardHover};
  }

  &:focus-visible {
    outline: 2px solid ${theme.colors.accent};
    outline-offset: 2px;
  }
`;

const CardTop = styled.div`
  background: linear-gradient(135deg, #1a0a0e, #2d0f18);
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid ${theme.colors.surfaceBorder};
`;

const MovieInitial = styled.span`
  font-size: 3rem;
  font-weight: ${theme.font.weight.bold};
  color: ${theme.colors.accent};
  opacity: 0.6;
`;

const CardBody = styled.div`
  padding: ${theme.spacing.lg};
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: ${theme.spacing.md};
`;

const MovieTitle = styled.h2`
  font-size: ${theme.font.size.lg};
  font-weight: ${theme.font.weight.semibold};
  color: ${theme.colors.textPrimary};
  line-height: 1.3;
`;

const GenreTags = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${theme.spacing.xs};
`;

const GenreTag = styled.span`
  font-size: ${theme.font.size.xs};
  font-weight: ${theme.font.weight.medium};
  background: rgba(248, 68, 100, 0.12);
  color: ${theme.colors.accent};
  border: 1px solid rgba(248, 68, 100, 0.25);
  border-radius: ${theme.radius.badge};
  padding: 2px 8px;
`;

const MetaRow = styled.div`
  display: flex;
  align-items: center;
  gap: ${theme.spacing.sm};
  flex-wrap: wrap;
  margin-top: auto;
`;

const MetaBadge = styled.span`
  font-size: ${theme.font.size.xs};
  background: ${theme.colors.surface};
  color: ${theme.colors.textSecondary};
  border: 1px solid ${theme.colors.surfaceBorder};
  border-radius: ${theme.radius.badge};
  padding: 2px 8px;
`;

const MetaText = styled.span`
  font-size: ${theme.font.size.xs};
  color: ${theme.colors.textMuted};
`;

const CbfcBadge = styled.span`
  font-size: ${theme.font.size.xs};
  font-weight: ${theme.font.weight.semibold};
  background: ${theme.colors.warning};
  color: #000;
  border-radius: ${theme.radius.badge};
  padding: 2px 8px;
  margin-left: auto;
`;

const CardFooter = styled.div`
  padding: ${theme.spacing.md} ${theme.spacing.lg};
  border-top: 1px solid ${theme.colors.surfaceBorder};
`;

const BookBtn = styled.span`
  font-size: ${theme.font.size.sm};
  font-weight: ${theme.font.weight.semibold};
  color: ${theme.colors.accent};
  transition: color ${theme.transition.fast};

  ${MovieCard}:hover & {
    color: ${theme.colors.accentHover};
  }
`;

const EmptyState = styled.p`
  text-align: center;
  color: ${theme.colors.textMuted};
  font-size: ${theme.font.size.lg};
  padding: ${theme.spacing.xxxl} 0;
`;

export default MovieListPage;
