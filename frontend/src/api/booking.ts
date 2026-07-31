import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'; // Configurable via .env (VITE_API_BASE_URL)

const bookingApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

bookingApi.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwt_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const bookTickets = async (showId: string, seatIds: string[], userId: string, bookingToken?: string): Promise<any> => {
  try {
    const numUserId = !isNaN(Number(userId)) && userId !== '' ? Number(userId) : null;
    const response = await bookingApi.post('/bookmyshow-booking-service/bookTickets', {
      showId: Number(showId),
      seatIds: seatIds.map(Number),
      userId: numUserId,
      bookingToken
    });
    return response.data; // Expecting TicketResponseDTO
  } catch (error) {
    console.error('Error booking tickets:', error);
    throw error;
  }
};

export const deleteBooking = async (ticketId: string): Promise<any> => {
  try {
    const response = await bookingApi.delete(`/bookmyshow-booking-service/deleteBooking?ticketId=${ticketId}`);
    return response.data; // Expecting confirmation of deletion
  } catch (error) {
    console.error(`Error deleting booking with ticket ID ${ticketId}:`, error);
    throw error;
  }
};

export const validateTicket = async (ticketId: string): Promise<any> => {
  try {
    const response = await bookingApi.get(`/bookmyshow-booking-service/validateTicket/${ticketId}`);
    return response.data; // Expecting ticket validation status
  } catch (error) {
    console.error(`Error validating ticket with ID ${ticketId}:`, error);
    throw error;
  }
};
