import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'; // Configurable via .env (VITE_API_BASE_URL)

const movieApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const getAllMovies = async (page: number = 0, size: number = 10): Promise<any> => {
  try {
    const response = await movieApi.get(`/movie/getAllMovies?page=${page}&size=${size}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching all movies:', error);
    throw error;
  }
};

export const getMovieById = async (movieId: string): Promise<any> => {
  try {
    const response = await movieApi.get(`/movie/getMovie/${movieId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching movie with ID ${movieId}:`, error);
    throw error;
  }
};
