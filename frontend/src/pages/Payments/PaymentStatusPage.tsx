// src/pages/Payments/PaymentStatusPage.tsx
import React, { useEffect, useState, useCallback, useRef } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { bookTickets, deleteBooking, confirmBooking } from '../../api/booking';
import { createPaymentIntent } from '../../api/payment';
import { useAuth } from '../../contexts/AuthContext';
import { theme } from '../../styles/theme';
import SkeletonCard from '../../components/common/SkeletonCard';

interface LocationState {
    showId: string;
    lockedSeatIds: number[];
    totalAmount: number;
    bookingToken?: string;
}

type Stage = 'idle' | 'booking' | 'paying' | 'success' | 'booking-error' | 'payment-error';

interface BookingResult {
    bookingToken: string;
    ticketId: string | number;
    showSeatIds: number[];
    amountPaid: number;
}

interface PaymentResult {
    paymentId: number;
    status: string;
    clientSecret: string;
    returnUrl: string;
}

const PaymentStatusPage: React.FC = () => {

    const location = useLocation();
    const navigate = useNavigate();
    const { user } = useAuth();

    const state = location.state as LocationState | null;

    const [stage, setStage] = useState<Stage>('idle');
    const [bookingResult, setBookingResult] = useState<BookingResult | null>(null);
    const [paymentResult, setPaymentResult] = useState<PaymentResult | null>(null);
    const [errorMsg, setErrorMsg] = useState<string>('');
    const bookingStarted = useRef(false);

    const runBookingAndPayment = useCallback(async () => {
        if (!state) return;

        const userId = user?.email ?? user?.username ?? 'guest';

        // Step 1 — book seats
        setStage('booking');
        let ticketData: BookingResult;
        try {
            const bookingResponse = await bookTickets(
                state.showId,
                state.lockedSeatIds.map(String),
                userId,
                state.bookingToken,
                state.totalAmount
            );
            // bookTickets returns ApiResponse<TicketDTO>; drill into .data if wrapped
            const dto = bookingResponse?.data ?? bookingResponse;
            ticketData = {
                bookingToken: dto.bookingToken ?? state.bookingToken,
                ticketId: dto.ticketId,
                showSeatIds: dto.showSeatIds ?? state.lockedSeatIds,
                amountPaid: dto.amountPaid ?? state.totalAmount,
            };
            setBookingResult(ticketData);
        } catch (err: any) {
            const detail = err.response?.data?.message
                ? `${err.response.data.message}${err.response.data.data ? ` (${JSON.stringify(err.response.data.data)})` : ''}`
                : (err.message || 'Booking failed. Your seats may have been released.');
            setErrorMsg(detail);
            setStage('booking-error');
            return;
        }

        // Step 2 — create payment intent
        setStage('paying');
        try {
            const paymentResponse = await createPaymentIntent({
                bookingToken: ticketData.bookingToken,
                amount: ticketData.amountPaid,
                currency: 'INR',
            });
            const pd: any = (paymentResponse as any)?.data ?? paymentResponse;
            setPaymentResult({
                paymentId: pd.paymentId,
                status: pd.status,
                clientSecret: pd.clientSecret,
                returnUrl: pd.returnUrl,
            });

            // Auto-confirm the booking on backend if in mock mode
            if (pd.clientSecret && pd.clientSecret.startsWith('pi_mock_secret_')) {
                const confirmResponse = await confirmBooking(ticketData.bookingToken);
                const confirmedDto = confirmResponse?.data ?? confirmResponse;
                if (confirmedDto?.ticketId) {
                    ticketData = {
                        ...ticketData,
                        ticketId: confirmedDto.ticketId,
                        showSeatIds: confirmedDto.showSeatIds ?? ticketData.showSeatIds,
                        amountPaid: confirmedDto.amountPaid ?? ticketData.amountPaid,
                    };
                    setBookingResult(ticketData);
                }
                // In mock dev flow, reflect successful payment status on UI
                setPaymentResult({
                    paymentId: pd.paymentId,
                    status: 'SUCCESS',
                    clientSecret: pd.clientSecret,
                    returnUrl: pd.returnUrl,
                });
            }

            setStage('success');
        } catch (err: any) {
            const detail = err.response?.data?.message
                ? `${err.response.data.message}${err.response.data.data ? ` (${JSON.stringify(err.response.data.data)})` : ''}`
                : (err.message || 'Payment initiation failed.');
            setErrorMsg(detail);
            setStage('payment-error');
        }
    }, [state, user]);

    useEffect(() => {
        if (stage === 'idle' && !bookingStarted.current) {
            bookingStarted.current = true;
            runBookingAndPayment();
        }
    }, [stage, runBookingAndPayment]);

    const handleRelease = async () => {
        if (bookingResult?.ticketId) {
            try {
                await deleteBooking(String(bookingResult.ticketId));
            } catch {
                /* best-effort */
            }
        }
        navigate('/movies');
    };

    // Guard: no state passed
    if (!state) {
        return (
            <PageBg>
                <StatusCard>
                    <StatusIcon $type="error">✕</StatusIcon>
                    <StatusTitle>No booking data found</StatusTitle>
                    <StatusMsg>Please start your booking from the seat selection page.</StatusMsg>
                    <PrimaryBtn id="back-to-movies" onClick={() => navigate('/movies')}>
                        Browse Movies
                    </PrimaryBtn>
                </StatusCard>
            </PageBg>
        );
    }

    // Loading states
    if (stage === 'booking' || stage === 'paying') {
        return (
            <PageBg>
                <StatusCard>
                    <Spinner />
                    <StatusTitle style={{ marginTop: theme.spacing.xl }}>
                        {stage === 'booking' ? 'Confirming your seats…' : 'Processing payment…'}
                    </StatusTitle>
                    <StatusMsg>Please do not close this page.</StatusMsg>
                    <SkeletonCard height="60px" />
                    <SkeletonCard height="40px" />
                </StatusCard>
            </PageBg>
        );
    }

    // Booking failed
    if (stage === 'booking-error') {
        return (
            <PageBg>
                <StatusCard $type="error">
                    <StatusIcon $type="error">✕</StatusIcon>
                    <StatusTitle>Booking Failed</StatusTitle>
                    <StatusMsg>{errorMsg}</StatusMsg>
                    <ButtonRow>
                        <SecondaryBtn id="retry-booking" onClick={() => { setStage('idle'); runBookingAndPayment(); }}>
                            Try Again
                        </SecondaryBtn>
                        <PrimaryBtn id="back-to-shows" onClick={() => navigate(-1)}>
                            Back to Seats
                        </PrimaryBtn>
                    </ButtonRow>
                </StatusCard>
            </PageBg>
        );
    }

    // Payment failed (ticket created)
    if (stage === 'payment-error') {
        return (
            <PageBg>
                <StatusCard $type="error">
                    <StatusIcon $type="error">✕</StatusIcon>
                    <StatusTitle>Payment Failed</StatusTitle>
                    <StatusMsg>{errorMsg}</StatusMsg>
                    {bookingResult && (
                        <TicketIdNote>Ticket #{bookingResult.ticketId} was created but payment was not completed.</TicketIdNote>
                    )}
                    <ButtonRow>
                        <SecondaryBtn id="retry-payment" onClick={() => { setStage('paying'); runBookingAndPayment(); }}>
                            Retry Payment
                        </SecondaryBtn>
                        <GhostBtn id="release-seats" onClick={handleRelease}>
                            Release Seats
                        </GhostBtn>
                    </ButtonRow>
                </StatusCard>
            </PageBg>
        );
    }

    // Success
    if (stage === 'success' && bookingResult && paymentResult) {
        return (
            <PageBg>
                <StatusCard $type="success">
                    <StatusIcon $type="success">✓</StatusIcon>
                    <StatusTitle>Booking Confirmed!</StatusTitle>
                    <StatusMsg>Your seats are booked. Enjoy the show!</StatusMsg>

                    <SummaryTable>
                        <SummaryRow>
                            <SummaryKey>Ticket ID</SummaryKey>
                            <SummaryVal>#{bookingResult.ticketId}</SummaryVal>
                        </SummaryRow>
                        <SummaryRow>
                            <SummaryKey>Show ID</SummaryKey>
                            <SummaryVal>#{state.showId}</SummaryVal>
                        </SummaryRow>
                        <SummaryRow>
                            <SummaryKey>Seats</SummaryKey>
                            <SummaryVal>{bookingResult.showSeatIds.join(', ')}</SummaryVal>
                        </SummaryRow>
                        <SummaryRow>
                            <SummaryKey>Amount Paid</SummaryKey>
                            <SummaryVal>₹{bookingResult.amountPaid.toFixed(2)}</SummaryVal>
                        </SummaryRow>
                        <SummaryRow>
                            <SummaryKey>Payment ID</SummaryKey>
                            <SummaryVal>#{paymentResult.paymentId}</SummaryVal>
                        </SummaryRow>
                        <SummaryRow>
                            <SummaryKey>Payment Status</SummaryKey>
                            <SummaryVal>
                                <StatusBadge>{paymentResult.status}</StatusBadge>
                            </SummaryVal>
                        </SummaryRow>
                    </SummaryTable>

                    {/* Debug info for testing */}
                    {paymentResult.clientSecret && (
                        <DebugPanel>
                            <DebugTitle>🔧 Debug Info (dev only)</DebugTitle>
                            <DebugItem><strong>Client Secret:</strong> {paymentResult.clientSecret.substring(0, 30)}…</DebugItem>
                            {paymentResult.returnUrl && (
                                <DebugItem><strong>Return URL:</strong> {paymentResult.returnUrl}</DebugItem>
                            )}
                        </DebugPanel>
                    )}

                    <PrimaryBtn id="book-again" onClick={() => navigate('/movies')}>
                        Book Again →
                    </PrimaryBtn>
                </StatusCard>
            </PageBg>
        );
    }

    return null;

};

