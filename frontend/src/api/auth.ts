import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'; // Configurable via .env (VITE_API_BASE_URL)

const authApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const loginUser = async (username: string, password: string): Promise<any> => {
  try {
    const response = await authApi.post('/login', { username, password }); // Assuming /login for Spring Security
    return response.data; // Expecting JWT token and user info
  } catch (error) {
    console.error('Login failed:', error);
    throw error;
  }
};

export const registerUser = async (username: string, password: string): Promise<any> => {
  try {
    const response = await authApi.post('/createUser', { username, password }); // Public endpoint from SecurityConfig
    return response.data; // Expecting user creation confirmation
  } catch (error) {
    console.error('Registration failed:', error);
    throw error;
  }
};
