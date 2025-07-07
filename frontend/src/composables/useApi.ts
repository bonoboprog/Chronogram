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