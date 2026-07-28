export interface ShowResponseDTO {
  showId: number;
  theatreId: number;
  screenId: number;
  movieId: number;
  startTime: string;
  endTime: string;
}

export interface SeatAvailabilityResponse {
  seatId: number;
  rowNumber: string;
  seatNumber: number;
  status: string;
  booked: boolean;
  lockedUntil: string | null;
  price: number;
  category: string;
}

export interface CreatePaymentRequest {
  ticketId: number;
  amount: number;
  currency: string;
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  SUCCEEDED = 'SUCCEEDED',
  FAILED = 'FAILED',
  CANCELED = 'CANCELED',
}

export interface PaymentResponse {
  paymentId: number;
  ticketId: number;
  amount: number;
  currency: string;
  status: PaymentStatus;
  stripePaymentIntentId: string;
  clientSecret: string;
  returnUrl: string;
}

export interface TicketDTO {
  showId: number;
  seatIds: number[];
  userId: string; // Assuming userId is a string for now, adjust if needed
}

export interface TicketResponseDTO {
  ticketId: string;
  showSeatIds: number[];
  showId: number;
  userId: string;
  amountPaid: number;
}