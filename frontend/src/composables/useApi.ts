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
api.interceptors.response.use(
    response => response,
    async error => {
        // Accedi allo store e al router solo all'interno dell'interceptor o di una funzione che verrà eseguita
        // in un contesto Vue (come una composable o un componente), altrimenti potresti avere problemi di "outside setup"
        // Una soluzione più pulita per gli interceptor globali è iniettare lo store tramite una closure o hook specifico.
        // Per semplicità qui simuliamo l'accesso, ma in un'app reale potresti doverlo strutturare diversamente
        // per evitare errori se l'interceptor viene caricato prima dell'app Pinia/Router.
        // Ad esempio, potresti avere una funzione initApi che prende lo store e il router.

        const authStore = useAuthStore(); // Ottieni l'istanza dello store
        const router = useRouter();       // Ottieni l'istanza del router

        console.error('[Axios Error]', error);

        // Gestione specifica per errori 401 Unauthorized
        if (error.response && error.response.status === 401) {
            console.warn('Authentication error: 401 Unauthorized. Logging out user.');

            // Effettua il logout forzato tramite Pinia
            await authStore.logout(); // Il metodo logout dovrebbe già ripulire token e reindirizzare

            // Mostra un toast all'utente
            const toast = await toastController.create({
                message: 'Sessione scaduta o non autorizzata. Effettua nuovamente il login.',
                duration: 3000,
                color: 'warning'
            });
            toast.present();

            // Reindirizza l'utente alla pagina di login (se non già fatto da authStore.logout)
            if (router.currentRoute.value.name !== 'login') { // Evita loop di reindirizzamento
                router.push({ name: 'login' });
            }
        }

        return Promise.reject(error);
    }
);