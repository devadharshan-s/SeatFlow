// src/pages/Shows/ShowListPage.tsx
import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import styled from 'styled-components';
import { getAllShows } from '../../api/show';
import { getAllTheatres } from '../../api/theatre';
import { ShowResponseDTO } from '../../api/types';
import { theme } from '../../styles/theme';
import PageWrapper from '../../components/layout/PageWrapper';
import SkeletonCard from '../../components/common/SkeletonCard';
import ErrorCard from '../../components/common/ErrorCard';

interface GroupedShows {
    theatreId: number;
    theatreName: string;
    shows: ShowResponseDTO[];
}

const ShowListPage: React.FC = () => {

    const [searchParams] = useSearchParams();
    const movieId = searchParams.get('movieId');
    const navigate = useNavigate();

    const [groupedShows, setGroupedShows] = useState<GroupedShows[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    const fetchShows = async () => {
        setLoading(true);
        setError(null);
        try {
            const [showsData, theatresData] = await Promise.all([
                getAllShows(0, 100),
                getAllTheatres()
            ]);

            const allShows: ShowResponseDTO[] = showsData.data ?? showsData.content ?? showsData ?? [];
            const allTheatres = theatresData.data ?? theatresData ?? [];

            const theatreMap: Record<number, string> = {};
            allTheatres.forEach((t: any) => {
                theatreMap[t.theatreId] = t.name;
            });

            const filteredShows = movieId
                ? allShows.filter((s) => s.movieId === parseInt(movieId))
                : allShows;

            const groupedMap: Record<number, { name: string; shows: ShowResponseDTO[] }> = {};
            filteredShows.forEach((show) => {
                const tId = show.theatreId;
                const tName = theatreMap[tId] || `Theatre #${tId}`;
                if (!groupedMap[tId]) {
                    groupedMap[tId] = { name: tName, shows: [] };
                }
                groupedMap[tId].shows.push(show);
            });

            const groupedList = Object.keys(groupedMap).map((key) => {
                const tId = parseInt(key);
                return {
                    theatreId: tId,
                    theatreName: groupedMap[tId].name,
                    shows: groupedMap[tId].shows
                };
            });

            setGroupedShows(groupedList);
        } catch (err) {
            console.error('Error loading shows and theatres:', err);
            setError('Could not load shows. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchShows();
    }, [movieId]);

    const formatDate = (iso: string) =>
        new Date(iso).toLocaleDateString('en-IN', { weekday: 'short', day: 'numeric', month: 'short' });

    const formatTime = (iso: string) =>
        new Date(iso).toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit', hour12: true });

    return (
        <PageWrapper>
            <PageHeader>
                <PageTitle>{movieId ? 'Select a Showtime' : 'All Shows'}</PageTitle>
                <PageSubtitle>
                    {movieId
                        ? 'Choose your preferred date and time'
                        : 'Browse all available shows across theatres'}
                </PageSubtitle>
            </PageHeader>

            {loading && (
                <Grid>
                    {Array.from({ length: 6 }).map((_, i) => (
                        <SkeletonCard key={i} height="180px" />
                    ))}
                </Grid>
            )}

            {!loading && error && (
                <ErrorCard message={error} onRetry={fetchShows} />
            )}

            {!loading && !error && groupedShows.length === 0 && (
                <EmptyState>No shows available. Check back soon!</EmptyState>
            )}

            {!loading && !error && groupedShows.length > 0 && (
                <div style={{ display: 'flex', flexDirection: 'column', gap: theme.spacing.xl }}>
                    {groupedShows.map((group) => (
                        <TheatreSection key={group.theatreId}>
                            <TheatreHeader>
                                <TheatreName>{group.theatreName}</TheatreName>
                                <TheatreBadge>{group.shows.length} {group.shows.length === 1 ? 'Show' : 'Shows'}</TheatreBadge>
                            </TheatreHeader>
                            <Grid>
                                {group.shows.map((show) => (
                                    <ShowCard key={show.showId} id={`show-card-${show.showId}`}>
                                        <ShowDateBadge>{formatDate(show.startTime)}</ShowDateBadge>
                                        <ShowTime>{formatTime(show.startTime)}</ShowTime>
                                        <ShowDivider />
                                        <ShowMeta>
                                            <MetaItem>
                                                <MetaLabel>Screen</MetaLabel>
                                                <MetaVal>#{show.screenId}</MetaVal>
                                            </MetaItem>
                                            <MetaItem>
                                                <MetaLabel>Ends</MetaLabel>
                                                <MetaVal>{formatTime(show.endTime)}</MetaVal>
                                            </MetaItem>
                                        </ShowMeta>
                                        <ViewSeatsBtn
                                            id={`view-seats-${show.showId}`}
                                            onClick={() => navigate(`/shows/${show.showId}/book`)}
                                        >
                                            View Seats →
                                        </ViewSeatsBtn>
                                    </ShowCard>
                                ))}
                            </Grid>
                        </TheatreSection>
                    ))}
                </div>
            )}
        </PageWrapper>
    );

};

const TheatreSection = styled.div`
  background: ${theme.colors.surface};
  border: 1px solid ${theme.colors.surfaceBorder};
  border-radius: ${theme.radius.card};
  padding: ${theme.spacing.xl};
  display: flex;
  flex-direction: column;
  gap: ${theme.spacing.xl};
`;

const TheatreHeader = styled.div`
  display: flex;
  align-items: center;
  gap: ${theme.spacing.md};
  border-bottom: 1px solid ${theme.colors.surfaceBorder};
  padding-bottom: ${theme.spacing.md};
`;

const TheatreName = styled.h2`
  font-size: ${theme.font.size.xl};
  font-weight: ${theme.font.weight.bold};
  color: ${theme.colors.textPrimary};
  margin: 0;
`;

const TheatreBadge = styled.span`
  background: ${theme.colors.accent}20;
  color: ${theme.colors.accent};
  font-size: ${theme.font.size.xs};
  font-weight: ${theme.font.weight.bold};
  padding: 4px 10px;
  border-radius: ${theme.radius.badge};
  text-transform: uppercase;
  letter-spacing: 0.5px;
`;

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
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: ${theme.spacing.xl};
`;

const ShowCard = styled.div`
  background: ${theme.colors.surfaceElevated};
  border: 1px solid ${theme.colors.surfaceBorder};
  border-radius: ${theme.radius.card};
  padding: ${theme.spacing.xl};
  display: flex;
  flex-direction: column;
  gap: ${theme.spacing.md};
  transition: transform ${theme.transition.base}, border-color ${theme.transition.base}, box-shadow ${theme.transition.base};

  &:hover {
    transform: translateY(-4px);
    border-color: ${theme.colors.accent};
    box-shadow: ${theme.shadow.card};
  }
`;

const ShowDateBadge = styled.span`
  font-size: ${theme.font.size.xs};
  font-weight: ${theme.font.weight.semibold};
  text-transform: uppercase;
  letter-spacing: 1px;
  color: ${theme.colors.accent};
`;

const ShowTime = styled.p`
  font-size: ${theme.font.size.display};
  font-weight: ${theme.font.weight.bold};
  color: ${theme.colors.textPrimary};
  line-height: 1;
`;

const ShowDivider = styled.hr`
  border: none;
  border-top: 1px solid ${theme.colors.surfaceBorder};
  margin: ${theme.spacing.xs} 0;
`;

const ShowMeta = styled.div`
  display: flex;
  justify-content: space-between;
`;

const MetaItem = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2px;
`;

const MetaLabel = styled.span`
  font-size: ${theme.font.size.xs};
  color: ${theme.colors.textMuted};
  text-transform: uppercase;
  letter-spacing: 0.5px;
`;

const MetaVal = styled.span`
  font-size: ${theme.font.size.md};
  font-weight: ${theme.font.weight.semibold};
  color: ${theme.colors.textPrimary};
`;

const ViewSeatsBtn = styled.button`
  margin-top: auto;
  width: 100%;
  padding: 10px;
  background: transparent;
  color: ${theme.colors.accent};
  font-size: ${theme.font.size.md};
  font-weight: ${theme.font.weight.semibold};
  border: 1px solid ${theme.colors.accent};
  border-radius: ${theme.radius.button};
  transition: background ${theme.transition.fast}, color ${theme.transition.fast};

  &:hover {
    background: ${theme.colors.accent};
    color: ${theme.colors.textPrimary};
  }

  &:focus-visible {
    outline: 2px solid ${theme.colors.accent};
    outline-offset: 2px;
  }
`;

const EmptyState = styled.p`
  text-align: center;
  color: ${theme.colors.textMuted};
  font-size: ${theme.font.size.lg};
  padding: ${theme.spacing.xxxl} 0;
`;

export default ShowListPage;
