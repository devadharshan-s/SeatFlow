// src/pages/Shows/ShowBookingPage.tsx
import React, { useEffect, useState, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import styled, { keyframes } from 'styled-components';
import { getShowSeats, lockSeats } from '../../api/show';
import { SeatAvailabilityResponse } from '../../api/types';
import { theme } from '../../styles/theme';
import PageWrapper from '../../components/layout/PageWrapper';
import SkeletonCard from '../../components/common/SkeletonCard';
import ErrorCard from '../../components/common/ErrorCard';

const LOCK_DURATION_SECONDS = 300;

const ShowBookingPage: React.FC = () => {

    const { showId } = useParams<{ showId: string }>();
    const navigate = useNavigate();

    const [seats, setSeats] = useState<SeatAvailabilityResponse[]>([]);
    const [selectedSeats, setSelectedSeats] = useState<number[]>([]);
    const [lockedSeatIds, setLockedSeatIds] = useState<number[]>([]);
    const [lockTimer, setLockTimer] = useState<number>(0);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [locking, setLocking] = useState<boolean>(false);

    const fetchSeats = useCallback(async () => {
        if (!showId) return;
        setLoading(true);
        setError(null);
        try {
            const data = await getShowSeats(showId);
            setSeats(data.data ?? data ?? []);
        } catch {
            setError('Failed to fetch seats. Please try again.');
        } finally {
            setLoading(false);
        }
    }, [showId]);

    useEffect(() => {
        fetchSeats();
    }, [fetchSeats]);

    useEffect(() => {
        let timer: ReturnType<typeof setInterval>;
        if (lockedSeatIds.length > 0 && lockTimer > 0) {
            timer = setInterval(() => setLockTimer((prev) => prev - 1), 1000);
        } else if (lockTimer === 0 && lockedSeatIds.length > 0) {
            setLockedSeatIds([]);
            setSelectedSeats([]);
            fetchSeats();
        }
        return () => clearInterval(timer);
    }, [lockedSeatIds, lockTimer, fetchSeats]);

    const handleSeatSelect = (seatId: number, status: string, booked: boolean) => {
        if (lockedSeatIds.length > 0) return;
        if (status === 'AVAILABLE' && !booked) {
            setSelectedSeats((prev) =>
                prev.includes(seatId) ? prev.filter((id) => id !== seatId) : [...prev, seatId]
            );
        }
    };

    const handleLockSeats = async () => {
        if (!showId || selectedSeats.length === 0) return;
        setLocking(true);
        try {
            await lockSeats(showId, selectedSeats.map(String), LOCK_DURATION_SECONDS);
            setLockedSeatIds(selectedSeats);
            setLockTimer(LOCK_DURATION_SECONDS);
            fetchSeats();
        } catch {
            setError('Failed to lock seats — they may have been taken. Please re-select.');
            fetchSeats();
        } finally {
            setLocking(false);
        }
    };

    const handleProceedToPayment = () => {
        if (lockedSeatIds.length === 0 || lockTimer === 0) return;
        navigate('/payments/status', {
            state: { showId, lockedSeatIds, totalAmount: calculateTotalAmount() },
        });
    };

    const calculateTotalAmount = () =>
        selectedSeats.reduce((total, seatId) => {
            const seat = seats.find((s) => s.seatId === seatId);
            return total + (seat ? seat.price : 0);
        }, 0);

    const seatLayout = seats.reduce((acc, seat) => {
        if (!acc[seat.rowNumber]) acc[seat.rowNumber] = [];
        acc[seat.rowNumber].push(seat);
        return acc;
    }, {} as Record<string, SeatAvailabilityResponse[]>);

    const timerProgress = lockedSeatIds.length > 0 ? (lockTimer / LOCK_DURATION_SECONDS) * 100 : 100;
    const timerMin = Math.floor(lockTimer / 60).toString().padStart(2, '0');
    const timerSec = (lockTimer % 60).toString().padStart(2, '0');

    if (loading) {
        return (
            <PageWrapper>
                <SkeletonCard height="60px" />
                <div style={{ marginTop: theme.spacing.xl }}>
                    <SkeletonCard height="400px" />
                </div>
            </PageWrapper>
        );
    }

    if (error && seats.length === 0) {
        return (
            <PageWrapper>
                <ErrorCard message={error} onRetry={fetchSeats} />
            </PageWrapper>
        );
    }

    return (
        <PageWrapper>
            <PageTitle>Select Your Seats</PageTitle>
            <ShowMeta>Show #{showId}</ShowMeta>

            {/* Lock timer banner */}
            {lockedSeatIds.length > 0 && lockTimer > 0 && (
                <TimerBanner id="lock-timer-banner">
                    <TimerLeft>
                        <TimerIcon>⏱</TimerIcon>
                        <TimerText>Seats locked — complete payment within</TimerText>
                    </TimerLeft>
                    <TimerCount>{timerMin}:{timerSec}</TimerCount>
                    <TimerBarWrapper>
                        <TimerBar $progress={timerProgress} $urgent={lockTimer < 60} />
                    </TimerBarWrapper>
                </TimerBanner>
            )}

            {error && <InlineError>{error}</InlineError>}

            {/* Screen indicator */}
            <ScreenIndicator>
                <ScreenLabel>SCREEN THIS WAY</ScreenLabel>
            </ScreenIndicator>

            {/* Seat grid */}
            <SeatGridContainer>
                {Object.entries(seatLayout)
                    .sort(([a], [b]) => a.localeCompare(b))
                    .map(([rowNumber, rowSeats]) => (
                        <Row key={rowNumber}>
                            <RowLabel>{rowNumber}</RowLabel>
                            <SeatRow>
                                {rowSeats
                                    .sort((a, b) => a.seatNumber - b.seatNumber)
                                    .map((seat) => {
                                        const isSelected = selectedSeats.includes(seat.seatId);
                                        const isBooked = seat.booked || seat.status === 'BOOKED';
                                        const isLockedByMe = lockedSeatIds.includes(seat.seatId);
                                        const isLockedByOthers = seat.status === 'LOCKED' && !isLockedByMe;

                                        let state: 'available' | 'selected' | 'locked-me' | 'locked-others' | 'booked' = 'available';
                                        if (isBooked) state = 'booked';
                                        else if (isLockedByOthers) state = 'locked-others';
                                        else if (isLockedByMe) state = 'locked-me';
                                        else if (isSelected) state = 'selected';

                                        return (
                                            <Seat
                                                key={seat.seatId}
                                                id={`seat-${seat.seatId}`}
                                                $state={state}
                                                disabled={isBooked || isLockedByOthers}
                                                onClick={() => handleSeatSelect(seat.seatId, seat.status, seat.booked)}
                                                title={`Row ${rowNumber}, Seat ${seat.seatNumber} — ₹${seat.price} (${state})`}
                                            >
                                                {seat.seatNumber}
                                            </Seat>
                                        );
                                    })}
                            </SeatRow>
                        </Row>
                    ))}
            </SeatGridContainer>

            {/* Legend */}
            <Legend>
                <LegendItem><LegendDot $color={theme.colors.seatAvailableBorder} />Available</LegendItem>
                <LegendItem><LegendDot $color={theme.colors.seatSelectedBorder} />Selected</LegendItem>
                <LegendItem><LegendDot $color={theme.colors.seatLockedBorder} />Locked</LegendItem>
                <LegendItem><LegendDot $color={theme.colors.seatBookedBorder} />Booked</LegendItem>
            </Legend>

            {/* Sticky booking summary */}
            <SummaryBar id="booking-summary-bar">
                <SummaryInfo>
                    {lockedSeatIds.length > 0 ? (
                        <>
                            <SummaryCount>{lockedSeatIds.length} seat{lockedSeatIds.length > 1 ? 's' : ''} locked</SummaryCount>
                            <SummaryAmount>₹{calculateTotalAmount().toFixed(2)}</SummaryAmount>
                        </>
                    ) : selectedSeats.length > 0 ? (
                        <>
                            <SummaryCount>{selectedSeats.length} seat{selectedSeats.length > 1 ? 's' : ''} selected</SummaryCount>
                            <SummaryAmount>₹{calculateTotalAmount().toFixed(2)}</SummaryAmount>
                        </>
                    ) : (
                        <SummaryHint>Select seats to continue</SummaryHint>
                    )}
                </SummaryInfo>
                <SummaryActions>
                    <LockBtn
                        id="lock-seats-btn"
                        onClick={handleLockSeats}
                        disabled={selectedSeats.length === 0 || lockedSeatIds.length > 0 || locking}
                    >
                        {locking ? 'Locking…' : lockedSeatIds.length > 0 ? '✓ Seats Locked' : 'Lock Seats'}
                    </LockBtn>
                    <PayBtn
                        id="proceed-to-payment-btn"
                        onClick={handleProceedToPayment}
                        disabled={lockedSeatIds.length === 0 || lockTimer === 0}
                    >
                        Proceed to Payment →
                    </PayBtn>
                </SummaryActions>
            </SummaryBar>
        </PageWrapper>
    );

};

const selectedPulse = keyframes`
  0%, 100% { box-shadow: 0 0 0 0 rgba(248, 68, 100, 0.4); }
  50%       { box-shadow: 0 0 0 4px rgba(248, 68, 100, 0); }
`;

const PageTitle = styled.h1`
  font-size: ${theme.font.size.xxxl};
  font-weight: ${theme.font.weight.bold};
  color: ${theme.colors.textPrimary};
  margin-bottom: ${theme.spacing.xs};
`;

const ShowMeta = styled.p`
  font-size: ${theme.font.size.sm};
  color: ${theme.colors.textMuted};
  margin-bottom: ${theme.spacing.xl};
`;

const TimerBanner = styled.div`
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: ${theme.spacing.lg};
  background: ${theme.colors.surfaceElevated};
  border: 1px solid ${theme.colors.accent};
  border-radius: ${theme.radius.card};
  padding: ${theme.spacing.lg} ${theme.spacing.xl};
  margin-bottom: ${theme.spacing.xl};
`;

const TimerLeft = styled.div`
  display: flex;
  align-items: center;
  gap: ${theme.spacing.sm};
  flex: 1;
`;

const TimerIcon = styled.span`font-size: 1.2rem;`;

const TimerText = styled.span`
  font-size: ${theme.font.size.sm};
  color: ${theme.colors.textSecondary};
`;

const TimerCount = styled.span`
  font-size: ${theme.font.size.xxl};
  font-weight: ${theme.font.weight.bold};
  color: ${theme.colors.accent};
  font-variant-numeric: tabular-nums;
  min-width: 70px;
  text-align: right;
`;

const TimerBarWrapper = styled.div`
  width: 100%;
  height: 4px;
  background: ${theme.colors.surfaceBorder};
  border-radius: ${theme.radius.full};
  overflow: hidden;
`;

const TimerBar = styled.div<{ $progress: number; $urgent: boolean }>`
  height: 100%;
  width: ${({ $progress }) => $progress}%;
  background: ${({ $urgent }) => ($urgent ? theme.colors.error : theme.colors.accent)};
  border-radius: ${theme.radius.full};
  transition: width 1s linear, background ${theme.transition.base};
`;

const InlineError = styled.p`
  color: ${theme.colors.error};
  font-size: ${theme.font.size.sm};
  margin-bottom: ${theme.spacing.lg};
  padding: ${theme.spacing.md};
  background: rgba(229, 9, 20, 0.08);
  border: 1px solid rgba(229, 9, 20, 0.3);
  border-radius: ${theme.radius.button};
`;

const ScreenIndicator = styled.div`
  text-align: center;
  padding: ${theme.spacing.lg} 0 ${theme.spacing.xl};
  position: relative;

  &::after {
    content: '';
    display: block;
    height: 4px;
    background: linear-gradient(90deg, transparent, ${theme.colors.accent}66, transparent);
    border-radius: ${theme.radius.full};
    margin-top: ${theme.spacing.sm};
  }
`;

const ScreenLabel = styled.span`
  font-size: ${theme.font.size.xs};
  font-weight: ${theme.font.weight.semibold};
  letter-spacing: 3px;
  text-transform: uppercase;
  color: ${theme.colors.textMuted};
`;

const SeatGridContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${theme.spacing.sm};
  margin-bottom: ${theme.spacing.xl};
  overflow-x: auto;
  padding-bottom: ${theme.spacing.sm};
`;

const Row = styled.div`
  display: flex;
  align-items: center;
  gap: ${theme.spacing.sm};
`;

const RowLabel = styled.span`
  font-size: ${theme.font.size.xs};
  font-weight: ${theme.font.weight.semibold};
  color: ${theme.colors.textMuted};
  min-width: 24px;
  text-align: right;
`;

const SeatRow = styled.div`
  display: flex;
  gap: ${theme.spacing.xs};
  flex-wrap: nowrap;
`;

type SeatState = 'available' | 'selected' | 'locked-me' | 'locked-others' | 'booked';

const seatColors: Record<SeatState, { bg: string; border: string }> = {
    available: { bg: theme.colors.seatAvailableBg, border: theme.colors.seatAvailableBorder },
    selected: { bg: theme.colors.seatSelectedBg, border: theme.colors.seatSelectedBorder },
    'locked-me': { bg: theme.colors.seatSelectedBg, border: theme.colors.seatSelectedBorder },
    'locked-others': { bg: theme.colors.seatLockedBg, border: theme.colors.seatLockedBorder },
    booked: { bg: theme.colors.seatBookedBg, border: theme.colors.seatBookedBorder },
};

const Seat = styled.button<{ $state: SeatState }>`
  width: 36px;
  height: 36px;
  border-radius: ${theme.radius.seat};
  font-size: ${theme.font.size.xs};
  font-weight: ${theme.font.weight.semibold};
  color: ${theme.colors.textSecondary};
  background: ${({ $state }) => seatColors[$state].bg};
  border: 1px solid ${({ $state }) => seatColors[$state].border};
  transition: transform ${theme.transition.fast}, background ${theme.transition.fast};
  cursor: pointer;

  &:not(:disabled):hover {
    transform: scale(1.1);
    color: ${theme.colors.textPrimary};
  }

  &:disabled {
    cursor: not-allowed;
    opacity: 0.6;
  }

  ${({ $state }) =>
      $state === 'selected' &&
      `animation: ${selectedPulse} 1.8s ease infinite;`}

  &:focus-visible {
    outline: 2px solid ${theme.colors.accent};
    outline-offset: 2px;
  }
`;

const Legend = styled.div`
  display: flex;
  gap: ${theme.spacing.xl};
  flex-wrap: wrap;
  margin-bottom: 100px; /* space for sticky bar */
`;

const LegendItem = styled.div`
  display: flex;
  align-items: center;
  gap: ${theme.spacing.sm};
  font-size: ${theme.font.size.sm};
  color: ${theme.colors.textSecondary};
`;

const LegendDot = styled.span<{ $color: string }>`
  width: 14px;
  height: 14px;
  border-radius: ${theme.radius.seat};
  background: transparent;
  border: 2px solid ${({ $color }) => $color};
  display: inline-block;
`;

const SummaryBar = styled.div`
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 50;
  background: ${theme.colors.surface};
  border-top: 1px solid ${theme.colors.surfaceBorder};
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: ${theme.spacing.lg} ${theme.spacing.xxl};
  gap: ${theme.spacing.xl};
  flex-wrap: wrap;
`;

const SummaryInfo = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2px;
`;

const SummaryCount = styled.span`
  font-size: ${theme.font.size.md};
  font-weight: ${theme.font.weight.semibold};
  color: ${theme.colors.textPrimary};
`;

const SummaryAmount = styled.span`
  font-size: ${theme.font.size.xl};
  font-weight: ${theme.font.weight.bold};
  color: ${theme.colors.accent};
`;

const SummaryHint = styled.span`
  font-size: ${theme.font.size.md};
  color: ${theme.colors.textMuted};
`;

const SummaryActions = styled.div`
  display: flex;
  gap: ${theme.spacing.lg};
  align-items: center;
`;

const LockBtn = styled.button`
  padding: 10px 24px;
  background: transparent;
  color: ${theme.colors.textPrimary};
  font-size: ${theme.font.size.md};
  font-weight: ${theme.font.weight.semibold};
  border: 1px solid ${theme.colors.surfaceBorder};
  border-radius: ${theme.radius.button};
  transition: border-color ${theme.transition.fast}, color ${theme.transition.fast};

  &:hover:not(:disabled) {
    border-color: ${theme.colors.accent};
    color: ${theme.colors.accent};
  }

  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }

  &:focus-visible {
    outline: 2px solid ${theme.colors.accent};
    outline-offset: 2px;
  }
`;

const PayBtn = styled.button`
  padding: 10px 28px;
  background: ${theme.colors.accent};
  color: ${theme.colors.textPrimary};
  font-size: ${theme.font.size.md};
  font-weight: ${theme.font.weight.semibold};
  border-radius: ${theme.radius.button};
  transition: background ${theme.transition.fast}, box-shadow ${theme.transition.fast}, transform ${theme.transition.fast};

  &:hover:not(:disabled) {
    background: ${theme.colors.accentHover};
    box-shadow: ${theme.shadow.button};
    transform: translateY(-1px);
  }

  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
    transform: none;
  }

  &:focus-visible {
    outline: 2px solid ${theme.colors.accent};
    outline-offset: 2px;
  }
`;

export default ShowBookingPage;
