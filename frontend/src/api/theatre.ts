import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const theatreApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const getAllTheatres = async (): Promise<any> => {
  try {
    const response = await theatreApi.get('/theatre/getAllTheatres');
    return response.data;
  } catch (error) {
    console.error('Error fetching all theatres:', error);
    throw error;
  }
};

export const getTheatreById = async (theatreId: string | number): Promise<any> => {
  try {
    const response = await theatreApi.get(`/theatre/getTheatre/${theatreId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching theatre with ID ${theatreId}:`, error);
    throw error;
  }
};