/* Spinner */
const spinKeyframes = `
  @keyframes spin { to { transform: rotate(360deg); } }
`;

const Spinner = styled.div`
  ${spinKeyframes}
  width: 48px;
  height: 48px;
  border: 3px solid ${theme.colors.surfaceBorder};
  border-top-color: ${theme.colors.accent};
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto;
`;

const PageBg = styled.div`
  min-height: 100vh;
  background: ${theme.colors.base};
  display: flex;
  align-items: center;
  justify-content: center;
  padding: ${theme.spacing.xl};
`;

const StatusCard = styled.div<{ $type?: 'success' | 'error' }>`
  background: ${theme.colors.surfaceElevated};
  border: 1px solid ${({ $type }) =>
      $type === 'success' ? theme.colors.success :
      $type === 'error'   ? theme.colors.error :
                            theme.colors.surfaceBorder};
  border-radius: ${theme.radius.card};
  padding: 48px 40px;
  width: 100%;
  max-width: 520px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: ${theme.spacing.lg};
  animation: fadeIn 300ms ease both;
`;

const StatusIcon = styled.div<{ $type: 'success' | 'error' }>`
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: ${({ $type }) =>
      $type === 'success' ? 'rgba(46, 204, 113, 0.12)' : 'rgba(229, 9, 20, 0.12)'};
  border: 2px solid ${({ $type }) =>
      $type === 'success' ? theme.colors.success : theme.colors.error};
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
  color: ${({ $type }) =>
      $type === 'success' ? theme.colors.success : theme.colors.error};
`;

