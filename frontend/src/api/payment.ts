import axios from 'axios';
import { CreatePaymentRequest, PaymentResponse } from './types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'; // Configurable via .env (VITE_API_BASE_URL)

const paymentApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

paymentApi.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwt_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const createPaymentIntent = async (request: CreatePaymentRequest): Promise<PaymentResponse> => {
  try {
    const response = await paymentApi.post('/payment/payments/intent', request);
    return response.data;
  } catch (error) {
    console.error('Error creating payment intent:', error);
    throw error;
  }
};
