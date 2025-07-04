// src/composables/useApi.ts
import axios from 'axios';
import { useAuthStore } from '@/store/auth'; // Importa lo store Pinia
import { useRouter } from 'vue-router';     // Importa il router di Vue
import { toastController } from '@ionic/vue'; // Se vuoi mostrare toast di errore

export const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor per loggare o gestire errori e soprattutto l'autenticazione
api.interceptors.request.use(
    async (config) => {
        console.log(`[Axios] Sending request to: ${config.url}`);

        try {
            const { value } = await SecureStorage.get({ key: 'authToken' });
            if (value) {
                config.headers.Authorization = `Bearer ${value}`;
                console.log(`[Axios] Added auth token to request (${value.substring(0, 10)}...)`);
            } else {
                console.log('[Axios] No auth token available');
            }
        } catch (error) {
            console.error('[Axios] SecureStorage error:', error);
        }

        return config;
    },
    (error) => {
        console.error('[Axios] Request error:', error);
        return Promise.reject(error);
    }
);

// Response Interceptor con logging
api.interceptors.response.use(
    (response) => {
        console.log(`[Axios] Response from ${response.config.url}: ${response.status}`);
        return response;
    },
    async (error) => {
        const originalRequest = error.config;
        const status = error.response?.status;

        console.error(
            `[Axios] API error: ${error.message}`,
            `URL: ${originalRequest.url}`,
            `Status: ${status}`
        );

        if (status === 401 || status === 403) {
            console.warn(`[Axios] Authentication error (${status}). Triggering logout`);

            const authStore = useAuthStore();
            const router = useRouter();

            // Evita loop di logout
            if (!originalRequest._retry) {
                originalRequest._retry = true;
                await authStore.logout();
            }

            const toast = await toastController.create({
                message: 'Sessione scaduta o non autorizzata',
                duration: 3000,
                color: 'warning'
            });
            await toast.present();

            if (router.currentRoute.value.name !== 'login') {
                console.log('[Axios] Redirecting to login page');
                router.push({ name: 'login' });
            }
        }

        return Promise.reject(error);
    }
);