const StatusTitle = styled.h1`
  font-size: ${theme.font.size.xxl};
  font-weight: ${theme.font.weight.bold};
  color: ${theme.colors.textPrimary};
`;

const StatusMsg = styled.p`
  font-size: ${theme.font.size.md};
  color: ${theme.colors.textSecondary};
`;

const SummaryTable = styled.div`
  width: 100%;
  background: ${theme.colors.surface};
  border: 1px solid ${theme.colors.surfaceBorder};
  border-radius: ${theme.radius.button};
  overflow: hidden;
  text-align: left;
  margin: ${theme.spacing.sm} 0;
`;

const SummaryRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: ${theme.spacing.md} ${theme.spacing.lg};
  border-bottom: 1px solid ${theme.colors.surfaceBorder};

  &:last-child {
    border-bottom: none;
  }
`;

const SummaryKey = styled.span`
  font-size: ${theme.font.size.sm};
  color: ${theme.colors.textMuted};
`;

const SummaryVal = styled.span`
  font-size: ${theme.font.size.sm};
  font-weight: ${theme.font.weight.semibold};
  color: ${theme.colors.textPrimary};
`;

const StatusBadge = styled.span`
  font-size: ${theme.font.size.xs};
  font-weight: ${theme.font.weight.bold};
  background: rgba(46, 204, 113, 0.15);
  color: ${theme.colors.success};
  border: 1px solid rgba(46, 204, 113, 0.3);
  border-radius: ${theme.radius.badge};
  padding: 2px 10px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
`;

const TicketIdNote = styled.p`
  font-size: ${theme.font.size.sm};
  color: ${theme.colors.textMuted};
  background: ${theme.colors.surface};
  border: 1px solid ${theme.colors.surfaceBorder};
  border-radius: ${theme.radius.button};
  padding: ${theme.spacing.md};
  width: 100%;
`;

const DebugPanel = styled.div`
  width: 100%;
  background: #0a0a0a;
  border: 1px solid #333;
  border-radius: ${theme.radius.button};
  padding: ${theme.spacing.lg};
  text-align: left;
`;

const DebugTitle = styled.p`
  font-size: ${theme.font.size.xs};
  color: ${theme.colors.textMuted};
  margin-bottom: ${theme.spacing.sm};
  font-weight: ${theme.font.weight.semibold};
`;

const DebugItem = styled.p`
  font-size: ${theme.font.size.xs};
  color: ${theme.colors.textMuted};
  font-family: monospace;
  word-break: break-all;
  margin-bottom: 4px;
`;

const ButtonRow = styled.div`
  display: flex;
  gap: ${theme.spacing.lg};
  flex-wrap: wrap;
  justify-content: center;
  width: 100%;
`;

const PrimaryBtn = styled.button`
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

const SecondaryBtn = styled.button`
  padding: 12px 24px;
  background: transparent;
  color: ${theme.colors.textPrimary};
  font-size: ${theme.font.size.md};
  font-weight: ${theme.font.weight.medium};
  border: 1px solid ${theme.colors.surfaceBorder};
  border-radius: ${theme.radius.button};
  transition: border-color ${theme.transition.fast};

  &:hover {
    border-color: ${theme.colors.accent};
    color: ${theme.colors.accent};
  }
`;

const GhostBtn = styled.button`
  padding: 12px 24px;
  background: transparent;
  color: ${theme.colors.textMuted};
  font-size: ${theme.font.size.md};
  border-radius: ${theme.radius.button};
  transition: color ${theme.transition.fast};

  &:hover {
    color: ${theme.colors.error};
  }
`;

export default PaymentStatusPage;
