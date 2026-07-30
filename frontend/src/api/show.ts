import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'; // Configurable via .env (VITE_API_BASE_URL)

const showApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

showApi.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwt_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const getAllShows = async (page: number = 0, size: number = 10): Promise<any> => {
  try {
    const response = await showApi.get(`/show/getAllShows?page=${page}&size=${size}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching all shows:', error);
    throw error;
  }
};

export const getShowById = async (showId: string): Promise<any> => {
  try {
    const response = await showApi.get(`/show/getShowById?showId=${showId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching show with ID ${showId}:`, error);
    throw error;
  }
};

export const getShowSeats = async (showId: string, status: string = 'ALL'): Promise<any> => {
  try {
    const response = await showApi.get(`/show-seat/getShowSeats/${showId}?status=${status}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching show seats for show ID ${showId}:`, error);
    throw error;
  }
};

export const lockSeats = async (showId: string, seatIds: string[], seconds: number): Promise<any> => {
  try {
    const response = await showApi.post(`/show-seat/lockSeats/${seconds}`, seatIds.map(Number));
    return response.data;
  } catch (error) {
    console.error(`Error locking seats for show ID ${showId}:`, error);
    throw error;
  }
};

export const unlockSeats = async (ticketId: string, seatIds: number[]): Promise<any> => {
  try {
    const response = await showApi.post(`/show-seat/unlockSeats/${ticketId}`, seatIds);
    return response.data;
  } catch (error) {
    console.error(`Error unlocking seats for ticket ID ${ticketId}:`, error);
    throw error;
  }
};

export const resolveShowSeatIds = async (showId: string, seatIds: number[]): Promise<any> => {
  try {
    const response = await showApi.post(`/show-seat/shows/${showId}/resolve-seat-ids`, seatIds);
    return response.data;
  } catch (error) {
    console.error(`Error resolving show seat IDs for show ID ${showId}:`, error);
    throw error;
  }
};
