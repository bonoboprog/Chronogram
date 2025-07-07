import axios from 'axios';
import { useAuthStore } from '@/store/auth';
import { useRouter } from 'vue-router';
import { toastController } from '@ionic/vue';

export const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Function to initialize interceptors with access to store/router
export function initApiInterceptors() {
    api.interceptors.response.use(
        response => response,
        async error => {
            const authStore = useAuthStore();
            const router = useRouter();

            console.error('[Axios Error]', error);

            // Gestione specifica per errori 401 Unauthorized
            if (error.response && error.response.status === 401) {
                console.warn('Authentication error: 401 Unauthorized. Logging out user.');
                await authStore.logout();

                const toast = await toastController.create({
                    message: 'Sessione scaduta o non autorizzata. Effettua nuovamente il login.',
                    duration: 3000,
                    color: 'warning'
                });
                toast.present();

                if (router.currentRoute.value.name !== 'login') {
                    router.push({ name: 'login' });
                }
            }

            return Promise.reject(error);
        }
    );
}