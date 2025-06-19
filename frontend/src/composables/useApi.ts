// src/composables/useApi.ts
import axios from 'axios';

export const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor per loggare o gestire errori
api.interceptors.response.use(
    response => response,
    error => {
        console.error('[Axios Error]', error);
        return Promise.reject(error);
    }
);
